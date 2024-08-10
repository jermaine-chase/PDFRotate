package com.jerms.pdftools.webapp.util;

import com.google.gson.JsonArray;
import com.jerms.pdftools.webapp.model.CrossWalkData;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PdfUtilTest {

    @Test
    void compareMarketingNameGivenUrl_shouldReturnSimilarityScore() {
        // Arrange
        String testPdfUrl = "http://example.com/test.pdf";
        String marketingName = "Test Marketing Name";
        PdfUtil pdfUtilSpy = Mockito.spy(PdfUtil.class);

        // Mocking the downloadPdf and getDocumentString methods
        Mockito.doReturn("test.pdf").when(pdfUtilSpy).downLoadPdf(testPdfUrl);
        Mockito.doReturn("Marketing Name: Test Marketing Name\nCoverage: Test").when(pdfUtilSpy).getDocumentString("test.pdf");

        // Act
        double similarityScore = pdfUtilSpy.compareMarketingNameGivenUrl(testPdfUrl, marketingName);

        // Assert
        assertEquals(1.0, similarityScore);
    }

    @Test
    void getDocumentString_shouldReturnTextFromPdf() throws IOException {
        // Arrange
        String pdfUrl = "src/test/resources/test.pdf";
        // Create a temporary PDF file for testing
        File tempFile = Files.createTempFile("test", ".pdf").toFile();
        Files.write(tempFile.toPath(), "%PDF-1.4\n%äÿóÀ\n1 0 obj\n<<\n/Type /Catalog\n/Pages 2 0 R\n>>\nendobj\n2 0 obj\n<<\n/Type /Pages\n/Kids [3 0 R]\n/Count 1\n>>\nendobj\n3 0 obj\n<<\n/Type /Page\n/Parent 2 0 R\n/MediaBox [0 0 612 792]\n/Contents 4 0 R\n/Resources <<\n/Font <<\n/F1 5 0 R\n>>\n>>\n>>\nendobj\n4 0 obj\n<<\n/Length 44\n>>\nstream\nBT\n/F1 24 Tf\n72 712 Td\n(Hello, PDFBox!) Tj\nET\nendstream\nendobj\n5 0 obj\n<<\n/Type /Font\n/Subtype /Type1\n/BaseFont /Helvetica\n>>\nendobj\nxref\n0 6\n0000000000 65535 f\n0000000018 00000 n\n0000000077 00000 n\n0000000178 00000 n\n0000000292 00000 n\n0000000370 00000 n\ntrailer\n<<\n/Size 6\n/Root 1 0 R\n>>\nstartxref\n437\n%%EOF".getBytes());

        // Act
        String documentString = PdfUtil.getDocumentString(tempFile.getAbsolutePath());

        // Assert
        assertNotNull(documentString);
        assertTrue(documentString.contains("Hello, PDFBox!"));

        // Cleanup
        tempFile.delete();
    }

    @Test
    void downLoadPdf_shouldReturnFilePath() {
        // Arrange
        String testUrl = "https://s29.q4cdn.com/175625835/files/doc_downloads/test.pdf";

        // Mocking the URI and InputStream
        PdfUtil pdfUtilSpy = Mockito.spy(PdfUtil.class);
        //Mockito.doReturn("test.pdf").when(pdfUtilSpy).generatePdfName(testUrl);

        // Act
        String downloadedFilePath = pdfUtilSpy.downLoadPdf(testUrl);

        // Assert
        assertNotNull(downloadedFilePath);
        assertEquals("test.pdf", downloadedFilePath);
    }

    @Test
    void getFields_shouldReturnListOfFields() {
        // Arrange
        String testPdfUrl = "src/test/resources/testForm.pdf";
        PdfUtil pdfUtilSpy = Mockito.spy(PdfUtil.class);

        // Create a temporary PDF file for testing
        File tempFile = null;
        try {
            tempFile = Files.createTempFile("testForm", ".pdf").toFile();
            Files.write(tempFile.toPath(), "%PDF-1.4\n%äÿóÀ\n1 0 obj\n<<\n/Type /Catalog\n/Pages 2 0 R\n>>\nendobj\n2 0 obj\n<<\n/Type /Pages\n/Kids [3 0 R]\n/Count 1\n>>\nendobj\n3 0 obj\n<<\n/Type /Page\n/Parent 2 0 R\n/MediaBox [0 0 612 792]\n/Contents 4 0 R\n/Resources <<\n/Font <<\n/F1 5 0 R\n>>\n>>\n>>\nendobj\n4 0 obj\n<<\n/Length 44\n>>\nstream\nBT\n/F1 24 Tf\n72 712 Td\n(Hello, PDFBox!) Tj\nET\nendstream\nendobj\n5 0 obj\n<<\n/Type /Font\n/Subtype /Type1\n/BaseFont /Helvetica\n>>\nendobj\nxref\n0 6\n0000000000 65535 f\n0000000018 00000 n\n0000000077 00000 n\n0000000178 00000 n\n0000000292 00000 n\n0000000370 00000 n\ntrailer\n<<\n/Size 6\n/Root 1 0 R\n>>\nstartxref\n437\n%%EOF".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Act
        ArrayList<String> fields = pdfUtilSpy.getFields(tempFile.getAbsolutePath());

        // Assert
        assertNotNull(fields);
        assertTrue(fields.isEmpty());

        // Cleanup
        tempFile.delete();
    }
}
