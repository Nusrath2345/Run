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
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class FileScannerServiceTest {

    @Mock
    private ScanResultRepository scanResultRepository;

    @InjectMocks
    private FileScannerService service;

    @BeforeEach
    void setUp() {
        lenient().when(scanResultRepository.save(any(ScanResult.class)))
                 .thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void pdfFile_returnsSafe() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf",
                "%PDF-1.4 content here".getBytes());
        ScanResult result = service.scan(file);
        assertEquals("SAFE", result.getResult());
        assertEquals(ScanType.FILE, result.getScanType());
        assertEquals("report.pdf", result.getTarget());
    }

    @Test
    void jpegImage_returnsSafe() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg",
                new byte[]{(byte)0xFF, (byte)0xD8, 0x00, 0x01});
        ScanResult result = service.scan(file);
        assertEquals("SAFE", result.getResult());
    }

    @Test
    void textFile_returnsSafe() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "notes.txt", "text/plain",
                "hello world".getBytes());
        ScanResult result = service.scan(file);
        assertEquals("SAFE", result.getResult());
    }

    @Test
    void zipArchive_returnsSuspicious() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "archive.zip", "application/zip",
                new byte[]{0x50, 0x4B, 0x03, 0x04});
        ScanResult result = service.scan(file);
        assertEquals("SUSPICIOUS", result.getResult());
        assertTrue(result.getDetails().contains("archive"));
    }

    @Test
    void jsFile_returnsSuspicious() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "script.js", "text/javascript",
                "alert('hello')".getBytes());
        ScanResult result = service.scan(file);
        assertEquals("SUSPICIOUS", result.getResult());
        assertTrue(result.getDetails().contains("script"));
    }

    @Test
    void macroExcel_returnsSuspicious() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "spreadsheet.xlsm",
                "application/vnd.ms-excel.sheet.macroenabled.12", "content".getBytes());
        ScanResult result = service.scan(file);
        assertEquals("SUSPICIOUS", result.getResult());
        assertTrue(result.getDetails().contains("macro"));
    }

    @Test
    void exeFile_returnsUnsafe() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "setup.exe", "application/octet-stream",
                new byte[]{0x4D, 0x5A, 0x00, 0x00});
        ScanResult result = service.scan(file);
        assertEquals("UNSAFE", result.getResult());
        assertTrue(result.getDetails().contains("executable"));
    }

    @Test
    void batFile_returnsUnsafe() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "run.bat", "text/plain",
                "@echo off".getBytes());
        ScanResult result = service.scan(file);
        assertEquals("UNSAFE", result.getResult());
    }

    @Test
    void ps1File_returnsUnsafe() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "payload.ps1", "text/plain",
                "Write-Host hello".getBytes());
        ScanResult result = service.scan(file);
        assertEquals("UNSAFE", result.getResult());
    }

    @Test
    void pdfWithMzHeader_returnsUnsafe() throws Exception {
        byte[] mzContent = new byte[]{0x4D, 0x5A, 0x00, 0x00, 0x01};
        MockMultipartFile file = new MockMultipartFile("file", "invoice.pdf", "application/pdf",
                mzContent);
        ScanResult result = service.scan(file);
        assertEquals("UNSAFE", result.getResult());
        assertTrue(result.getDetails().contains("MZ header"));
    }

    @Test
    void unsupportedExtension_throws() {
        MockMultipartFile file = new MockMultipartFile("file", "data.xyz123", "application/octet-stream",
                "data".getBytes());
        assertThrows(IllegalArgumentException.class, () -> service.scan(file));
    }

    @Test
    void emptyFile_throws() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> service.scan(file));
    }

    @Test
    void blankFilename_throws() {
        MockMultipartFile file = new MockMultipartFile("file", "", "application/octet-stream",
                "data".getBytes());
        assertThrows(IllegalArgumentException.class, () -> service.scan(file));
    }
}
