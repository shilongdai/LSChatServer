package net.viperfish.chatapplication.core;

public abstract class ValidatedRequestHandler implements RequestHandler {

	public abstract boolean validate(LSRequest req);

	public abstract LSResponse wrappedHandleRequest(LSRequest req, LSPayload resp);

	@Override
	public final LSResponse handleRequest(LSRequest req, LSPayload resp) {
		if (validate(req)) {
			return wrappedHandleRequest(req, resp);
		} else {
			LSResponse invalidRequest = new LSResponse();
			invalidRequest.setStatus(LSResponse.INVALID_REQUEST);
			return invalidRequest;
		}
	}

}
