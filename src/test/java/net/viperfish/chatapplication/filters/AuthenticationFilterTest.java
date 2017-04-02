package net.viperfish.chatapplication.filters;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;

import org.junit.Test;

import net.viperfish.chatapplication.core.AuthenticationUtils;
import net.viperfish.chatapplication.core.DefaultLSSession;
import net.viperfish.chatapplication.core.FilterException;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;

/**
 *
 * @author sdai
 */
public class AuthenticationFilterTest {

	@Test
	public void testAuthentication()
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, FilterException {
		DefaultLSSession.createSession("test");
		LSRequest req = new LSRequest();
		req.setSource("test");
		req.setType(LSRequest.LS_MESSAGE);
		req.setData("test data");
		SecureRandom rand = new SecureRandom();
		byte[] macKey = new byte[32];
		rand.nextBytes(macKey);
		req.getSession().setAttribute("macKey", macKey);
		req.getAttributes().put("target", "sdai");
		req.getAttributes().put("mac",
				AuthenticationUtils.INSTANCE.generateHMAC(macKey, req.getTimeStamp(), req.getData()));
		LSPayload payload = new LSPayload();
		AuthenticationFilter filter = new AuthenticationFilter();
		MockFilterChain mock = new MockFilterChain();
		filter.doFilter(req, payload, mock);
	}

}