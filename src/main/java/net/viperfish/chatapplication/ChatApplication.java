/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.util.HashMap;
import java.util.Map;
import net.viperfish.chatapplication.core.ChatWebSocket;
import net.viperfish.chatapplication.core.DefaultLSPayload;
import net.viperfish.chatapplication.core.DefaultLSRequest;
import net.viperfish.chatapplication.core.DefaultLSStatus;
import net.viperfish.chatapplication.core.JsonGenerator;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSStatus;
import net.viperfish.chatapplication.core.RequestHandler;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.UserRegister;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketListener;

/**
 *
 * @author sdai
 */
public class ChatApplication extends WebSocketApplication {

    private UserDatabase userDB;
    private final Map<Long, RequestHandler> handlerMapper;
    private final JsonGenerator generator;
    private  UserRegister socketMapper;

    public ChatApplication() {
        handlerMapper = new HashMap<>();
        generator = new JsonGenerator();
    }

    public void setUserDB(UserDatabase userDB) {
        this.userDB = userDB;
    }

    public void setSocketMapper(UserRegister mapper) {
        this.socketMapper = mapper;
    }

    public void addHandler(Long type, RequestHandler handler) {
        handlerMapper.put(type, handler);
    }

    @Override
    public WebSocket createSocket(ProtocolHandler handler, HttpRequestPacket requestPacket, WebSocketListener... listeners) {
        return new ChatWebSocket(super.createSocket(handler, requestPacket, listeners), null);
    }

    @Override
    public void onClose(WebSocket socket, DataFrame frame) {
        if(socket instanceof ChatWebSocket) {
            ChatWebSocket chatSocket = (ChatWebSocket) socket;
            if(chatSocket.getUser() != null) {
                socketMapper.unregister(chatSocket.getUser());
            }
        } else {
            throw new AssertionError("Websocket not Chatsocket");
        }
        super.onClose(socket, frame);
    }

    
    
    @Override
    public void onMessage(WebSocket socket, String text) {
        try {
            DefaultLSRequest req = generator.fromJson(DefaultLSRequest.class, text);
            req.setSocket(socket);
            if (req.getType() == null) {
                throw new RuntimeException();
            }

            RequestHandler handler = handlerMapper.get(req.getType());
            DefaultLSPayload payload = new DefaultLSPayload();
            LSStatus status = new DefaultLSStatus();
            if (handler != null) {
                status = handler.handleRequest(req, payload);
                if (payload.getTarget() != null) {
                    WebSocket targetSocket = socketMapper.getSocket(payload.getTarget());
                    if (targetSocket == null || !targetSocket.isConnected()) {
                        status.setStatus(LSStatus.USER_OFFLINE);
                    } else {
                        targetSocket.send(generator.toJson(payload));
                    }
                }
            } else {
                status.setStatus(LSStatus.NO_HANDLER);
            }
            DefaultLSPayload statusPayload = new DefaultLSPayload();
            statusPayload.setSource(null);
            if(socketMapper.getSocket(req.getSource()) != null) {
                statusPayload.setTarget(req.getSource());
            } else {
                statusPayload.setTarget(null);
            }
            statusPayload.setData(generator.toJson(status));
            statusPayload.setSource(null);
            statusPayload.setType(LSPayload.LS_STATUS);
            socket.send(generator.toJson(statusPayload));

        } catch (JsonParseException | JsonMappingException | JsonGenerationException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    

}
