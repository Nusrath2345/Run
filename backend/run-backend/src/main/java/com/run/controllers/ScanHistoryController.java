package com.run.controllers;
 
import com.run.dto.ApiResponse;
import com.run.models.ScanResult;
import com.run.services.ScanHistoryService;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
// REST endpoints for the scan history panel
// GET /api/scan-history              -> all results
// GET /api/scan-history?type=LINK    -> filtered by scan type
// GET /api/scan-history?result=SAFE  -> filtered by result
// GET /api/scan-history?type=LINK&result=SAFE -> both filters
@RestController
@RequestMapping("/api/scan-history")
public class ScanHistoryController {
 
    private final ScanHistoryService scanHistoryService;
 
    public ScanHistoryController(ScanHistoryService scanHistoryService) {
        this.scanHistoryService = scanHistoryService;
    }
 
    @GetMapping
    public ApiResponse<List<ScanResult>> getHistory(
            @RequestParam(required = false, defaultValue = "ALL") String type,
            @RequestParam(required = false, defaultValue = "ALL") String result) {
        try {
            List<ScanResult> records = scanHistoryService.getFiltered(type, result);
            return ApiResponse.success(records);
        } catch (Exception e) {
            return ApiResponse.error("Failed to load scan history: " + e.getMessage());
        }
    }
}