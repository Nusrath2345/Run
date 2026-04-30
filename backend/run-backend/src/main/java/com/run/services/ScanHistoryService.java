package com.run.services;
 
import com.run.models.ScanResult;
import com.run.models.ScanType;
import com.run.repositories.ScanResultRepository;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
// sits between the controller and repository
// handles filtering logic for scan history
@Service
public class ScanHistoryService {
 
    private final ScanResultRepository scanResultRepository;
 
    public ScanHistoryService(ScanResultRepository scanResultRepository) {
        this.scanResultRepository = scanResultRepository;
    }
 
    // get all scan results
    public List<ScanResult> getAllResults() {
        return scanResultRepository.findAll();
    }
 
    // filter by scan type only
    public List<ScanResult> getByType(String type) {
        try {
            ScanType scanType = ScanType.valueOf(type.toUpperCase());
            return scanResultRepository.findByScanType(scanType);
        } catch (IllegalArgumentException e) {
            return scanResultRepository.findAll();
        }
    }
 
    // filter by result/risk level only
    public List<ScanResult> getByResult(String result) {
        return scanResultRepository.findByResult(result.toUpperCase());
    }
 
    // filter by both scan type and result
    public List<ScanResult> getFiltered(String type, String result) {
        if ((type == null || type.equals("ALL")) && (result == null || result.equals("ALL"))) {
            return getAllResults();
        }
        if (type != null && !type.equals("ALL") && (result == null || result.equals("ALL"))) {
            return getByType(type);
        }
        if ((type == null || type.equals("ALL")) && result != null && !result.equals("ALL")) {
            return getByResult(result);
        }
        // both filters active
        try {
            ScanType scanType = ScanType.valueOf(type.toUpperCase());
            return scanResultRepository.findByScanType(scanType)
                    .stream()
                    .filter(r -> r.getResult().equalsIgnoreCase(result))
                    .toList();
        } catch (IllegalArgumentException e) {
            return getByResult(result);
        }
    }
}