/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import java.util.Collection;

import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.ValidatedRequestHandler;

/**
 *
 * @author sdai
 */
public class DeleteAssociateHandler extends ValidatedRequestHandler {

	private UserDatabase db;

	public DeleteAssociateHandler(UserDatabase db) {
		this.db = db;
	}

	@Override
	public void init() {
	}

	@Override
	public boolean validate(LSRequest req) {
		if (req.getSource() == null || req.getSource().length() == 0) {
			return false;
		}
		if (req.getData() == null || req.getData().length() == 0) {
			return false;
		}
		return true;
	}

	@Override
	public LSResponse wrappedHandleRequest(LSRequest req, Collection<LSPayload> resp) {
		String toDel = req.getData();
		String user = req.getSource();

		User u = db.findByUsername(user);
		u.getAssociates().remove(toDel);
		db.save(u);

		LSResponse response = new LSResponse();
		response.setStatus(LSResponse.SUCCESS);
		return response;
	}

}
