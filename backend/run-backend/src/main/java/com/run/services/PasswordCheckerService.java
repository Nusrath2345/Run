package com.run.services;

import java.util.List;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

@Service
public class PasswordCheckerService 
{
    
    private static final List<String> COMMON_PASSWORDS = Arrays.asList("password", "123456", "12345678", "qwerty", "abc123", "111111", "1234567890", "1234567", "password1", "12345");
    public String checkStrength(String password) 
    {
        List<String> suggestions = new ArrayList<>();

        for (String common : COMMON_PASSWORDS) 
        {
            if (password.toLowerCase().contains(common)) 
            {
                return "WEAK";
            }
        }

        
        int score = 0;

        if (password == null || password.isEmpty()) 
        {
        return "No password provided";
        }

        if (password.length() >= 8) score++;
        else suggestions.add("Use at least 8 characters");

        if (password.length() >= 12) score++;

        if (password.matches(".*[A-Z].*")) score++;
        else suggestions.add("Include uppercase letters");

        if (password.matches(".*[a-z].*")) score++;
        else suggestions.add("Include lowercase letters");

        if (password.matches(".*\\d.*")) score++;
        else suggestions.add("Include numbers");

        if (password.matches(".*[!@#$%^&*()].*")) score++;
        else suggestions.add("Include special characters");

        if (score <= 2) 
            return "WEAK";
        if (score <= 5) 
            return "MEDIUM";
        else 
            return "STRONG";
    }
    
}
