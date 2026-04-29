package com.run.services;

import org.springframework.stereotype.Service;

@Service
public class EmailScannerService {

    public String scan(String content) {
        if (content == null || content.isBlank()) return "SAFE";

        String lower = content.toLowerCase();
        int score = 0;

        // Urgency indicators
        if (lower.contains("act now")) score += 2;
        if (lower.contains("urgent")) score += 2;
        if (lower.contains("immediately")) score += 1;
        if (lower.contains("account suspended")) score += 3;
        if (lower.contains("verify your account")) score += 3;
        if (lower.contains("confirm your details")) score += 3;
        if (lower.contains("limited time")) score += 1;
        if (lower.contains("expires soon")) score += 1;

        // Personal info requests
        if (lower.contains("enter your password")) score += 3;
        if (lower.contains("provide your")) score += 2;
        if (lower.contains("bank details")) score += 3;
        if (lower.contains("credit card")) score += 2;
        if (lower.contains("social security")) score += 3;

        // Too good to be true
        if (lower.contains("you have won")) score += 3;
        if (lower.contains("you've won")) score += 3;
        if (lower.contains("claim your prize")) score += 3;
        if (lower.contains("free gift")) score += 2;
        if (lower.contains("lottery")) score += 2;
        if (lower.contains("winner")) score += 1;

        // Generic greetings
        if (lower.contains("dear customer")) score += 2;
        if (lower.contains("dear user")) score += 2;
        if (lower.contains("dear account holder")) score += 2;

        // Link/click bait
        if (lower.contains("click here")) score += 2;
        if (lower.contains("click the link")) score += 2;
        if (lower.contains("follow this link")) score += 2;

        if (score >= 8) return "SCAM";
        if (score >= 3) return "SUSPICIOUS";
        return "SAFE";
    }
}