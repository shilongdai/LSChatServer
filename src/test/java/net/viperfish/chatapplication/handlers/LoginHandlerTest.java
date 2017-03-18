/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.util.Date;
import java.util.HashMap;

import org.glassfish.grizzly.websockets.WebSocket;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import net.viperfish.chatapplication.MockWebSocket;
import net.viperfish.chatapplication.TestUtils;
import net.viperfish.chatapplication.core.DefaultLSSession;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.LSSession;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.UserRegister;
import net.viperfish.chatapplication.userdb.RAMUserDatabase;

/**
 *
 * @author sdai
 */
public class LoginHandlerTest {

	private static UserDatabase userDB;
	private static UserRegister register;
	private static KeyPair testKey;
	private static KeyPair serverKey;

	@BeforeClass
	public static void init() throws NoSuchAlgorithmException {
		userDB = new RAMUserDatabase();
		testKey = TestUtils.generateKeyPair();
		try {
			userDB.save(new User("sample", TestUtils.generateCertificate(testKey).getEncoded()));
		} catch (CertificateEncodingException e) {
			throw new RuntimeException(e);
		}
		register = new UserRegister();
		serverKey = TestUtils.generateKeyPair();
	}

	@Test
	public void testLoginHandlerSuccess() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		register.unregister("sample");
		LSSession session = DefaultLSSession.getSession("sample");
		WebSocket socket = new MockWebSocket();
		LoginHandler handler = new LoginHandler(userDB, register, serverKey.getPrivate());
		String clientChallenge = TestUtils.generateChallenge();
		LSRequest req = new LSRequest("sample", new HashMap<>(), new Date(), 1L, clientChallenge, socket);
		req.setSession(session);
		LSPayload payload = new LSPayload();
		LSResponse resp = handler.handleRequest(req, payload);

		Assert.assertEquals(LSResponse.CHALLENGE, resp.getStatus());
		Assert.assertEquals(true, req.getSession().containsAttribute("imposedChallenge"));
		Assert.assertNotEquals(0, resp.getData().length());
		Assert.assertEquals(true,
				TestUtils.verifyChallenge(clientChallenge, resp.getData().split(";")[1], serverKey.getPublic()));

		req = new LSRequest("sample", new HashMap<>(), new Date(), 1L,
				TestUtils.generateCredential(resp.getData().split(";")[0], testKey.getPrivate()), socket);
		req.setSession(session);
		resp = handler.handleRequest(req, payload);

		Assert.assertEquals(LSResponse.SUCCESS, resp.getStatus());
		Assert.assertNotEquals(null, register.getSocket("sample"));
		Assert.assertArrayEquals(testKey.getPublic().getEncoded(),
				req.getSession().getAttribute("publicKey", PublicKey.class).getEncoded());
	}

	@Test
	public void testLoginHandlerFail() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		register.unregister("sample");
		LoginHandler handler = new LoginHandler(userDB, register, serverKey.getPrivate());
		String clientChallenge = TestUtils.generateChallenge();
		LSRequest req;
		LSSession noExist = DefaultLSSession.getSession("noexist");
		WebSocket socket = new MockWebSocket();
		req = new LSRequest("noexist", new HashMap<>(), new Date(), 1L, clientChallenge, socket);
		req.setSession(noExist);
		LSPayload payload = new LSPayload();
		LSResponse resp = handler.handleRequest(req, payload);
		Assert.assertEquals(LSResponse.LOGIN_FAIL, resp.getStatus());
		Assert.assertEquals(null, register.getSocket("noexist"));

		socket = new MockWebSocket();
		LSSession sampleSession = DefaultLSSession.getSession("sample");
		req = new LSRequest("sample", new HashMap<>(), new Date(), 1L, clientChallenge, socket);
		req.setSession(sampleSession);
		payload = new LSPayload();
		resp = handler.handleRequest(req, payload);
		Assert.assertEquals(LSResponse.CHALLENGE, resp.getStatus());
		Assert.assertEquals(true,
				TestUtils.verifyChallenge(clientChallenge, resp.getData().split(";")[1], serverKey.getPublic()));

		req = new LSRequest("sample", new HashMap<>(), new Date(), 1L,
				TestUtils.generateCredential("0", testKey.getPrivate()), socket);
		req.setSession(sampleSession);
		resp = handler.handleRequest(req, payload);

		Assert.assertEquals(LSResponse.LOGIN_FAIL, resp.getStatus());
		Assert.assertEquals(null, register.getSocket("sample"));
		Assert.assertEquals(false, req.getSession().containsAttribute("publicKey"));
	}
}
