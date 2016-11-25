/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import java.util.Date;
import java.util.HashMap;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSStatus;
import net.viperfish.chatapplication.MockWebSocket;
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
        LSRequest req= new LSRequest("test", new HashMap<>(), new Date(), 2L, "testMessage", new MockWebSocket());
        LSPayload payload = new LSPayload();
        req.getAttributes().put("target", "other");
        LSStatus status = handler.handleRequest(req, payload);
        
        Assert.assertEquals(LSStatus.SUCCESS, status.getStatus());
        Assert.assertEquals("test", payload.getSource());
        Assert.assertEquals("other", payload.getTarget());
        Assert.assertEquals("testMessage", payload.getData());
    }
}
