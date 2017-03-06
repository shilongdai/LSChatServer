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
import net.viperfish.chatapplication.core.ValidatedRequestHandler;

/**
 *
 * @author sdai
 */
public class SearchUserHandler extends ValidatedRequestHandler {

	private final UserDatabase userDB;
	private final JsonGenerator jGenerator;
	private final Logger logger;

	public SearchUserHandler(UserDatabase userDB) {
		this.userDB = userDB;
		jGenerator = new JsonGenerator();
		logger = LogManager.getLogger();
	}

	@Override
	public void init() {
	}

	@Override
	public boolean validate(LSRequest req) {
		if (req.getSource() == null || req.getSource().length() == 0) {
			return false;
		}
		if (req.getAttribute("keyword") == null || req.getAttribute("keyword").isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public LSResponse wrappedHandleRequest(LSRequest req, LSPayload resp) {
		String keyword = req.getAttribute("keyword");
		logger.info("Keyword:" + keyword);
		LSResponse response = new LSResponse();
		logger.info("Searching for:" + keyword);
		Set<String> result = new HashSet<>();
		for (User u : userDB.search(keyword)) {
			result.add(u.getUsername());
		}
		logger.info("Search Found:" + Arrays.toString(result.toArray()));
		try {
			String serialized = jGenerator.toJson(result);
			if (serialized.length() == 0) {
				response.setData("[ ]");
			} else {
				response.setData(serialized);
			}
			response.setStatus(LSResponse.SUCCESS);
			return response;
		} catch (JsonGenerationException | JsonMappingException ex) {
			response.setStatus(LSResponse.INTERNAL_ERROR);
			logger.warn("Serialization Error", ex);
			return response;
		}
	}

}
