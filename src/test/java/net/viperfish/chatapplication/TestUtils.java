/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import org.springframework.util.Base64Utils;

/**
 *
 * @author sdai
 */
public final class TestUtils {
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        SecureRandom rand = new SecureRandom();
        generator.initialize(384, rand);
        return generator.generateKeyPair();
    }
    
    public static String generateCredential(String info, PrivateKey key) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initSign(key);
        
        long response = Long.parseLong(info) + 1;
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        byte[] data = buffer.putLong(response).array();
        sig.update(data);
        byte[] signature = sig.sign();
        return Base64Utils.encodeToString(signature);
    }
    
    public static String generateChallenge() {
        SecureRandom rand = new SecureRandom();
        return Long.toString(rand.nextLong());
    }
    
    public static boolean verifyChallenge(String sent, String signature, PublicKey server) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(server);
        
        byte[] sigBytes = Base64Utils.decodeFromString(signature);
        byte[] data = ByteBuffer.allocate(Long.BYTES).putLong(Long.parseLong(sent) + 1).array();
        
        sig.update(data);
        return sig.verify(sigBytes);
    }
}
