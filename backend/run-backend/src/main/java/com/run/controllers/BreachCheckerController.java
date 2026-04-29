package com.run.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import com.run.services.BreachCheckerService;

@RestController
@RequestMapping("/api/breach-checker")
public class BreachCheckerController {

    private final BreachCheckerService breachCheckerService;

    public BreachCheckerController(BreachCheckerService breachCheckerService) {
        this.breachCheckerService = breachCheckerService;
    }

    @PostMapping("/check")
    public Map<String, Object> checkEmail(@RequestBody Map<String, String> request) {

        String email = request.get("email");

        String result = breachCheckerService.checkEmail(email);

        Map<String, Object> response = new HashMap<>();
        response.put("status", result); // BREACHED or CLEAR

        return response;
    }
}