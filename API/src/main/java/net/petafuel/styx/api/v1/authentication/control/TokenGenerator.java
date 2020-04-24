package net.petafuel.styx.api.v1.authentication.control;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @documented https://confluence.petafuel.intern/display/RIS/API+Conventions
 */
public class TokenGenerator {
    private TokenGenerator() {
    }

    public static String generateRandomBytes() {
        byte[] bytes = new byte[32];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return binaryToHex(bytes);
    }

    //This hash method is only used for obfuscation of plain tokens when permanently saved e.g. into a database
    @SuppressWarnings("squid:S4790")
    public static String hashSHA256(String token) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(token.getBytes());
        return binaryToHex(md.digest());
    }

    public static String binaryToHex(byte[] digest) {
        return String.format("%064x", new BigInteger(1, digest));
    }
}
