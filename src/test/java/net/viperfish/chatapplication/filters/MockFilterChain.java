package net.viperfish.chatapplication.filters;

import java.util.Collection;

import net.viperfish.chatapplication.core.LSFilterChain;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;

/**
 *
 * @author sdai
 */
public class MockFilterChain implements LSFilterChain {

	@Override
	public LSResponse doFilter(LSRequest req, Collection<LSPayload> resps) {
		LSPayload resp = new LSPayload();
		resp.setType(LSPayload.LS_MESSAGE);
		resp.setSource(req.getSource());
		resp.setTarget(req.getAttribute("target"));
		resp.setData(req.getData());
		return new LSResponse(LSResponse.SUCCESS, "", "");
	}

}
