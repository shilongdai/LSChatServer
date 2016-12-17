/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.filters;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import net.viperfish.chatapplication.core.AuthenticationUtils;
import net.viperfish.chatapplication.core.FilterException;
import net.viperfish.chatapplication.core.LSFilter;
import net.viperfish.chatapplication.core.LSFilterChain;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author sdai
 */
public class AuthenticationFilter implements LSFilter {

    private final KeyPair keyPair;
    private Logger logger;

    public AuthenticationFilter(KeyPair keyPair) {
        this.keyPair = keyPair;
        logger = LogManager.getLogger();
    }

    @Override
    public LSStatus doFilter(LSRequest req, LSPayload resp, LSFilterChain chain) throws FilterException {
        if(req.getType() == LSRequest.LS_LOGIN) {
            logger.info("Not Processing Login Messages");
            return chain.doFilter(req, resp);
        } 
        try {
            String clientSignature = req.getAttribute("signature");
            logger.info("Client Signed:" + clientSignature);
            
            if (AuthenticationUtils.INSTANCE.verifySignedMessage(req.getData(), req.getTimeStamp(), req.getSession().getAttribute("publicKey", PublicKey.class), clientSignature)) {
                logger.info("Message Authenticated");
                LSStatus status = chain.doFilter(req, resp);
                logger.info("Signing Message with data:" + resp.getData() + " timestamp:" + resp.getTimestamp().getTime());
                String serverSig = AuthenticationUtils.INSTANCE.signMessage(resp.getData(), resp.getTimestamp(), keyPair.getPrivate());
                resp.getAttr().put("signature", serverSig);
                return status;
            } else {
                logger.info("Message authentication checked failed");
                LSStatus status = new LSStatus();
                status.setStatus(LSStatus.AUTHENTICATE_FAIL);
                throw new FilterException(status);
            }

        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            LSStatus status = new LSStatus();
            status.setStatus(LSStatus.INTERNAL_ERROR);
            logger.warn("Exception", ex);
            throw new FilterException(status);
        } catch (SignatureException ex) {
            LSStatus status = new LSStatus();
            status.setStatus(LSStatus.AUTHENTICATE_FAIL);
            logger.info("Invalid Signature", ex);
            throw new FilterException(status);
        }
    }

}
