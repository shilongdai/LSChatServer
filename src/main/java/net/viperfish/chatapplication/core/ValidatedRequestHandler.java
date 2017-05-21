package net.viperfish.chatapplication.core;

import java.util.Collection;

public abstract class ValidatedRequestHandler implements RequestHandler {

	public abstract boolean validate(LSRequest req);

	public abstract LSResponse wrappedHandleRequest(LSRequest req, Collection<LSPayload> resp);

	@Override
	public final LSResponse handleRequest(LSRequest req, Collection<LSPayload> resp) {
		if (validate(req)) {
			return wrappedHandleRequest(req, resp);
		} else {
			LSResponse invalidRequest = new LSResponse();
			invalidRequest.setStatus(LSResponse.INVALID_REQUEST);
			return invalidRequest;
		}
	}

}
