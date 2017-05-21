/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.viperfish.chatapplication.MockWebSocket;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;

/**
 *
 * @author sdai
 */
public class MessagingHandlerTest {

	@Test
	public void testMessageHandler() {
		MessagingHandler handler = new MessagingHandler();
		LSRequest req = new LSRequest("test", new HashMap<>(), new Date(), 2L, "testMessage", new MockWebSocket());
		List<LSPayload> payloads = new LinkedList<>();
		req.getAttributes().put("target", "other");
		LSResponse status = handler.handleRequest(req, payloads);
		LSPayload payload = payloads.get(0);
		Assert.assertEquals(LSResponse.SUCCESS, status.getStatus());
		Assert.assertEquals("test", payload.getSource());
		Assert.assertEquals("other", payload.getTarget());
		Assert.assertEquals("testMessage", payload.getData());
	}
}
