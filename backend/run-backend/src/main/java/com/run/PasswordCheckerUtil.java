package com.run;

import java.util.List;
import java.util.Arrays;

public class PasswordCheckerUtil 
{
   
    private static final List<String> COMMON_PASSWORDS = Arrays.asList("password", "123456", "12345678", "qwerty", "abc123", "111111", "1234567890", "1234567", "password1", "12345");
    public static String checkStrength(String password) 
    {
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
        if (password.length() >= 12) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*()].*")) score++;

        if (score <= 2) 
            return "WEAK";
        if (score <= 5) 
            return "MEDIUM";
        else 
            return "STRONG";
    }
    
}
