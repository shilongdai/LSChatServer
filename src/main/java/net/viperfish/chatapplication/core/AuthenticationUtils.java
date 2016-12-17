/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Date;
import org.springframework.util.Base64Utils;

/**
 *
 * @author sdai
 */
public enum AuthenticationUtils {
    INSTANCE;
    
    public boolean verifyNPlusOneAuth(String correctChallenge, String signature, PublicKey pub) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        long incremented = Long.parseLong(correctChallenge) + 1;
        
        byte[] data = ByteBuffer.allocate(Long.BYTES).putLong(incremented).array();
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(pub);
        sig.update(data);
        
        return sig.verify(Base64Utils.decodeFromString(signature));
    }
    
    public String generateNPlusOneCredential(String challenge, PrivateKey key) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initSign(key);
        
        long response = Long.parseLong(challenge) + 1;
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        byte[] data = buffer.putLong(response).array();
        sig.update(data);
        byte[] signature = sig.sign();
        return Base64Utils.encodeToString(signature);
    }
    
    public String signMessage(String message, Date timestamp, PrivateKey key) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        StringBuilder sb = new StringBuilder();
        String toSign = sb.append(message).append(":").append(Long.toString(timestamp.getTime())).toString();
        byte[] data = toSign.getBytes(StandardCharsets.UTF_8);
        
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initSign(key);
        sig.update(data);
        byte[] signature = sig.sign();
        return Base64Utils.encodeToString(signature);
    }
    
    public boolean verifySignedMessage(String message, Date timestamp, PublicKey publicKey, String signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        String messageToVerify = new StringBuilder().append(message).append(":").append(Long.toString(timestamp.getTime())).toString();
        byte[] data = messageToVerify.getBytes(StandardCharsets.UTF_8);
        byte[] signatureBytes = Base64Utils.decodeFromString(signature);
        
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signatureBytes);
    }
    
    public String generateChallenge() {
        SecureRandom rand = new SecureRandom();
        return Long.toString(rand.nextLong());
    }
}
