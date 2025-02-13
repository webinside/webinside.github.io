package br.com.webinside.runtime.util;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class CryptoAES {

	private SecretKey secretKey;

	public CryptoAES(String secret, int size) throws Exception {
		KeyGenerator KeyGen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(secret.getBytes("UTF-8"));
		KeyGen.init(size, sr);
		secretKey = KeyGen.generateKey();
	}
	
	public String encode(String text) throws Exception {
		Cipher ecipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		ecipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] bytes = ecipher.doFinal(text.getBytes("UTF-8"));	
		return Base64.getEncoder().encodeToString(bytes);
	}
	
	public String decode(String base64) throws Exception {
		Cipher dcipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		dcipher.init(Cipher.DECRYPT_MODE, secretKey);
		try {
			byte[] bytes = Base64.getDecoder().decode(base64);
			return new String(dcipher.doFinal(bytes), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
