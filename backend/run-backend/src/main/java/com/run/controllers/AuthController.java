package com.run.controllers;

import com.run.dto.LoginRequest;
import com.run.dto.RegisterRequest;
import com.run.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String result = authService.register(request);
        if (result.equals("EMAIL_TAKEN")) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
        return ResponseEntity.ok("Account created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String result = authService.login(request);
        switch (result) {
            case "SUCCESS": return ResponseEntity.ok("Login successful");
            case "WRONG_PASSWORD": return ResponseEntity.badRequest().body("Incorrect password");
            default: return ResponseEntity.badRequest().body("Account not found");
        }
    }
}