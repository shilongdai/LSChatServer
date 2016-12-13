/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.handlers;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSStatus;
import net.viperfish.chatapplication.core.RequestHandler;
import net.viperfish.chatapplication.core.User;
import net.viperfish.chatapplication.core.UserDatabase;
import net.viperfish.chatapplication.core.UserRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Base64Utils;
import sun.security.ec.ECPublicKeyImpl;

/**
 *
 * @author sdai
 */
public final class LoginHandler implements RequestHandler {

    private final UserDatabase userDB;
    private final UserRegister reg;
    private final Logger logger;
    private final PrivateKey priv;

    public LoginHandler(UserDatabase userDB, UserRegister reg, PrivateKey priv) {
        this.userDB = userDB;
        this.reg = reg;
        logger = LogManager.getLogger();
        this.priv = priv;
    }

    @Override
    public void init() {
    }

    @Override
    public LSStatus handleRequest(LSRequest req, LSPayload resp) {
        User u = userDB.findByUsername(req.getSource());
        LSStatus status = new LSStatus();
        if (u == null) {
            status.setStatus(LSStatus.LOGIN_FAIL, "User" + req.getSource() + " not found");
            return status;
        }
        try {
            if (req.getSession().getAttribute("imposedChallenge", String.class) == null) {

                status.setStatus(LSStatus.CHALLENGE);
                SecureRandom rand = new SecureRandom();
                long chg = 0;
                while (chg == 0) {
                    chg = rand.nextLong();
                }

                String challengeResponse = this.generateCredential(req.getData(), priv);
                status.setAdditional(Long.toString(chg) + ";" + challengeResponse);
                req.getSession().setAttribute("imposedChallenge", Long.toString(chg));
                return status;

            }
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException ex) {
            this.logger.warn("cannot generate credential", ex);
            status.setStatus(LSStatus.INTERNAL_ERROR);
            req.getSession().invalidate();
            return status;
        }
        String suppliedCredential = req.getData();
        PublicKey userKey = null;
        try {
            userKey = new ECPublicKeyImpl(u.getCredential());
        } catch (InvalidKeyException ex) {
            logger.warn("Invalid Key", ex);
            status.setStatus(LSStatus.INTERNAL_ERROR, "Invalid Public Key");
            req.getSession().invalidate();
            return status;
        }
        try {
            if (this.verifyLogin(req.getSession().getAttribute("imposedChallenge", String.class), suppliedCredential, userKey)) {
                status.setStatus(LSStatus.SUCCESS);
                reg.register(u.getUsername(), req.getSocket());
                req.getSession().setAttribute("publicKey", userKey);
            } else {
                status.setStatus(LSStatus.LOGIN_FAIL, "Username or password incorrect");
                req.getSession().invalidate();
                return status;
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            logger.warn("cannot verify signature", ex);
            status.setStatus(LSStatus.LOGIN_FAIL, "Server Cannot Verify Signature");
            req.getSession().invalidate();
            return status;
        }
        return status;
    }

    public boolean verifyLogin(String correctChallenge, String signature, PublicKey pub) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        long incremented = Long.parseLong(correctChallenge) + 1;

        byte[] data = ByteBuffer.allocate(Long.BYTES).putLong(incremented).array();
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(pub);
        sig.update(data);

        return sig.verify(Base64Utils.decodeFromString(signature));
    }

    public String generateCredential(String info, PrivateKey key) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initSign(key);

        long response = Long.parseLong(info) + 1;
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        byte[] data = buffer.putLong(response).array();
        sig.update(data);
        byte[] signature = sig.sign();
        return Base64Utils.encodeToString(signature);
    }

}
