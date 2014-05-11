package es.tena.foundation.crypto;

import es.tena.foundation.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

/**
 * DES crypto algorithm utilities
 * @author Francisco Tena<francisco.tena@gmail.com>
 */
public class DESUtil {

    /**
     * Encrypts the input string provided
     *
     * @param input Text to encrypt
     * @return encrypted Text
     * @throws RuntimeException
     */
    public static String encrypt(String input) throws RuntimeException {
        String storepass = null;
        byte[] result = null;
        try {
            Security.addProvider(new sun.security.provider.Sun());
            MessageDigest lMessageDigest = java.security.MessageDigest
                    .getInstance("SHA", "SUN");
            result = lMessageDigest.digest(input.getBytes());
            storepass = Base64.encodeBytes(result);

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            return null;
        }
        return storepass;
    }

    /**
     * Checks if 2 encrypted strings are equal
     *
     * @param pass1 String to compare
     * @param pass2 String to compare
     * @return True if both strings provided are equal
     * @throws Exception
     */
    public boolean equals(String pass1, String pass2)
            throws Exception {
        return encrypt(pass1).equals(encrypt(pass2));
    }
}

