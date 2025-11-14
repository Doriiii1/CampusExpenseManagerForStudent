package com.example.campusexpensemanagerforstudent.utils;

import android.util.Patterns;

public class ValidationHelper {

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validate phone number (Vietnamese format)
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) return true; // Optional field
        return phone.matches("^0[0-9]{9}$"); // Vietnamese phone format: 0XXXXXXXXX
    }

    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    /**
     * Check if passwords match
     */
    public static boolean passwordsMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }

    /**
     * Check if string is not empty
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Validate amount
     */
    public static boolean isValidAmount(String amount) {
        if (amount == null || amount.isEmpty()) return false;
        try {
            double value = Double.parseDouble(amount);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
