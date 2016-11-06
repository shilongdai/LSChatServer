/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import java.util.Date;
import java.util.HashMap;
import net.viperfish.chatapplication.core.ChatWebSocket;
import net.viperfish.chatapplication.core.DefaultLSPayload;
import net.viperfish.chatapplication.core.DefaultLSRequest;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSStatus;
import net.viperfish.chatapplication.core.MockWebSocket;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.UserRegister;
import net.viperfish.chatapplication.userdb.RAMUserDatabase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sdai
 */
public class LoginHandlerTest {

    private static UserDatabase userDB;
    private static UserRegister register;

    @BeforeClass
    public static void init() {
        userDB = new RAMUserDatabase();
        userDB.save(new User("sample", "password"));
        register = new UserRegister();
    }

    @Test
    public void testLoginHandlerSuccess() {
        register.unregister("sample");
        ChatWebSocket chatSocket = new ChatWebSocket(new MockWebSocket(), null);
        LoginHandler handler = new LoginHandler(userDB, register);
        LSRequest req = new DefaultLSRequest("sample", new HashMap<>(), new Date(), 1L, "password", chatSocket);
        LSPayload payload = new DefaultLSPayload();
        LSStatus resp = handler.handleRequest(req, payload);

        Assert.assertEquals("sample", chatSocket.getUser());
        Assert.assertEquals(LSStatus.SUCCESS, resp.getStatus());
        Assert.assertNotEquals(null, register.getSocket("sample"));
        
    }
    
    @Test
    public void testLoginHandlerFail() {
        register.unregister("sample");
        LoginHandler handler = new LoginHandler(userDB, register);
        LSRequest req;
        ChatWebSocket socket = new ChatWebSocket(new MockWebSocket(), null);
        req = new DefaultLSRequest("noexist", new HashMap<>(), new Date(), 1L, "null", socket);
        LSPayload payload = new DefaultLSPayload();
        LSStatus resp = handler.handleRequest(req, payload);
        
        Assert.assertEquals(LSStatus.LOGIN_FAIL, resp.getStatus());
        Assert.assertEquals(null, register.getSocket("noexist"));
        Assert.assertEquals(null, socket.getUser());
        
        socket = new ChatWebSocket(new MockWebSocket(), null);
        req = new DefaultLSRequest("sample", new HashMap<>(), new Date(), 1L, "null", socket);
        payload = new DefaultLSPayload();
        resp = handler.handleRequest(req, payload);
        
        Assert.assertEquals(null, socket.getUser());
        Assert.assertEquals(LSStatus.LOGIN_FAIL, resp.getStatus());
        Assert.assertEquals(null, register.getSocket("sample"));
    }
}
