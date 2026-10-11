package com.printxpress.app.core.security;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hashes and verifies passwords with PBKDF2WithHmacSHA256 and a random per-user salt (NFR-5).
 * <p>
 * Salts and hashes are stored as lower-case hex text. Verification uses a constant-time
 * comparison so response time does not reveal how many bytes matched.
 * </p>
 *
 * @since 1.0
 */
public final class PasswordHasher {

    /** JCA algorithm name. */
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    /** PBKDF2 iteration count; high enough to slow brute force, low enough for a phone. */
    private static final int ITERATIONS = 120_000;
    /** Derived key length in bits. */
    private static final int KEY_LENGTH_BITS = 256;
    /** Salt length in bytes. */
    private static final int SALT_BYTES = 16;
    /** Hex digit lookup table. */
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    /** Source of random salts. */
    private final SecureRandom random;

    /** Creates a hasher backed by a {@link SecureRandom}. */
    public PasswordHasher() {
        this(new SecureRandom());
    }

    /**
     * Creates a hasher with an injected random source (for tests).
     *
     * @param random the random generator
     */
    public PasswordHasher(SecureRandom random) {
        this.random = random;
    }

    /**
     * Generates a new random salt.
     *
     * @return the salt as hex text
     */
    public String generateSalt() {
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        return toHex(salt);
    }

    /**
     * Hashes a password with the given salt.
     *
     * @param password the plain password; the array is not modified
     * @param saltHex  the salt as hex text
     * @return the hash as hex text
     * @throws IllegalStateException when the platform lacks the PBKDF2 algorithm
     */
    public String hash(char[] password, String saltHex) {
        PBEKeySpec spec = new PBEKeySpec(password, fromHex(saltHex), ITERATIONS, KEY_LENGTH_BITS);
        try {
            byte[] derived = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
            return toHex(derived);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 unavailable", e);
        } finally {
            spec.clearPassword();
        }
    }

    /**
     * Checks a password against a stored hash.
     *
     * @param password     the candidate password
     * @param saltHex      the stored salt
     * @param expectedHash the stored hash
     * @return true when the password matches
     */
    public boolean verify(char[] password, String saltHex, String expectedHash) {
        if (password == null || saltHex == null || expectedHash == null) {
            return false;
        }
        byte[] actual = fromHex(hash(password, saltHex));
        byte[] expected = fromHex(expectedHash);
        boolean match = MessageDigest.isEqual(actual, expected);
        Arrays.fill(actual, (byte) 0);
        return match;
    }

    /**
     * Encodes bytes as lower-case hex.
     *
     * @param bytes the bytes
     * @return the hex text
     */
    static String toHex(byte[] bytes) {
        char[] out = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            out[i * 2] = HEX[v >>> 4];
            out[i * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(out);
    }

    /**
     * Decodes hex text to bytes.
     *
     * @param hex the hex text with an even length
     * @return the bytes
     * @throws IllegalArgumentException when the text is not valid hex
     */
    static byte[] fromHex(String hex) {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("hex length must be even");
        }
        byte[] out = new byte[hex.length() / 2];
        for (int i = 0; i < out.length; i++) {
            int hi = Character.digit(hex.charAt(i * 2), 16);
            int lo = Character.digit(hex.charAt(i * 2 + 1), 16);
            if (hi < 0 || lo < 0) {
                throw new IllegalArgumentException("invalid hex");
            }
            out[i] = (byte) ((hi << 4) | lo);
        }
        return out;
    }
}
