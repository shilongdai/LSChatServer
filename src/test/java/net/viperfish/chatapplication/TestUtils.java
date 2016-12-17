/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import net.viperfish.chatapplication.core.AuthenticationUtils;

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
        return AuthenticationUtils.INSTANCE.generateNPlusOneCredential(info, key);
    }
    
    public static String generateChallenge() {
        SecureRandom rand = new SecureRandom();
        return Long.toString(rand.nextLong());
    }
    
    public static boolean verifyChallenge(String sent, String signature, PublicKey server) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return AuthenticationUtils.INSTANCE.verifyNPlusOneAuth(sent, signature, server);
    }
}
