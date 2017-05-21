package net.viperfish.chatapplication.handlers;

import java.util.Collection;

import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.RequestHandler;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;

public final class RetrieveUnsentPacketsHandler implements RequestHandler {

	private UserDatabase db;

	public RetrieveUnsentPacketsHandler(UserDatabase db) {
		this.db = db;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public LSResponse handleRequest(LSRequest req, Collection<LSPayload> payloads) {
		LSResponse resp = new LSResponse();
		User u = db.findByUsername(req.getSource());
		if (u != null) {
			for (int i = 0; i < u.getUnsentMessages().size(); ++i) {
				payloads.add(u.getUnsentMessages().get(i));
			}
			u.getUnsentMessages().clear();
			db.save(u);

			resp.setStatus(LSResponse.SUCCESS);
		} else {
			resp.setStatus(LSResponse.USER_NOT_FOUND);
		}
		return resp;
	}

}
