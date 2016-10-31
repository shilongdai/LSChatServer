/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import java.util.Date;
import java.util.HashMap;
import net.viperfish.chatapplication.core.DefaultLSRequest;
import net.viperfish.chatapplication.core.DefaultLSResponse;
import net.viperfish.chatapplication.core.LSRequest;
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
        LoginHandler handler = new LoginHandler(userDB, register);
        LSRequest req = new DefaultLSRequest("sample", "server", new HashMap<>(), new Date(), 1L, "password", new MockWebSocket());
        DefaultLSResponse resp = new DefaultLSResponse();
        handler.handleRequest(req, resp);

        Assert.assertEquals(200, resp.getStatus());
        Assert.assertNotEquals(null, register.getSocket("sample"));
    }
}
