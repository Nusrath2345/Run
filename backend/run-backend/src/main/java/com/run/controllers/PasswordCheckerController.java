package com.run.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.run.services.PasswordCheckerService;
import com.run.repositories.ScanResultRepository;
import com.run.models.ScanResult;
import com.run.models.ScanType;

@RestController
@RequestMapping("/api/password-checker")
public class PasswordCheckerController {

    private final PasswordCheckerService passwordCheckerService;
    private final ScanResultRepository scanResultRepository;

    public PasswordCheckerController(PasswordCheckerService passwordCheckerService,
                                     ScanResultRepository scanResultRepository) {
        this.passwordCheckerService = passwordCheckerService;
        this.scanResultRepository = scanResultRepository;
    }

    @PostMapping("/check")
    public Map<String, Object> checkPassword(@RequestBody Map<String, String> request) {

        String password = request.get("password");

        Map<String, Object> result = passwordCheckerService.checkPassword(password);

        // 🔹 Save to DB
        scanResultRepository.save(
            new ScanResult(
                ScanType.PASSWORD,
                password,
                (String) result.get("strength"),
                result.get("details").toString()
            )
        );

        return result;
    }
}