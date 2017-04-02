/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v1CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import net.viperfish.chatapplication.core.AuthenticationUtils;

/**
 *
 * @author sdai
 */
public final class TestUtils {
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance(AuthenticationUtils.KEYTYPE);
		SecureRandom rand = new SecureRandom();
		generator.initialize(2048, rand);
		return generator.generateKeyPair();
	}

	public static String generateCredential(String info, PrivateKey key)
			throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		return AuthenticationUtils.INSTANCE.generateNPlusOneCredential(info, key);
	}

	public static String generateChallenge() {
		SecureRandom rand = new SecureRandom();
		return Long.toString(rand.nextLong());
	}

	public static boolean verifyChallenge(String sent, String signature, PublicKey server)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		return AuthenticationUtils.INSTANCE.verifyNPlusOneAuth(sent, signature, server);
	}

	public static X509Certificate generateCertificate(KeyPair keyPair) {
		try {
			ContentSigner sigGen = new JcaContentSignerBuilder(AuthenticationUtils.ALGORITHM)
					.build(keyPair.getPrivate());
			Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
			Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);
			X509v1CertificateBuilder v1CertGen = new JcaX509v1CertificateBuilder(new X500Principal("CN=User"),
					BigInteger.ONE, startDate, endDate, new X500Principal("CN=User"), keyPair.getPublic());
			X509CertificateHolder certHolder = v1CertGen.build(sigGen);
			return new JcaX509CertificateConverter().getCertificate(certHolder);
		} catch (OperatorCreationException | CertificateException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static byte[] generateMACKey() {
		SecureRandom rand = new SecureRandom();
		byte[] key = new byte[32];
		rand.nextBytes(key);
		return key;
	}
}
