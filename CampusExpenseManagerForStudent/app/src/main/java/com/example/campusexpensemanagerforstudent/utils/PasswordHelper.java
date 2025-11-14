package com.example.campusexpensemanagerforstudent.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHelper {

    /**
     * Hash password using SHA-256
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verify password against hash
     */
    public static boolean verifyPassword(String password, String hash) {
        String passwordHash = hashPassword(password);
        return passwordHash != null && passwordHash.equals(hash);
    }

    /**
     * Convert byte array to hexadecimal string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Validate password strength
     */
    public static boolean isPasswordStrong(String password) {
        return password != null && password.length() >= 8;
    }
}
