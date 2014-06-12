package es.tena.foundation.crypto;

import es.tena.foundation.util.Base64;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 *
 * @author Francisco Tena<francisco.tena@gmail.com>
 */
public class MD5RSAUtil {

    static MessageDigest md5 = null;
    

    /**
     * Encrypts the supplied string with MD5
     *
     * @param text
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String md5Hash(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] bytes = doMd5Hash(text);
        return Base64.encodeBytes(bytes);
    }

    /**
     * Encrypts the supplied string with MD5
     *
     * @param text
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static byte[] doMd5Hash(String text)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (md5 == null) {
            md5 = MessageDigest.getInstance("md5");
        }
        md5.update(text.getBytes("utf-8"));
        byte[] bytes = md5.digest();
        return bytes;
    }

    /**
     * Encrypts the supplied string with MD5
     *
     * @param message
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] md5Hash(byte[] message)
            throws NoSuchAlgorithmException {
        if (md5 == null) {
            md5 = MessageDigest.getInstance("md5");
        }
        md5.update(message);
        byte[] bytes = md5.digest();
        return bytes;
    }

    /**
     * Gets the signature of the string provided using the privateKey parameter
     *
     * @param text
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String signMD5RSA(String text, byte[] privateKey)
            throws Exception {
        String signature = Base64.encodeBytes(
                signMD5RSA(text.getBytes("utf-8"), privateKey));
        return signature;
    }

    /**
     * Gets the signature of the string provided using the privateKey parameter
     *
     * @param message
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] signMD5RSA(byte[] message, byte[] privateKey)
            throws Exception {
        byte[] messageHash = md5Hash(message);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey);
        Key k = kf.generatePrivate(spec);
        cipher.init(Cipher.ENCRYPT_MODE, k);
        byte[] bytes = cipher.doFinal(messageHash);
        return bytes;
    }

    /**
     * Gets the signature of the string provided using the privateKey parameter
     *
     * @param text text to verify its authenticity
     * @param signature of the text
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean checkSignatureMD5RSA(String text, String signature, byte[] publicKey)
            throws Exception {
        return checkSignatureMD5RSA(text.getBytes("utf-8"), Base64.decode(signature), publicKey);
    }

    /**
     * Gets the signature of the string provided using the privateKey parameter
     *
     * @param message byte array to verify its authenticity
     * @param signature of the text
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean checkSignatureMD5RSA(byte[] message, byte[] signature, byte[] publicKey)
            throws Exception {
        byte[] messageHash = md5Hash(message);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
        Key k = kf.generatePublic(spec);
        cipher.init(Cipher.DECRYPT_MODE, k);
        byte[] bytes = cipher.doFinal(signature);
        if (bytes.length != messageHash.length) {
            return false;
        }
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != messageHash[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Encrypts with RSA the byte array provided with the public key provided
     *
     * @param message
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] RSAEncrypt(byte[] message, byte[] publicKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
        Key k = kf.generatePublic(spec);
        cipher.init(Cipher.ENCRYPT_MODE, k);
        byte[] bytes = cipher.doFinal(message);
        return bytes;
    }

    /**
     * Decrypts the byte array provided using the privateKey parameter
     *
     * @param message
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] RSADecrypt(byte[] message, byte[] privateKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey);
        Key k = kf.generatePrivate(spec);
        cipher.init(Cipher.DECRYPT_MODE, k);
        byte[] bytes = cipher.doFinal(message);
        return bytes;
    }
}
