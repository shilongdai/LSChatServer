/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handler;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import net.viperfish.chatapplication.TestUtils;
import net.viperfish.chatapplication.core.AuthenticationUtils;
import net.viperfish.chatapplication.core.DefaultLSSession;
import net.viperfish.chatapplication.core.FilterException;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.filters.AuthenticationFilter;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author sdai
 */
public class AuthenticationFilterTest {
    
    @Test
    public void testAuthentication() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, FilterException {
        KeyPair keyPair = TestUtils.generateKeyPair();
        KeyPair serverPair = TestUtils.generateKeyPair();
        LSRequest req = new LSRequest();
        req.setSource("test");
        req.setType(LSRequest.LS_MESSAGE);
        req.setData("test data");
        req.setSession(DefaultLSSession.getSession("test"));
        DefaultLSSession.getSession("test").setAttribute("publicKey", keyPair.getPublic());
        req.getAttributes().put("target", "sdai");
        req.getAttributes().put("signature", AuthenticationUtils.INSTANCE.signMessage("test data", req.getTimeStamp(), keyPair.getPrivate()));
        LSPayload payload = new LSPayload();
        AuthenticationFilter filter = new AuthenticationFilter(serverPair);
        MockFilterChain mock =new MockFilterChain();
        filter.doFilter(req, payload, mock);
        
        Assert.assertEquals(true, AuthenticationUtils.INSTANCE.verifySignedMessage(payload.getData(), payload.getTimestamp(), serverPair.getPublic(), payload.getAttr().get("signature")));
    }
    
}
