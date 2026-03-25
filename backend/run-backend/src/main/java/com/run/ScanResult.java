public class ScanResult {
    private String scanType;   // e.g. "EMAIL", "URL", "FILE"
    private String target;     // what was scanned, e.g. the URL or email
    private String riskLevel;  // "SAFE", "SUSPICIOUS", or "UNSAFE"
    private String details;    // short explanation of the result
    private String timestamp;  // date and time of the scan

    // Constructor
    public ScanResult(String scanType, String target, String riskLevel, String details, String timestamp) {
        this.scanType = scanType;
        this.target = target;
        this.riskLevel = riskLevel;
        this.details = details;
        this.timestamp = timestamp;
    }

    // Getters
    public String getScanType() {
        return scanType;
    }

    public String getTarget() {
        return target;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getDetails() {
        return details;
    }

    public String getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}