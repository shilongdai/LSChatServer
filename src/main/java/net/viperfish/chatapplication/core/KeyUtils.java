/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 *
 * @author sdai
 */
public enum KeyUtils {
	INSTANCE;

	public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
		SecureRandom rand = new SecureRandom();
		generator.initialize(384, rand);
		return generator.generateKeyPair();
	}

	public void dumpKeyPair(Path pubPath, Path privPath, KeyPair keypair) throws IOException {
		Files.createDirectories(pubPath.getParent());
		Files.createDirectories(privPath.getParent());

		Files.write(pubPath, keypair.getPublic().getEncoded(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		Files.write(privPath, keypair.getPrivate().getEncoded(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
	}

	public PublicKey readPublicKey(Path p) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Files.readAllBytes(p);
		X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(keyBytes);
		PublicKey publicKey = KeyFactory.getInstance(AuthenticationUtils.KEYTYPE).generatePublic(publicSpec);
		return publicKey;
	}

	public PrivateKey readPrivateKey(Path p) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] privateKeyBytes = Files.readAllBytes(p);
		PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		PrivateKey priv = KeyFactory.getInstance(AuthenticationUtils.KEYTYPE).generatePrivate(privateSpec);
		return priv;
	}

	public void writePublicKey(Path publicKey, PublicKey pub) throws IOException {
		byte[] keyBytes = pub.getEncoded();
		Files.write(publicKey, keyBytes, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
	}

	public X509Certificate readCertificate(Path p) throws IOException, CertificateException {
		byte[] certBytes = Files.readAllBytes(p);
		return AuthenticationUtils.INSTANCE.bytesToCertificate(certBytes);
	}
}
