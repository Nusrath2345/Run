package com.run.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.run.services.PasswordCheckerService;

@RestController
@RequestMapping("/api/password-checker")
public class PasswordCheckerController {

    private final PasswordCheckerService passwordCheckerService;

    public PasswordCheckerController(PasswordCheckerService passwordCheckerService) {
        this.passwordCheckerService = passwordCheckerService;
    }

    @PostMapping("/check")
    public Map<String, Object> checkPassword(@RequestBody Map<String, String> request) {

        String password = request.get("password");

        String result = passwordCheckerService.checkStrength(password);

        Map<String, Object> response = new HashMap<>();
        response.put("strength", result);

        return response;
    }
}
