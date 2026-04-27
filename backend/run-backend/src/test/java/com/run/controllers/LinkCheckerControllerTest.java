package com.run.controllers;

import com.run.dto.ApiResponse;
import com.run.dto.LinkScanRequest;
import com.run.models.ScanResult;
import com.run.models.ScanType;
import com.run.services.LinkCheckerService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LinkCheckerControllerTest {

    private final LinkCheckerService service = mock(LinkCheckerService.class);
    private final LinkCheckerController controller = new LinkCheckerController(service);

    @Test
    void scan_callsService_andWrapsResult() {
        ScanResult mockResult = new ScanResult(ScanType.LINK, "https://example.com", "SAFE", "No threats detected.");
        when(service.scan("https://example.com")).thenReturn(mockResult);

        ResponseEntity<ApiResponse<ScanResult>> response =
                controller.scan(new LinkScanRequest("https://example.com"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("SAFE", response.getBody().getData().getResult());
        assertEquals("https://example.com", response.getBody().getData().getTarget());
        verify(service).scan("https://example.com");
    }

    @Test
    void scan_emptyUrl_throwsException() {
        when(service.scan("")).thenThrow(new IllegalArgumentException("URL must not be empty."));

        assertThrows(IllegalArgumentException.class,
                () -> controller.scan(new LinkScanRequest("")));
    }
}