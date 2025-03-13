package com.megacitycab.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

public class PasswordUtil {


    private static final int ITERATIONS = 10000; 
    private static final int KEY_LENGTH = 256; 
    private static final int SALT_LENGTH = 16;


    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }


    public static String hashPassword(String password, String salt) {
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }


    public static boolean verifyPassword(String plainPassword, String hashedPassword, String salt) {
        String inputHash = hashPassword(plainPassword, salt);
        return inputHash.equals(hashedPassword);
    }

    public static void main(String[] args) {
        String password = "mypassword123";
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        System.out.println("Salt: " + salt);
        System.out.println("Hashed Password: " + hashedPassword);


        boolean isMatch = verifyPassword("mypassword123", hashedPassword, salt);
        System.out.println("Password match: " + isMatch);
    }
}
