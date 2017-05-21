package net.viperfish.chatapplication.handlers;

import java.util.Collection;

import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.RequestHandler;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.UserRegister;

public final class PersistentPayloadWrapper implements RequestHandler {

	private RequestHandler handler;
	private UserRegister register;
	private UserDatabase userdb;

	public PersistentPayloadWrapper(RequestHandler handler, UserRegister register, UserDatabase userdb) {
		super();
		this.handler = handler;
		this.register = register;
		this.userdb = userdb;
	}

	@Override
	public void init() {
		handler.init();

	}

	@Override
	public LSResponse handleRequest(LSRequest req, Collection<LSPayload> resps) {
		LSResponse resp = handler.handleRequest(req, resps);
		for (LSPayload payload : resps) {
			if (!register.isOnline(payload.getTarget())) {
				User u = userdb.findByUsername(payload.getTarget());
				if (u == null) {
					return new LSResponse(LSResponse.USER_NOT_FOUND, payload.getTarget(), "");
				}
				u.getUnsentMessages().add(payload);
				userdb.save(u);
				resps.remove(payload);
			}
		}
		return resp;
	}

}
