package net.viperfish.chatapplication.core;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.util.Base64Utils;

/**
 * An utility enum that includes methods for anthenticating users and messages.
 * 
 * @author sdai
 */
public enum AuthenticationUtils {
	INSTANCE;

	/**
	 * The signing algorithm
	 */
	public static final String ALGORITHM = "SHA256withRSA";
	/**
	 * The key type for the algorithm
	 */
	public static final String KEYTYPE = "RSA";

	/**
	 * generates a signature of a message based on its content and timestamp
	 * 
	 * @param message
	 *            the message content to sign
	 * @param timestamp
	 *            the timestamp of the message to sign
	 * @param key
	 *            the private key to sign the message with
	 * @return the signature
	 * @throws SignatureException
	 *             if error occurred while signing
	 * @throws IllegalArgumentException
	 *             if the key is not proper for this signing algorithm or if the
	 *             signing algorithm is unsupported by the JCA.
	 */
	public String signMessage(String message, Date timestamp, PrivateKey key) throws SignatureException {
		// merge the message data and timestamp into one string for signature.
		StringBuilder sb = new StringBuilder();
		String toSign = sb.append(message).append(":").append(Long.toString(timestamp.getTime())).toString();
		byte[] data = toSign.getBytes(StandardCharsets.UTF_8);
		// sign the merged string
		try {
			Signature sig = Signature.getInstance(ALGORITHM);
			sig.initSign(key);
			sig.update(data);
			byte[] signature = sig.sign();
			return Base64Utils.encodeToString(signature);
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * verifies a signature generated from its content and timestamp
	 * 
	 * @param message
	 *            the message content
	 * @param timestamp
	 *            the message timestamp
	 * @param publicKey
	 *            the public key of the signer
	 * @param signature
	 *            the signature encoded in Base64
	 * @return <code>true</code> if the signature is valid, <code>false</code>
	 *         if the signature is invalid
	 * @throws SignatureException
	 *             if an error occurred while verifying the signature.
	 * @throws IllegalArgumentException
	 *             if the public key is not appropriate or if the signing
	 *             algorithm is unsupported.
	 */
	public boolean verifySignedMessage(String message, Date timestamp, PublicKey publicKey, String signature)
			throws SignatureException {
		// get the message and timestamp into one string for verification
		String messageToVerify = new StringBuilder().append(message).append(":")
				.append(Long.toString(timestamp.getTime())).toString();
		byte[] data = messageToVerify.getBytes(StandardCharsets.UTF_8);
		byte[] signatureBytes = Base64Utils.decodeFromString(signature);

		// verification
		try {
			Signature sig = Signature.getInstance(ALGORITHM);
			sig.initVerify(publicKey);
			sig.update(data);
			return sig.verify(signatureBytes);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * de-serialize a certificate encoded X509 certificate to a Java object
	 * 
	 * @param certBytes
	 *            the serialized certificate
	 * @return the de-serialized certificate
	 * @throws CertificateException
	 *             if failed to convert the encoded bytes into a valid
	 *             certificate
	 */
	public X509Certificate bytesToCertificate(byte[] certBytes) throws CertificateException {
		ByteArrayInputStream in = new ByteArrayInputStream(certBytes);
		return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in);
	}

	/**
	 * generates an SHA256 HMAC digest of a message based on its content and
	 * timestamp
	 * 
	 * @param key
	 *            the key to generate the MAC
	 * @param timestamp
	 *            the timestamp of the message
	 * @param data
	 *            the content of the message
	 * @return the HMAC code encoded in Base64
	 */
	public String generateHMAC(byte[] key, Date timestamp, String data) {
		// get the data and timestamp into one string to hash
		StringBuilder sb = new StringBuilder();
		sb.append(timestamp.getTime()).append(data);
		byte[] toMac = sb.toString().getBytes(StandardCharsets.UTF_8);

		// the hmacing
		Mac hmac = new HMac(new SHA256Digest());
		hmac.init(new KeyParameter(key));
		hmac.update(toMac, 0, toMac.length);
		byte[] out = new byte[32];
		hmac.doFinal(out, 0);
		return Base64Utils.encodeToString(out);
	}
}
