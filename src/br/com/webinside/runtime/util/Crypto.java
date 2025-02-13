/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Soluções Tecnológicas Ltda.
 * Copyright (c) 2009-2010 Incógnita Inteligência Digital Ltda.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; versão 2.1 da Licença.
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 * ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Você deve ter recebido uma cópia da GNU LGPL junto com este programa; se não, 
 * veja em http://www.gnu.org/licenses/ 
 */

package br.com.webinside.runtime.util;

import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Classe utilizada para criptogratia de textos.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.4 $
 *
 * @since 3.0
 */
public class Crypto {
	
	private static Map<String, String> cacheDES = 
			Collections.synchronizedMap(new HashMap());
	
    private String strKey;
    private SecretKey desKey;

    /**
     * Cria um novo Crypto.
     *
     * @param key a chave que será usada no DES.
     */
    public Crypto(String key) {
    	strKey = (key != null) ? key : "";
    }

    // --------------------------------------------------------------------------
    
    public String encodeDES(String text) {
        if (text == null) return "";
        if (cacheDES.containsKey(strKey + ":" + text)) {
        	return cacheDES.get(strKey + ":" + text);
        }
        try {
        	createSecretKey();
            StringBuffer resp = new StringBuffer();
            Cipher cipher = Cipher.getInstance("DES", "SunJCE");
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            byte[] enc = cipher.doFinal(text.getBytes());
            for (int i = 0; i < enc.length; i++) {
                if (((int) enc[i] & 0xff) < 0x10) {
                    resp.append("0");
                }
                resp.append(Long.toString((int) enc[i] & 0xff, 16));
            }
            cacheDES.put(strKey + ":" + text, resp.toString());
            return resp.toString();
        } catch (Exception err) {
        	System.err.println(err.getMessage());
        }
        return "";
    }

    public String decodeDES(String text) {
        if (text == null) return "";
        if (cacheDES.containsKey(strKey + ":" + text)) {
        	return cacheDES.get(strKey + ":" + text);
        }
        try {
        	createSecretKey();
            Cipher cipher = Cipher.getInstance("DES", "SunJCE");
            AlgorithmParameters algParams = cipher.getParameters();
            cipher.init(Cipher.DECRYPT_MODE, desKey, algParams);
            int size = text.length() / 2;
            byte[] msg = new byte[size];
            for (int i = 0; i < (size * 2); i = i + 2) {
                String hex = StringA.mid(text, i, i + 1);
                msg[i / 2] = (byte) (Integer.parseInt(hex, 16));
            }
            byte[] dec = cipher.doFinal(msg);
            String resp = new String(dec).trim();
            cacheDES.put(strKey + ":" + text, resp);
            return resp;
        } catch (Exception err) {
        	System.err.println(err.getMessage() + " in password \"" + text + "\"");
        }
        return "";
    }
    
    private void createSecretKey() {
        try {
        	if (desKey == null) {
	            KeyGenerator kg = KeyGenerator.getInstance("DES", "SunJCE");
	            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
	            sr.setSeed(strKey.getBytes());            
	            kg.init(sr);
	            desKey = kg.generateKey();
        	}   
        } catch (Exception err) {
        	err.printStackTrace(System.err);
            // Nunca deve ocorrer.
        }
    }

    // --------------------------------------------------------------------------

    public static String encodeMD5(String text) {
    	return encodeHash("MD5", text);
    }
    	
    public static String encodeSHA1(String text) {
    	return encodeHash("SHA1", text);
    }
    
    private static String encodeHash(String alg, String text) {
        StringBuffer resp = new StringBuffer();
        if (text != null) {
            try {
                MessageDigest digest = MessageDigest.getInstance(alg);
                byte[] hash = digest.digest(text.getBytes());
                for (int i = 0; i < hash.length; i++) {
                    if (((int) hash[i] & 0xff) < 0x10) resp.append("0");
                    resp.append(Long.toString((int) hash[i] & 0xff, 16));
                }
            } catch (NoSuchAlgorithmException err) {
            	err.printStackTrace(System.err);
                // Nunca deve ocorrer.
            }
        }
        return resp.toString();
    }
        
    public static void main(String[] args) {
    	System.out.println(encodeMD5("G95MOOPUCS"));
    	System.out.println(encodeSHA1("G95MOOPUCS"));
    }
        
}
