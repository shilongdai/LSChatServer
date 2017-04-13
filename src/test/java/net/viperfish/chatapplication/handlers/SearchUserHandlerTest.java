/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.viperfish.chatapplication.core.JsonGenerator;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.userdb.RAMUserDatabase;

/**
 *
 * @author sdai
 */
public class SearchUserHandlerTest {
	private final UserDatabase userdb;
	private final SearchUserHandler handler;

	public SearchUserHandlerTest() {
		userdb = new RAMUserDatabase();
		userdb.save(new User("test1", new byte[0]));
		userdb.save(new User("test2", new byte[0]));
		userdb.save(new User("somerandomguy", new byte[0]));
		handler = new SearchUserHandler(userdb);
	}

	@Test
	public void testLookup() throws JsonParseException, JsonMappingException {
		LSRequest req = new LSRequest();
		req.setType(LSRequest.LS_LOOKUP_USER);
		req.getAttributes().put("keyword", "test");

		LSPayload payload = new LSPayload();
		LSResponse resp = handler.handleRequest(req, payload);

		Assert.assertEquals(LSResponse.SUCCESS, resp.getStatus());
		JsonGenerator generator = new JsonGenerator();
		@SuppressWarnings("unchecked")
		Set<String> results = generator.fromJson(Set.class, resp.getData());
		Assert.assertEquals(true, results.contains("test1"));
		Assert.assertEquals(true, results.contains("test2"));
		Assert.assertEquals(false, results.contains("somerandomguy"));

	}

}
