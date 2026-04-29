package com.run.controllers;

import com.run.dto.EmailScanRequest;
import com.run.services.EmailScannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email-scanner")
public class EmailScannerController {

    private final EmailScannerService emailScannerService;

    public EmailScannerController(EmailScannerService emailScannerService) {
        this.emailScannerService = emailScannerService;
    }

    @PostMapping("/scan")
    public ResponseEntity<String> scan(@RequestBody EmailScanRequest request) {
        String result = emailScannerService.scan(request.getContent());
        return ResponseEntity.ok(result);
    }
}
