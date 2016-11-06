/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import java.util.Date;
import java.util.HashMap;
import net.viperfish.chatapplication.core.DefaultLSPayload;
import net.viperfish.chatapplication.core.DefaultLSRequest;
import net.viperfish.chatapplication.core.LSStatus;
import net.viperfish.chatapplication.core.MockWebSocket;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author sdai
 */
public class MessagingHandlerTest {
    
    @Test
    public void testMessageHandler() {
        MessagingHandler handler = new MessagingHandler();
        DefaultLSRequest req= new DefaultLSRequest("test", new HashMap<>(), new Date(), 2L, "testMessage", new MockWebSocket());
        DefaultLSPayload payload = new DefaultLSPayload();
        req.getAttributes().put("target", "other");
        LSStatus status = handler.handleRequest(req, payload);
        
        Assert.assertEquals(LSStatus.SUCCESS, status.getStatus());
        Assert.assertEquals("test", payload.getSource());
        Assert.assertEquals("other", payload.getTarget());
        Assert.assertEquals("testMessage", payload.getData());
    }
}
