package com.run.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// shared model for all scan results
// every scanner saves to this table, adam's scan history reads from it
//
// result values depend on the module:
//   link/file checker  -> SAFE, SUSPICIOUS, UNSAFE
//   email scanner      -> SAFE, SUSPICIOUS, SCAM
//   password checker   -> WEAK, MEDIUM, STRONG
//   breach checker     -> CLEAR, BREACHED
@Entity
@Table(name = "scan_results")
public class ScanResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "scan_type", nullable = false, length = 20)
    private ScanType scanType;

    @Column(nullable = false, length = 500)
    private String target;

    @Column(nullable = false, length = 50)
    private String result;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // automatically set the timestamp when saving
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // no-arg constructor required by JPA
    public ScanResult() {}

    public ScanResult(ScanType scanType, String target, String result, String details) {
        this.scanType = scanType;
        this.target = target;
        this.result = result;
        this.details = details;
    }

    // getters
    public Long getId() { return id; }
    public ScanType getScanType() { return scanType; }
    public String getTarget() { return target; }
    public String getResult() { return result; }
    public String getDetails() { return details; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // setters
    public void setScanType(ScanType scanType) { this.scanType = scanType; }
    public void setTarget(String target) { this.target = target; }
    public void setResult(String result) { this.result = result; }
    public void setDetails(String details) { this.details = details; }
}