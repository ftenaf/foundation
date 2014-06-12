package es.tena.foundation.crypto;

import es.tena.foundation.util.Base64;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Francisco Tena<francisco.tena@gmail.com>
 */
public class SHA256Util {
    
    static MessageDigest sha256 = null;
    /**
     * Encrypts the supplied string with SHA-256
     *
     * @param text
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String sha256Hash(String text)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (sha256 == null) {
            sha256 = MessageDigest.getInstance("sha-256");
        }
        sha256.update(text.getBytes("utf-8"));
        return Base64.encodeBytes(sha256.digest());
    }

    /**
     * Encrypts the supplied string with SHA-256
     *
     * @param message
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] sha256Hash(byte[] message)
            throws NoSuchAlgorithmException {
        if (sha256 == null) {
            sha256 = MessageDigest.getInstance("sha-256");
        }
        sha256.update(message);
        return sha256.digest();
    }
}
