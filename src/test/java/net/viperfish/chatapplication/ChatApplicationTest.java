/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.viperfish.chatapplication.core.JsonGenerator;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSStatus;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.UserRegister;
import net.viperfish.chatapplication.handlers.LoginHandler;
import net.viperfish.chatapplication.handlers.MessagingHandler;
import net.viperfish.chatapplication.userdb.RAMUserDatabase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.Base64Utils;

/**
 *
 * @author sdai
 */
public class ChatApplicationTest {

    private static ChatApplication toTest;
    private static JsonGenerator generator;
    private static UserDatabase userDB;
    private static UserRegister reg;
    private static KeyPair testKey1;
    private static KeyPair testKey2;

    @BeforeClass
    public static void setup() throws NoSuchAlgorithmException {
        testKey1 = TestUtils.generateKeyPair();
        testKey2 = TestUtils.generateKeyPair();
        
        toTest = new ChatApplication();
        generator = new JsonGenerator();
        userDB = new RAMUserDatabase();
        reg = new UserRegister();
        User testUser = new User("testUser", Base64Utils.encodeToString(testKey1.getPublic().getEncoded()));
        User testUser1 = new User("testUser1", Base64Utils.encodeToString(testKey2.getPublic().getEncoded()));
        userDB.save(testUser);
        userDB.save(testUser1);
        toTest.setSocketMapper(reg);
        toTest.addHandler(LSRequest.LS_LOGIN, new LoginHandler(userDB, reg));
        toTest.addHandler(LSRequest.LS_MESSAGE, new MessagingHandler());
    }

    @Test
    public void testLoginSuccess() throws JsonGenerationException, JsonMappingException, JsonParseException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        reg.clear();
        MockWebSocket socket = new MockWebSocket();
        LSRequest request = new LSRequest();
        request.setType(1L);
        request.setTimeStamp(new Date());
        request.setSource("testUser");
        request.setData("challenge");
        String packet = generator.toJson(request);
        toTest.onMessage(socket, packet);
        
        Assert.assertEquals(1, socket.getSentData().size());
        
        LSPayload payload = generator.fromJson(LSPayload.class, socket.getSentData().get(0));
        Assert.assertEquals(LSPayload.LS_STATUS, payload.getType());
        Assert.assertEquals(null, payload.getSource());
        Assert.assertEquals(null, payload.getTarget());
        
        LSStatus resp = generator.fromJson(LSStatus.class, payload.getData());
        Assert.assertEquals(LSStatus.CHALLENGE, resp.getStatus());
        request = new LSRequest();
        request.setType(1L);
        request.setTimeStamp(new Date());
        request.setSource("testUser");
        request.setData(TestUtils.generateCredential(resp.getAdditional(), testKey1.getPrivate()));
        packet = generator.toJson(request);
        toTest.onMessage(socket, packet);
        payload = generator.fromJson(LSPayload.class, socket.getSentData().get(1));
        Assert.assertEquals(LSPayload.LS_STATUS, payload.getType());
        Assert.assertEquals(null, payload.getSource());
        Assert.assertEquals("testUser", payload.getTarget());
        
        resp = generator.fromJson(LSStatus.class, payload.getData());
        
        Assert.assertEquals(LSStatus.SUCCESS, resp.getStatus());
        Assert.assertNotEquals(null, reg.getSocket("testUser"));
    }
    
    
    @Test
    public void testLoginFail() throws JsonGenerationException, JsonMappingException, JsonParseException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        reg.clear();
        MockWebSocket socket = new MockWebSocket();
        LSRequest request = new LSRequest();
        request.setType(1L);
        request.setTimeStamp(new Date());
        request.setSource("testUser");
        request.setData(TestUtils.generateCredential("0", testKey2.getPrivate()));
        String packet = generator.toJson(request);
        toTest.onMessage(socket, packet);
        toTest.onMessage(socket, packet);

        LSPayload payload = generator.fromJson(LSPayload.class, socket.getSentData().get(1));
        Assert.assertEquals(LSPayload.LS_STATUS, payload.getType());
        Assert.assertEquals(null, payload.getSource());
        Assert.assertEquals(null, payload.getTarget());
        
        LSStatus resp = generator.fromJson(LSStatus.class, payload.getData());
        Assert.assertEquals(LSStatus.LOGIN_FAIL, resp.getStatus());
    }

    @Test
    public void testMessage() throws JsonGenerationException, JsonMappingException, JsonParseException {
        reg.clear();
        MockWebSocket socket = new MockWebSocket();
        MockWebSocket socket1 = new MockWebSocket();
        reg.register("testUser", socket);
        reg.register("testUser1", socket1);
        LSRequest message = new LSRequest();
        message.setData("testMessage");
        message.setType(2L);
        message.setTimeStamp(new Date());
        message.getAttributes().put("target", "testUser");
        message.setSource("testUser1");
        String messagePacket = generator.toJson(message);
        toTest.onMessage(socket1, messagePacket);
        
        Assert.assertEquals(1, socket1.getSentData().size());
        LSPayload payload = generator.fromJson(LSPayload.class, socket1.getSentData().get(0));
        Assert.assertEquals(LSPayload.LS_STATUS, payload.getType());
        Assert.assertEquals(null, payload.getSource());
        Assert.assertEquals("testUser1", payload.getTarget());
        LSStatus status = generator.fromJson(LSStatus.class, payload.getData());
        Assert.assertEquals(LSStatus.SUCCESS, status.getStatus());
        
        Assert.assertEquals(1, socket.getSentData().size());
        LSPayload received = generator.fromJson(LSPayload.class, socket.getSentData().get(0));
        Assert.assertEquals("testMessage", received.getData());
        Assert.assertEquals("testUser", received.getTarget());
        Assert.assertEquals("testUser1", received.getSource());
    }
    
    @Test
    public void testMessageTargetNotFound() throws JsonGenerationException, JsonMappingException, JsonParseException {
        reg.clear();
        MockWebSocket socketSource = new MockWebSocket();
        reg.register("source",socketSource);
        Map<String, String> attrs = new HashMap<>();
        attrs.put("target", "dne");
        LSRequest req = new LSRequest("source",attrs , new Date(), 2L, "irrelevent", socketSource);
        
        toTest.onMessage(socketSource, generator.toJson(req));
        
        LSPayload payload = generator.fromJson(LSPayload.class, socketSource.getSentData().get(0));
        Assert.assertEquals(LSPayload.LS_STATUS, payload.getType());
        Assert.assertEquals(null, payload.getSource());
        Assert.assertEquals("source", payload.getTarget());
        LSStatus status = generator.fromJson(LSStatus.class, payload.getData());
        Assert.assertEquals(LSStatus.USER_OFFLINE, status.getStatus());
        
    }

}
