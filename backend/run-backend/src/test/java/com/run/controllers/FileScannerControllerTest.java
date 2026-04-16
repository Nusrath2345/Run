package com.run.controllers;

import com.run.dto.ApiResponse;
import com.run.models.ScanResult;
import com.run.models.ScanType;
import com.run.services.FileScannerService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileScannerControllerTest {

    private final FileScannerService service = mock(FileScannerService.class);
    private final FileScannerController controller = new FileScannerController(service);

    @Test
    void scan_callsService_andWrapsResult() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf",
                "application/pdf", "content".getBytes());
        ScanResult mockResult = new ScanResult(ScanType.FILE, "report.pdf", "SAFE",
                "No threats detected.");
        when(service.scan(file)).thenReturn(mockResult);

        ResponseEntity<ApiResponse<ScanResult>> response = controller.scan(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("SAFE", response.getBody().getData().getResult());
        assertEquals("report.pdf", response.getBody().getData().getTarget());
        verify(service).scan(file);
    }

    @Test
    void scan_unsupportedType_throws() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "data.xyz123",
                "application/octet-stream", "data".getBytes());
        when(service.scan(file)).thenThrow(new IllegalArgumentException("Unsupported file type: .xyz123"));

        assertThrows(IllegalArgumentException.class, () -> controller.scan(file));
    }
}
