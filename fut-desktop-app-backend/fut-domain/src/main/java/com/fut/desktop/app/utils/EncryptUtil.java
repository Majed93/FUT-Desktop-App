package com.fut.desktop.app.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Use secret passphrase provided from program arguments
 */
public final class EncryptUtil {

    private final static byte[] SALT = new byte[16];

    public static String encrypt(String text, String secretKey) throws Exception {
        char[] pwd = secretKey.toCharArray();
//        Key skeySpec;
        byte[] encryptText = text.getBytes();
        Cipher cipher;

        try {
            SecretKeyFactory kf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(pwd, SALT, 8192, 256);
            SecretKey tmp = kf.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            return Base64.encodeBase64String(cipher.doFinal(encryptText));
        } catch (Exception e) {
            throw new Exception("Error encrypting: " + e.getMessage());
        }

    }

    public static String decrypt(String text, String secretKey) throws Exception {
        char[] pwd = secretKey.toCharArray();
        Cipher cipher;
        byte[] decryptText;
//        Key skeySpec;

        try {
            SecretKeyFactory kf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(pwd, SALT, 8192, 256);
            SecretKey tmp = kf.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            decryptText = Base64.decodeBase64(text);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            return new String(cipher.doFinal(decryptText));
        } catch (Exception e) {
            throw new Exception("Error decrypting: " + e.getMessage());
        }
    }

    public static String url(String io) {
        if (io.contains("156.212.100.5")) {
            io = io.replaceAll("156.212.100.5", "localhost");
        }
        return io;
    }
}
