package com.run.services;

import com.run.models.ScanResult;
import com.run.models.ScanType;
import com.run.repositories.ScanResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Service
public class FileScannerService {

    private static final Set<String> SAFE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg",
            "txt", "csv", "log", "md",
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"
    );

    private static final Set<String> SUSPICIOUS_EXTENSIONS = Set.of(
            "zip", "rar", "7z", "tar", "gz",
            "js", "vbs", "py", "rb", "pl", "php",
            "docm", "xlsm", "pptm"
    );

    private static final Set<String> UNSAFE_EXTENSIONS = Set.of(
            "exe", "com", "scr", "msi", "dll",
            "bat", "cmd", "sh", "ps1"
    );

    private final ScanResultRepository scanResultRepository;

    public FileScannerService(ScanResultRepository scanResultRepository) {
        this.scanResultRepository = scanResultRepository;
    }

    public ScanResult scan(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("File must have a name.");
        }
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty.");
        }

        String ext = getExtension(filename);
        if (ext.isEmpty()) {
            throw new IllegalArgumentException("File has no extension - cannot determine type.");
        }

        boolean isUnsafeExt     = UNSAFE_EXTENSIONS.contains(ext);
        boolean isSuspiciousExt = SUSPICIOUS_EXTENSIONS.contains(ext);
        boolean isSafeExt       = SAFE_EXTENSIONS.contains(ext);

        if (!isUnsafeExt && !isSuspiciousExt && !isSafeExt) {
            throw new IllegalArgumentException("Unsupported file type: ." + ext);
        }

        String result;
        String details;

        if (isUnsafeExt) {
            result  = "UNSAFE";
            details = "Dangerous file type detected: ." + ext + " is an executable or system script.";
        } else if (isSuspiciousExt) {
            result  = "SUSPICIOUS";
            details = "." + ext + " files may contain " + categoryLabel(ext) + " content. Review before opening.";
        } else {
            result  = "SAFE";
            details = "No threats detected. ." + ext + " is a recognised safe file type.";
        }

        // magic byte check: if the first 2 bytes are MZ (windows exe header)
        // but the extension says its something else, its probably a disguised exe
        if (!isUnsafeExt) {
            byte[] bytes = file.getBytes();
            if (bytes.length >= 2 && bytes[0] == 0x4D && bytes[1] == 0x5A) {
                result  = "UNSAFE";
                details = "Executable content detected (MZ header) despite ." + ext
                        + " extension; file may be a disguised Windows executable.";
            }
        }

        return scanResultRepository.save(new ScanResult(ScanType.FILE, filename, result, details));
    }

    // gets lowercase extension from filename
    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot >= 0 && dot < filename.length() - 1)
                ? filename.substring(dot + 1).toLowerCase()
                : "";
    }

    // human readable label for the suspicious category
    private String categoryLabel(String ext) {
        return switch (ext) {
            case "zip", "rar", "7z", "tar", "gz" -> "archive";
            case "js", "vbs", "py", "rb", "pl", "php" -> "script";
            case "docm", "xlsm", "pptm" -> "macro";
            default -> "potentially dangerous";
        };
    }
}
