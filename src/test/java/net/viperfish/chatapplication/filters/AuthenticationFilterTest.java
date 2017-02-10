package net.viperfish.chatapplication.filters;

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
import org.junit.Test;

/**
 *
 * @author sdai
 */
public class AuthenticationFilterTest {
    
    @Test
    public void testAuthentication() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, FilterException {
        KeyPair keyPair = TestUtils.generateKeyPair();
        LSRequest req = new LSRequest();
        req.setSource("test");
        req.setType(LSRequest.LS_MESSAGE);
        req.setData("test data");
        req.setSession(DefaultLSSession.getSession("test"));
        DefaultLSSession.getSession("test").setAttribute("publicKey", keyPair.getPublic());
        req.getAttributes().put("target", "sdai");
        req.getAttributes().put("signature", AuthenticationUtils.INSTANCE.signMessage("test data", req.getTimeStamp(), keyPair.getPrivate()));
        LSPayload payload = new LSPayload();
        AuthenticationFilter filter = new AuthenticationFilter();
        MockFilterChain mock =new MockFilterChain();
        filter.doFilter(req, payload, mock);
    }
    
}