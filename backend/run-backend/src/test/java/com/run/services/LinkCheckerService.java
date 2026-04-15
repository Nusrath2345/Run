package com.run.services;

import com.run.models.ScanResult;
import com.run.models.ScanType;
import com.run.repositories.ScanResultRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// checks a url for risk indicators and scores it
// 0 = SAFE, 1-3 = SUSPICIOUS, 4+ = UNSAFE
@Service
public class LinkCheckerService {

    private static final Set<String> URL_SHORTENERS = Set.of(
            "bit.ly", "tinyurl.com", "t.co", "goo.gl", "ow.ly",
            "short.link", "buff.ly", "tiny.cc", "is.gd", "cli.gs"
    );

    private static final Set<String> SUSPICIOUS_TLDS = Set.of(
            "xyz", "tk", "ml", "ga", "cf", "gq", "top", "work", "click", "loan"
    );

    private static final List<String> PHISHING_KEYWORDS = List.of(
            "verify-account", "confirm-identity", "secure-login",
            "update-payment", "suspended-account", "validate-account"
    );

    private final ScanResultRepository scanResultRepository;

    public LinkCheckerService(ScanResultRepository scanResultRepository) {
        this.scanResultRepository = scanResultRepository;
    }

    // scans url and saves result to db
    public ScanResult scan(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL must not be empty.");
        }

        URI uri;
        try {
            uri = new URI(url).parseServerAuthority();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + e.getMessage());
        }

        String scheme = uri.getScheme();
        String host = uri.getHost();

        if (scheme == null || host == null) {
            throw new IllegalArgumentException("URL must include a scheme (http/https) and a host.");
        }

        List<String> warnings = new ArrayList<>();
        int score = 0;

        // http instead of https
        if ("http".equalsIgnoreCase(scheme)) {
            warnings.add("Connection is not encrypted (HTTP instead of HTTPS)");
            score += 1;
        } else if (!"https".equalsIgnoreCase(scheme)) {
            warnings.add("Unusual URL scheme: " + scheme);
            score += 3;
        }

        // ip address as host
        if (host.matches("\\d{1,3}(\\.\\d{1,3}){3}")) {
            warnings.add("IP address used instead of a domain name");
            score += 3;
        }

        // url shortener
        if (URL_SHORTENERS.contains(host.toLowerCase())) {
            warnings.add("URL shortener detected — the real destination is hidden");
            score += 2;
        }

        // suspicious tld
        int lastDot = host.lastIndexOf('.');
        if (lastDot >= 0) {
            String tld = host.substring(lastDot + 1).toLowerCase();
            if (SUSPICIOUS_TLDS.contains(tld)) {
                warnings.add("Suspicious top-level domain: ." + tld);
                score += 2;
            }
        }

        // too many subdomains (more than 3 dots)
        long dotCount = host.chars().filter(c -> c == '.').count();
        if (dotCount > 3) {
            warnings.add("Unusually high number of subdomains (" + dotCount + " levels)");
            score += 2;
        }

        // phishing keywords in url
        String urlLower = url.toLowerCase();
        for (String keyword : PHISHING_KEYWORDS) {
            if (urlLower.contains(keyword)) {
                warnings.add("Suspicious phishing keyword in URL: '" + keyword + "'");
                score += 4;
                break;  // only count once
            }
        }

        // really long url
        if (url.length() > 150) {
            warnings.add("Unusually long URL (" + url.length() + " characters)");
            score += 1;
        }

        // decide result based on total score
        String result;
        String details;
        if (score == 0) {
            result = "SAFE";
            details = "No threats detected. The URL appears legitimate.";
        } else if (score <= 3) {
            result = "SUSPICIOUS";
            details = "Risk indicators found: " + String.join("; ", warnings) + ".";
        } else {
            result = "UNSAFE";
            details = "Multiple risk indicators found: " + String.join("; ", warnings) + ".";
        }

        return scanResultRepository.save(new ScanResult(ScanType.LINK, url, result, details));
    }
}