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
import net.viperfish.chatapplication.core.LSResponse;
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
    public LSResponse doFilter(LSRequest req, LSPayload resp, LSFilterChain chain) throws FilterException {
        if(req.getType() == LSRequest.LS_LOGIN) {
            logger.info("Not Processing Login Messages");
            return chain.doFilter(req, resp);
        } 
        try {
            String clientSignature = req.getAttribute("signature");
            logger.info("Client Signed:" + clientSignature);
            
            if (AuthenticationUtils.INSTANCE.verifySignedMessage(req.getData(), req.getTimeStamp(), req.getSession().getAttribute("publicKey", PublicKey.class), clientSignature)) {
                logger.info("Message Authenticated");
                LSResponse status = chain.doFilter(req, resp);
                return status;
            } else {
                logger.info("Message authentication checked failed");
                LSResponse status = new LSResponse();
                status.setStatus(LSResponse.AUTHENTICATE_FAIL);
                throw new FilterException(status);
            }

        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            LSResponse status = new LSResponse();
            status.setStatus(LSResponse.INTERNAL_ERROR);
            logger.warn("Exception", ex);
            throw new FilterException(status);
        } catch (SignatureException ex) {
            LSResponse status = new LSResponse();
            status.setStatus(LSResponse.AUTHENTICATE_FAIL);
            logger.info("Invalid Signature", ex);
            throw new FilterException(status);
        }
    }

}