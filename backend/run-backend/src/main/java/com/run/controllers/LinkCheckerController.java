package com.run.controllers;

import com.run.dto.ApiResponse;
import com.run.dto.LinkScanRequest;
import com.run.models.ScanResult;
import com.run.services.LinkCheckerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// POST /api/link-checker/scan
// body: { "url": "https://..." }
// returns ApiResponse with SAFE / SUSPICIOUS / UNSAFE
@RestController
@RequestMapping("/api/link-checker")
public class LinkCheckerController {

    private final LinkCheckerService linkCheckerService;

    public LinkCheckerController(LinkCheckerService linkCheckerService) {
        this.linkCheckerService = linkCheckerService;
    }

    @PostMapping("/scan")
    public ResponseEntity<ApiResponse<ScanResult>> scan(@RequestBody LinkScanRequest request) {
        ScanResult result = linkCheckerService.scan(request.getUrl());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}