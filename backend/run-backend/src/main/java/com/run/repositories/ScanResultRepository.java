package com.run.repositories;

import com.run.models.ScanResult;
import com.run.models.ScanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// handles saving and loading scan results from the database
// adam's scan history will use findAll, findByScanType, findByResult for filtering
// all scanner services call save() after a scan finishes
@Repository
public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {
    List<ScanResult> findByScanType(ScanType scanType);
    List<ScanResult> findByResult(String result);
}