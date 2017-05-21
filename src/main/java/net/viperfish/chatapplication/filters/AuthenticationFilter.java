package net.viperfish.chatapplication.filters;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.viperfish.chatapplication.core.AuthenticationUtils;
import net.viperfish.chatapplication.core.FilterException;
import net.viperfish.chatapplication.core.LSFilter;
import net.viperfish.chatapplication.core.LSFilterChain;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;

/**
 *
 * @author sdai
 */
public class AuthenticationFilter implements LSFilter {

	private Logger logger;

	public AuthenticationFilter() {
		logger = LogManager.getLogger();
	}

	@Override
	public LSResponse doFilter(LSRequest req, Collection<LSPayload> respSet, LSFilterChain chain)
			throws FilterException {
		if (req.getType() == LSRequest.LS_LOGIN) {
			logger.info("Not Processing Login Messages");
			return chain.doFilter(req, respSet);
		}
		String mac = req.getAttribute("mac");
		if (mac == null) {
			logger.info("Message not authenticated");
			LSResponse status = new LSResponse();
			status.setStatus(LSResponse.AUTHENTICATE_FAIL);
			throw new FilterException(status);
		}
		byte[] macKey = req.getSession().getAttribute("macKey", byte[].class);
		logger.info("Message Mac:" + mac);

		if (AuthenticationUtils.INSTANCE.generateHMAC(macKey, req.getTimeStamp(), req.getData()).equals(mac)) {
			logger.info("Message Authenticated");
			LSResponse status = chain.doFilter(req, respSet);
			return status;
		} else {
			logger.info("Message authentication checked failed");
			LSResponse status = new LSResponse();
			status.setStatus(LSResponse.AUTHENTICATE_FAIL);
			throw new FilterException(status);
		}
	}

}