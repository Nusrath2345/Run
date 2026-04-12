package com.run;

public class PasswordCheckerUtil 
{
    public static String checkStrength(String password) {
        int score = 0;

        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*()].*")) score++;

        if (score <= 2) 
            return "WEAK";
        if (score <= 4) 
            return "MEDIUM";
        else 
            return "STRONG";
    }
    
}
