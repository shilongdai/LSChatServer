/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Base64Utils;

import net.viperfish.chatapplication.core.AuthenticationUtils;
import net.viperfish.chatapplication.core.DefaultLSSession;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.UserRegister;
import net.viperfish.chatapplication.core.ValidatedRequestHandler;

/**
 *
 * @author sdai
 */
public final class LoginHandler extends ValidatedRequestHandler {

	private final UserDatabase userDB;
	private final UserRegister reg;
	private final Logger logger;

	public LoginHandler(UserDatabase userDB, UserRegister reg) {
		this.userDB = userDB;
		this.reg = reg;
		logger = LogManager.getLogger();
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
		if (req.getAttribute("signature") == null || req.getAttribute("signature").isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public LSResponse wrappedHandleRequest(LSRequest req, LSPayload resp) {
		User u = userDB.findByUsername(req.getSource());
		LSResponse status = new LSResponse();
		if (u == null) {
			status.setStatus(LSResponse.LOGIN_FAIL, "User" + req.getSource() + " not found");
			return status;
		}

		byte[] macKey = Base64Utils.decodeFromString(req.getData());
		PublicKey userKey = null;
		try {
			X509Certificate userCert = AuthenticationUtils.INSTANCE.bytesToCertificate(u.getCredential());
			userKey = userCert.getPublicKey();
		} catch (CertificateException ex) {
			logger.warn("Invalid Certificate", ex);
			status.setStatus(LSResponse.INTERNAL_ERROR, "Invalid Certificate Key");
			req.getSession().invalidate();
			return status;
		}
		try {
			if (AuthenticationUtils.INSTANCE.verifySignedMessage(req.getData(), req.getTimeStamp(), userKey,
					req.getAttribute("signature"))) {
				status.setStatus(LSResponse.SUCCESS);
				reg.register(u.getUsername(), req.getSocket());
				DefaultLSSession.createSession(req.getSource());
				req.getSession().setAttribute("macKey", macKey);
				logger.info("User Logged In:" + req.getSource());
			} else {
				status.setStatus(LSResponse.LOGIN_FAIL, "Username or password incorrect");
				logger.info("Incorrect Signature:" + req.getSource());
				return status;
			}
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
			logger.warn("cannot verify signature", ex);
			status.setStatus(LSResponse.LOGIN_FAIL, "Server Cannot Verify Signature");
			req.getSession().invalidate();
			return status;
		}
		return status;
	}

}
