package com.run.controllers;

import com.run.dto.ApiResponse;
import com.run.models.ScanResult;
import com.run.services.FileScannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// POST /api/file-scanner/scan
// multipart file upload, field name = "file"
// returns ApiResponse with SAFE / SUSPICIOUS / UNSAFE
@RestController
@RequestMapping("/api/file-scanner")
public class FileScannerController {

    private final FileScannerService fileScannerService;

    public FileScannerController(FileScannerService fileScannerService) {
        this.fileScannerService = fileScannerService;
    }

    @PostMapping("/scan")
    public ResponseEntity<ApiResponse<ScanResult>> scan(@RequestParam("file") MultipartFile file)
            throws IOException {
        ScanResult result = fileScannerService.scan(file);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
