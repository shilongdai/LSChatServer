package net.viperfish.chatapplication.handlers;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Base64Utils;

import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.ValidatedRequestHandler;

public final class GetPublicKeyHandler extends ValidatedRequestHandler {

	private UserDatabase userDB;
	private Logger logger;

	public GetPublicKeyHandler(UserDatabase userDB) {
		this.userDB = userDB;
		logger = LogManager.getLogger();
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
	public LSResponse wrappedHandleRequest(LSRequest req, Collection<LSPayload> resp) {
		logger.info("Hitting the getPublicKey hander");
		User target = userDB.findByUsername(req.getData());
		logger.info("Got Credential");
		LSResponse response = new LSResponse();
		if (target == null) {
			response.setStatus(LSResponse.USER_NOT_FOUND, "");
			return response;
		}
		response.setStatus(LSResponse.SUCCESS);
		response.setData(Base64Utils.encodeToString(target.getCredential()));
		logger.info("Sending Response");
		return response;
	}

}
