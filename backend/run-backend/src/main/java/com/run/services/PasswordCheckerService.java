package com.run.services;

import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class PasswordCheckerService {

    private static final List<String> COMMON_PASSWORDS = Arrays.asList(
        "password", "123456", "12345678", "qwerty", "abc123",
        "111111", "1234567890", "1234567", "password1", "12345"
    );

    public Map<String, Object> checkPassword(String password) {

        List<String> details = new ArrayList<>();
        int score = 0;

        // 🔹 Empty check
        if (password == null || password.isEmpty()) {
            details.add("Password cannot be empty");

            Map<String, Object> result = new HashMap<>();
            result.put("strength", "WEAK");
            result.put("details", details);
            return result;
        }

        // 🔹 Common passwords
        for (String common : COMMON_PASSWORDS) {
            if (password.toLowerCase().contains(common)) {
                details.add("Password is too common");
                break;
            }
        }

        // 🔹 Rules
        if (password.length() >= 8) score++;
        else details.add("Use at least 8 characters");

        if (password.length() >= 12) score++;

        if (password.matches(".*[A-Z].*")) score++;
        else details.add("Include uppercase letters");

        if (password.matches(".*[a-z].*")) score++;
        else details.add("Include lowercase letters");

        if (password.matches(".*\\d.*")) score++;
        else details.add("Include numbers");

        if (password.matches(".*[!@#$%^&*()].*")) score++;
        else details.add("Include special characters");

        // 🔹 Strength
        String strength;
        if (score <= 2) strength = "WEAK";
        else if (score <= 5) strength = "MEDIUM";
        else strength = "STRONG";

        // 🔹 Response
        Map<String, Object> result = new HashMap<>();
        result.put("strength", strength);
        result.put("details", details);

        return result;
    }
}
