/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.viperfish.chatapplication.MockWebSocket;
import net.viperfish.chatapplication.core.DefaultLSSession;
import net.viperfish.chatapplication.core.JsonGenerator;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.UserRegister;
import net.viperfish.chatapplication.userdb.RAMUserDatabase;

/**
 *
 * @author sdai
 */
public class AssociateLookupHandlerTest {
	private static UserDatabase userdb;
	private static UserRegister userRegister;

	@BeforeClass
	public static void init() {
		userdb = new RAMUserDatabase();
		userRegister = new UserRegister();

		User u1, u2, u3;
		u1 = new User("test1", new byte[0]);
		u2 = new User("test2", new byte[0]);
		u3 = new User("test3", new byte[0]);

		u1.getAssociates().add("test2");
		u1.getAssociates().add("test3");

		u2.getAssociates().add("test1");
		u2.getAssociates().add("test3");

		u3.getAssociates().add("test1");
		u3.getAssociates().add("test2");

		userdb.save(u1);
		userdb.save(u2);
		userdb.save(u3);

		userRegister.register("test1", new MockWebSocket());
		userRegister.register("test2", new MockWebSocket());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLookupAll() throws JsonParseException, JsonMappingException {
		AssociateLookupHandler handler = new AssociateLookupHandler(userdb, userRegister);
		handler.init();
		LSRequest associateRequest = new LSRequest();
		DefaultLSSession.createSession("test1");
		associateRequest.setSource("test1");
		associateRequest.setType(LSRequest.LS_ASSOCIATE_LOOKUP);
		associateRequest.getAttributes().put("checkOnline", "false");

		LSPayload resp = new LSPayload();
		LSResponse stat = handler.handleRequest(associateRequest, resp);

		Assert.assertEquals(LSResponse.SUCCESS, stat.getStatus());
		JsonGenerator generator = new JsonGenerator();
		Set<String> associates = generator.fromJson(Set.class, stat.getData());
		Assert.assertEquals(2, associates.size());
		Assert.assertEquals(true, associates.contains("test2"));
		Assert.assertEquals(true, associates.contains("test3"));

		associateRequest.getAttributes().put("checkOnline", "true");

		stat = handler.handleRequest(associateRequest, resp);
		associates = generator.fromJson(Set.class, stat.getData());
		Assert.assertEquals(LSResponse.SUCCESS, stat.getStatus());
		Assert.assertEquals(1, associates.size());
		Assert.assertEquals(true, associates.contains("test2"));
	}

}
