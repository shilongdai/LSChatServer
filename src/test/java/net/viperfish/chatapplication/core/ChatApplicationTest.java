/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import net.viperfish.chatapplication.userdb.RAMUserDatabase;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.util.Date;
import net.viperfish.chatapplication.ChatApplication;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sdai
 */
public class ChatApplicationTest {

    private static ChatApplication toTest;
    private static JsonGenerator generator;
    private static UserDatabase userDB;

    @BeforeClass
    public static void setup() {
        toTest = new ChatApplication();
        generator = new JsonGenerator();
        userDB = new RAMUserDatabase();
        User testUser = new User("testUser", "password");
        User testUser1 = new User("testUser1", "password");
        userDB.save(testUser);
        userDB.save(testUser1);
        toTest.setUserDB(userDB);
    }

    @Test
    public void testLoginSuccess() throws JsonGenerationException, JsonMappingException, JsonParseException {
        MockWebSocket socket = new MockWebSocket();
        DefaultLSRequest request = new DefaultLSRequest();
        request.setType(1L);
        request.setTimeStamp(new Date());
        request.setTarget("server");
        request.setSource("testUser");
        request.setData("password");
        String packet = generator.toJson(request);
        toTest.onMessage(socket, packet);

        DefaultLSResponse resp = generator.fromJson(DefaultLSResponse.class, socket.getSentData().get(0));
        Assert.assertEquals(200L, resp.getStatus());
    }

    @Test
    public void testLoginFail() throws JsonGenerationException, JsonMappingException, JsonParseException {
        MockWebSocket socket = new MockWebSocket();
        DefaultLSRequest request = new DefaultLSRequest();
        request.setType(1L);
        request.setTimeStamp(new Date());
        request.setTarget("server");
        request.setSource("testUser");
        request.setData("fail");
        String packet = generator.toJson(request);
        toTest.onMessage(socket, packet);

        DefaultLSResponse resp = generator.fromJson(DefaultLSResponse.class, socket.getSentData().get(0));
        Assert.assertEquals(201L, resp.getStatus());
    }

    @Test
    public void testMessage() throws JsonGenerationException, JsonMappingException {
        MockWebSocket socket = new MockWebSocket();
        DefaultLSRequest request = new DefaultLSRequest();
        request.setType(1L);
        request.setTimeStamp(new Date());
        request.setTarget("server");
        request.setSource("testUser");
        request.setData("password");
        String packet = generator.toJson(request);
        toTest.onMessage(socket, packet);

        MockWebSocket socket1 = new MockWebSocket();
        DefaultLSRequest request1 = new DefaultLSRequest();
        request.setType(1L);
        request.setTimeStamp(new Date());
        request.setTarget("server");
        request.setSource("testUser1");
        request.setData("password");
        String packet1 = generator.toJson(request1);
        toTest.onMessage(socket1, packet1);

        DefaultLSRequest message = new DefaultLSRequest();
        message.setData("testMessage");
        message.setType(2L);
        message.setTimeStamp(new Date());
        message.setTarget("testUser");
        message.setSource("testUser1");
        String messagePacket = generator.toJson(message);
        toTest.onMessage(socket1, messagePacket);
    }

}
