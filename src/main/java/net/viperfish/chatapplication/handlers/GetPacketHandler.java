package net.viperfish.chatapplication.handlers;

import org.springframework.util.Base64Utils;

import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.ValidatedRequestHandler;

public final class GetPacketHandler extends ValidatedRequestHandler {

	private UserDatabase userDB;

	public GetPacketHandler(UserDatabase userDB) {
		this.userDB = userDB;
	}

	@Override
	public void init() {

	}

	@Override
	public boolean validate(LSRequest req) {
		if (req.getData() == null || req.getData().isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public LSResponse wrappedHandleRequest(LSRequest req, LSPayload resp) {
		User target = userDB.findByUsername(req.getData());
		LSResponse response = new LSResponse();
		if (target == null) {
			response.setStatus(LSResponse.USER_NOT_FOUND, "");
			return response;
		}
		response.setData(Base64Utils.encodeToString(target.getCredential()));
		return response;
	}

}
