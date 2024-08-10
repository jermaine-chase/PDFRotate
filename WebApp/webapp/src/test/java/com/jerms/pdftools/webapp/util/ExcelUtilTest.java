package com.jerms.pdftools.webapp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jerms.pdftools.webapp.model.CrossWalkData;
import com.jerms.pdftools.webapp.model.MarketingData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExcelUtilTest {

    private CrossWalkData mockCrossWalkData;
    private MarketingData mockMarketingData;

    @BeforeEach
    public void setUp() {
        mockCrossWalkData = new CrossWalkData();
        mockCrossWalkData.fileName = "test-file.xlsx";
        mockCrossWalkData.region = "test-region";

        mockMarketingData = new MarketingData();
        mockMarketingData.exportFile = "test-export-file.xlsx";
        mockMarketingData.title = "test-title";
        mockMarketingData.percent = 0.5;
    }

    @Test
    public void testReadMappingFromExcel() throws IOException, InvalidFormatException {
        File mockFile = mock(File.class);
        OPCPackage mockPkg = mock(OPCPackage.class);
        XSSFWorkbook mockWorkbook = mock(XSSFWorkbook.class);
        XSSFSheet mockSheet = mock(XSSFSheet.class);
        XSSFRow mockRow = mock(XSSFRow.class);
        XSSFCell mockCell = mock(XSSFCell.class);

        when(mockFile.exists()).thenReturn(true);
        when(OPCPackage.open(mockFile)).thenReturn(mockPkg);
        when(new XSSFWorkbook(mockPkg)).thenReturn(mockWorkbook);
        when(mockWorkbook.getSheetAt(0)).thenReturn(mockSheet);
        when(mockSheet.getPhysicalNumberOfRows()).thenReturn(2);
        when(mockSheet.getRow(0)).thenReturn(mockRow);
        when(mockSheet.getRow(1)).thenReturn(mockRow);
        when(mockRow.getPhysicalNumberOfCells()).thenReturn(1);
        when(mockRow.getCell(0)).thenReturn(mockCell);
        when(mockCell.getStringCellValue()).thenReturn("test-value");

        JsonArray result = ExcelUtil.readMappingFromExcel(mockCrossWalkData);

        assertNotNull(result);
        assertEquals(1, result.size());
        JsonObject firstEntry = result.get(0).getAsJsonObject();
        assertEquals("test-value", firstEntry.get("test-value").getAsString());
        assertEquals("test-region", firstEntry.get("region").getAsString());
    }

    @Test
    public void testCreateExportFile() {
        String result = ExcelUtil.createExportFile();
        assertNotNull(result);
        assertTrue(result.contains("MarketingNameExport_"));
    }

    @Test
    public void testAddRecordToExportFile() throws IOException, InvalidFormatException {
        File mockFile = mock(File.class);
        OPCPackage mockPkg = mock(OPCPackage.class);
        XSSFWorkbook mockWorkbook = mock(XSSFWorkbook.class);
        XSSFSheet mockSheet = mock(XSSFSheet.class);
        XSSFRow mockRow = mock(XSSFRow.class);
        XSSFCell mockCell = mock(XSSFCell.class);
        FileOutputStream mockFileOut = mock(FileOutputStream.class);

        when(mockFile.exists()).thenReturn(true);
        when(OPCPackage.open(mockFile)).thenReturn(mockPkg);
        when(new XSSFWorkbook(mockPkg)).thenReturn(mockWorkbook);
        when(mockWorkbook.getSheet("Export")).thenReturn(mockSheet);
        when(mockSheet.getLastRowNum()).thenReturn(0);
        when(mockSheet.createRow(1)).thenReturn(mockRow);
        when(mockRow.createCell(0)).thenReturn(mockCell);

        boolean result = ExcelUtil.addRecordToExportFile(mockMarketingData);

        assertTrue(result);
    }
}
