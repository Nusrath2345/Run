package com.run.services;

import com.run.models.ScanResult;
import com.run.models.ScanType;
import com.run.repositories.ScanResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class LinkCheckerServiceTest {

    @Mock
    private ScanResultRepository scanResultRepository;

    @InjectMocks
    private LinkCheckerService service;

    @BeforeEach
    void setUp() {
        lenient().when(scanResultRepository.save(any(ScanResult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void httpsUrl_noIssues_returnsSafe() {
        ScanResult result = service.scan("https://example.com");
        assertEquals("SAFE", result.getResult());
        assertEquals(ScanType.LINK, result.getScanType());
        assertEquals("https://example.com", result.getTarget());
    }

    @Test
    void httpUrl_returnsSuspicious() {
        ScanResult result = service.scan("http://example.com");
        assertEquals("SUSPICIOUS", result.getResult());
        assertTrue(result.getDetails().contains("not encrypted"));
    }

    @Test
    void ipAddressHost_returnsSuspicious() {
        ScanResult result = service.scan("https://192.168.1.1/login");
        assertEquals("SUSPICIOUS", result.getResult());
        assertTrue(result.getDetails().contains("IP address"));
    }

    @Test
    void ipAddressWithHttp_returnsUnsafe() {
        ScanResult result = service.scan("http://192.168.1.1/login");
        assertEquals("UNSAFE", result.getResult());
    }

    @Test
    void urlShortener_returnsSuspicious() {
        ScanResult result = service.scan("https://bit.ly/abc123");
        assertEquals("SUSPICIOUS", result.getResult());
        assertTrue(result.getDetails().contains("shortener"));
    }

    @Test
    void suspiciousTld_returnsSuspicious() {
        ScanResult result = service.scan("https://example.xyz/page");
        assertEquals("SUSPICIOUS", result.getResult());
        assertTrue(result.getDetails().contains(".xyz"));
    }

    @Test
    void tooManySubdomains_returnsSuspicious() {
        ScanResult result = service.scan("https://a.b.c.d.example.com/page");
        assertEquals("SUSPICIOUS", result.getResult());
        assertTrue(result.getDetails().contains("subdomains"));
    }

    @Test
    void phishingKeyword_returnsUnsafe() {
        ScanResult result = service.scan("https://example.com/verify-account");
        assertEquals("UNSAFE", result.getResult());
        assertTrue(result.getDetails().contains("verify-account"));
    }

    @Test
    void longUrl_addesToScore() {
        String longPath = "/page?" + "x".repeat(130);
        ScanResult result = service.scan("https://example.com" + longPath);
        assertEquals("SUSPICIOUS", result.getResult());
        assertTrue(result.getDetails().contains("long"));
    }

    @Test
    void invalidUrl_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.scan("not a url at all"));
    }

    @Test
    void emptyUrl_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.scan(""));
    }

    @Test
    void result_getsSaved() {
        ScanResult saved = service.scan("https://example.com");
        assertNotNull(saved);
        assertEquals("https://example.com", saved.getTarget());
    }
}
