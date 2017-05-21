/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.viperfish.chatapplication.core.DefaultLSSession;
import net.viperfish.chatapplication.core.JsonGenerator;
import net.viperfish.chatapplication.core.LSFilter;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.RequestHandler;
import net.viperfish.chatapplication.core.UserRegister;

/**
 * The main request dispatcher that dispatches requests to specific handlers.
 * This class handles websocket packets, deserializes the JSON request in the
 * packet into {@link LSRequest}, and relay the request to its handler based on
 * the request type. This class is not designed for thread safety
 * 
 * @author sdai
 *
 */
public class ChatApplication extends WebSocketApplication {

	private final Map<Long, RequestHandler> handlerMapper;
	private final JsonGenerator generator;
	private final UserRegister socketMapper;
	private final Logger logger;
	private final DefaultFilterChain filterChain;

	/**
	 * creates a {@link ChatApplication} object.
	 */
	public ChatApplication(UserRegister reg) {
		handlerMapper = new HashMap<>();
		generator = new JsonGenerator();
		logger = LogManager.getLogger();
		filterChain = new DefaultFilterChain();
		socketMapper = reg;
	}

	/**
	 * registers a handler for a type of request.
	 * 
	 * @param type
	 *            the type of {@link LSRequest} that the handler can handles
	 * @param handler
	 *            the request handler
	 */
	public void addHandler(Long type, RequestHandler handler) {
		handlerMapper.put(type, handler);
	}

	/**
	 * converts an incoming websocket packet data in JSON into
	 * {@link LSRequest}.
	 * 
	 * @param data
	 *            the JSON request
	 * @param socket
	 *            the originating websocket
	 * @return a {@link LSRequest} that represents the incoming JSON packet
	 * @throws JsonParseException
	 *             if the incoming websocket packet is not proper JSON
	 * @throws JsonMappingException
	 *             if the incoming websocket packet fails to map to a
	 *             {@link LSRequest}
	 */
	private LSRequest convertToRequest(String data, WebSocket socket) throws JsonParseException, JsonMappingException {
		LSRequest req = generator.fromJson(LSRequest.class, data);
		req.setSocket(socket);
		return req;
	}

	/**
	 * sends the {@link LSPayload} from a handler to its target client as
	 * requested by the handler. This method sends the constructed
	 * {@link LSPayload} from a {@link RequestHandler} to another client if
	 * required (The {@link LSPayload} has a target). If the target is offline,
	 * this method changes the {@link LSResponse} status to 203 User Offline. If
	 * the target of the {@link LSPayload} is null, this method does nothings
	 *
	 * @param payload
	 *            the payload to sent if there is a target
	 * @param status
	 *            the status to change if the target is offline
	 * @throws JsonGenerationException
	 *             if failed to generate JSON payload from {@link LSPayload}
	 * @throws JsonMappingException
	 *             if failed to map values in {@link LSPayload} to JSON fields
	 */
	private void sendPayload(LSPayload payload, LSResponse status)
			throws JsonGenerationException, JsonMappingException {
		logger.info("sending payload to:" + payload.getTarget());
		// check if the handler requires to send a payload
		if (payload.getTarget() != null) {
			// get the websocket for the target
			WebSocket targetSocket = socketMapper.getSocket(payload.getTarget());
			logger.info("Websocket for:" + payload.getTarget() + "->" + targetSocket);
			if (targetSocket == null) {
				// set status to 203 if the target of the payload is offline
				status.setStatus(LSResponse.USER_OFFLINE, "Target User Offline");
			} else {
				// serializes the payload into JSON
				String sentData = generator.toJson(payload);
				logger.info("Sending:\n" + sentData);
				// send the serialized payload to the target client
				targetSocket.send(sentData);
			}
		}
	}

