/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.ValidatedRequestHandler;

/**
 *
 * @author sdai
 */
public final class MessagingHandler extends ValidatedRequestHandler {

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
		if (req.getAttribute("target") == null || req.getAttribute("target").length() == 0) {
			return false;
		}
		return true;
	}

	@Override
	public LSResponse wrappedHandleRequest(LSRequest req, LSPayload resp) {
		resp.setType(LSPayload.LS_MESSAGE);
		resp.setSource(req.getSource());
		resp.setTarget(req.getAttribute("target"));
		resp.setData(req.getData());
		resp.setAttribute("encryptionCredential", req.getAttribute("encryptionCredential"));
		resp.setAttribute("endToEndSig", req.getAttribute("endToEndSig"));
		return new LSResponse(LSResponse.SUCCESS, "Message Proccessed", "");
	}

}
