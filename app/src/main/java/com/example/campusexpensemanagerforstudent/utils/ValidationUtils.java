package com.example.campusexpensemanagerforstudent.utils;

import android.util.Patterns;

public class ValidationUtils {
    
    public static boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() && 
               Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        // Vietnamese phone number format: 10 digits starting with 0
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        return cleanPhone.length() == 10 && cleanPhone.startsWith("0");
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() >= 2;
    }

    public static boolean isValidAmount(String amount) {
        if (amount == null || amount.trim().isEmpty()) return false;
        try {
            double value = Double.parseDouble(amount);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