	/**
	 * dispatches a {@link LSRequest} to an appropriate {@link RequestHandler}
	 * based on its type.
	 * 
	 * @param req
	 *            the incoming request to process
	 * @return the response or result to the request. The status depends on the
	 *         handler. However, if no handler is found, the status is 202 No
	 *         Handler.
	 * @throws JsonGenerationException
	 *             if failed to generate JSON for the {@link LSPayload}
	 * @throws JsonMappingException
	 *             if failed to map {@link LSPayload} fields into JSON fields
	 */
	private LSResponse handleRequest(LSRequest req) throws JsonGenerationException, JsonMappingException {
		// set up the mathcing Requesthandler
		RequestHandler handler = handlerMapper.get(req.getType());
		LSResponse status = new LSResponse();
		List<LSPayload> resps = new LinkedList<>();
		if (handler != null) {
			// runs the request through the filter chain, and then process the
			// request with the handler
			filterChain.setEndpoint(handler);
			status = filterChain.process(req, resps);

			// send the payload to another client if any
			for (LSPayload payload : resps) {
				sendPayload(payload, status);
			}
		} else {
			logger.info("No Handler Present For Message Type:" + req.getType());
			status.setStatus(LSResponse.NO_HANDLER, "No Handler Found For Type" + req.getType());
		}
		return status;
	}

	/**
	 * sends the response of the handler back to the origin of the
	 * {@link LSRequest}.
	 * 
	 * @param statusPayload
	 *            the {@link LSPayload} that will contain the {@link LSResponse}
	 *            data
	 * @param status
	 *            the result of the handler to the origin client
	 * @param origin
	 *            the websocket of the source client
	 * @throws JsonGenerationException
	 *             if failed to generate JSON from the {@link LSPayload}
	 * @throws JsonMappingException
	 *             if failed to map {@link LSPayload} fields into JSON fields.
	 */
	private void sendStatus(LSPayload statusPayload, LSResponse status, WebSocket origin)
			throws JsonGenerationException, JsonMappingException {
		// load the LSResponse into the datagram body of the LSPayload
		statusPayload.setData(generator.toJson(status));
		// no source for this payload because it originates from server
		statusPayload.setSource(null);
		statusPayload.setType(LSPayload.LS_STATUS);

		String statusJSON = generator.toJson(statusPayload);
		logger.info("Sending Status Back:" + statusJSON);
		// sends the payload to the orinator
		origin.send(statusJSON);
	}

	/**
	 * handles an incoming websocket packet and send result and payload to
	 * clients.
	 * 
	 * @param socket
	 *            the originating socket of the request
	 * @param text
	 *            the string body of the websocket packet
	 */
	@Override
	public void onMessage(WebSocket socket, String text) {
		logger.info("Received Message:" + text);

		// initialize the result objects
		LSResponse status = new LSResponse();
		LSPayload statusPayload = new LSPayload();
		statusPayload.setSource(null);
		try {
			// turn the text request into request object
			LSRequest req = convertToRequest(text, socket);
			if (req.getType() == null) {
				logger.warn("Invalid Message:" + text);
				return;
			}

			// handles the request
			status = handleRequest(req);
		} catch (Exception ex) {
			// set the status to internal error if any exceptions occurred
			logger.warn("Exception Caught:" + ex);
			status.setStatus(LSResponse.INTERNAL_ERROR, ex.getMessage());
		} finally {
			try {
				sendStatus(statusPayload, status, socket);
			} catch (Exception ex) {
				logger.warn("Exception Caught While sending internal error status", ex);
				socket.send("{'status':204, 'reason':\"\", 'additional':\"\"}");
			}
		}
	}

	/**
	 * cleans up user data on socket close
	 * 
	 * @param socket
	 * @param frame
	 */
	@Override
	public void onClose(WebSocket socket, DataFrame frame) {
		logger.info("Closing Socket");
		String username = socketMapper.getUsername(socket);
		if (username != null) {
			// delete session data for this socket
			logger.info("Invalidating session for " + username);
			DefaultLSSession.getSession(username).invalidate();
		}
		socketMapper.unregister(socket);
		super.onClose(socket, frame);
	}

	/**
	 * adds a filter to the chat server
	 * 
	 * @param filter
	 *            the filter to add
	 */
	public void addFilter(LSFilter filter) {
		this.filterChain.addFilter(filter);
	}

}
