package com.jerms.pdftools.webapp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jerms.pdftools.webapp.model.CrossWalkData;
import com.jerms.pdftools.webapp.model.MarketingData;
import com.jerms.pdftools.webapp.util.FileUtil;
import com.jerms.pdftools.webapp.util.PdfUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PdfToolsControllerTest {

    private PdfToolsController pdfToolsController;
    private CrossWalkData mockCrossWalkData;
    private MarketingData mockMarketingData;

    @BeforeEach
    public void setUp() {
        pdfToolsController = new PdfToolsController();
        mockCrossWalkData = new CrossWalkData();
        mockCrossWalkData.fileName = "test-file.xlsx";
        mockCrossWalkData.region = "test-region";

        mockMarketingData = new MarketingData();
        mockMarketingData.title = "test-title";
        mockMarketingData.pstageUrl = "http://pstage-url.com";
        mockMarketingData.prodUrl = "http://prod-url.com";
        mockMarketingData.region = "ps";
        mockMarketingData.percent = 0.5;
        mockMarketingData.exportFile = "test-export-file.xlsx";
    }

    @Test
    public void testReadExcel() {
        String result = pdfToolsController.readExcel(mockCrossWalkData);
        assertNotNull(result);
    }

    @Test
    public void testGetPdfContent() {
        String input = "file=test-file.pdf";
        String result = pdfToolsController.getPdfContent(input);
        assertNotNull(result);
    }

    @Test
    public void testGetPdfFields() {
        String input = "file=test-file.pdf";
        String result = pdfToolsController.getPdfFields(input);
        assertNotNull(result);
    }

    @Test
    public void testRenameAndRotatePdfs() {
        String input = "test-input";
        String result = pdfToolsController.renameAndRotatePdfs(input);
        assertNotNull(result);
    }

    @Test
    public void testCompareMarketNameAndTitle() {
        String result = pdfToolsController.compareMarketNameAndTitle(mockMarketingData);
        assertNotNull(result);
    }

    @Test
    public void testHandleFileUpload() throws IOException {
        MultipartFile mockFile = new MockMultipartFile("file", "test-file.txt", "text/plain", "test content".getBytes());
        Path path = Paths.get(FileUtil.UPLOAD_PATH + mockFile.getOriginalFilename());
        Files.createDirectories(path.getParent());

        String result = pdfToolsController.handleFileUpload(mockFile);
        JsonObject response = new Gson().fromJson(result, JsonObject.class);

        assertEquals("File uploaded successfully!", response.get("status").getAsString());
        assertTrue(Files.exists(path));
    }
}