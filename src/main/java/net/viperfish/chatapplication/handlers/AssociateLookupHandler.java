/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.viperfish.chatapplication.core.JsonGenerator;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.UserRegister;
import net.viperfish.chatapplication.core.ValidatedRequestHandler;

/**
 *
 * @author sdai
 */
public class AssociateLookupHandler extends ValidatedRequestHandler {

	private final UserDatabase db;
	private final UserRegister register;
	private final JsonGenerator jGenerator;
	private final Logger logger;

	public AssociateLookupHandler(UserDatabase userDb, UserRegister userRegister) {
		this.db = userDb;
		this.register = userRegister;
		jGenerator = new JsonGenerator();
		logger = LogManager.getLogger();
	}

	@Override
	public boolean validate(LSRequest req) {
		if (req.getAttribute("checkOnline") == null || req.getAttribute("checkOnline").length() == 0) {
			return false;
		}
		if (!req.getAttribute("checkOnline").equalsIgnoreCase("true")
				&& !req.getAttribute("checkOnline").equalsIgnoreCase("false")) {
			return false;
		}
		if (req.getSource() == null || req.getSource().length() == 0) {
			return false;
		}
		return true;
	}

	@Override
	public LSResponse wrappedHandleRequest(LSRequest req, LSPayload resp) {
		boolean onlineFilter = Boolean.parseBoolean(req.getAttribute("checkOnline"));
		logger.info("Online filter:" + onlineFilter);
		try {
			User target = db.findByUsername(req.getSource());
			if (target == null) {
				LSResponse response = new LSResponse();
				response.setStatus(LSResponse.USER_NOT_FOUND);
				return response;
			}
			Set<String> temp = new HashSet<>(db.findByUsername(req.getSource()).getAssociates());
			logger.info("Persisted Associates:" + Arrays.deepToString(temp.toArray()));
			Set<String> result;
			if (onlineFilter) {
				result = new HashSet<>();
				temp.stream().filter((u) -> (register.isOnline(u))).forEach((String u) -> {
					logger.info("Online User:" + u);
					result.add(u);
				});
			} else {
				result = temp;
			}
			LSResponse response = new LSResponse();
			response.setStatus(LSResponse.SUCCESS);
			response.setData(jGenerator.toJson(result));
			return response;
		} catch (JsonGenerationException | JsonMappingException ex) {
			LSResponse response = new LSResponse(LSResponse.INTERNAL_ERROR, ex.getMessage(), "");
			return response;
		}

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
