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

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class ExcelUtil {
    public static JsonArray readMappingFromExcel(CrossWalkData input) {
        JsonArray out = new JsonArray();

        File file = new File(URLDecoder.decode(input.fileName, StandardCharsets.UTF_8));
        String exportFile = createExportFile();
        // Create a workbook object
        try {
            OPCPackage pkg = OPCPackage.open(file);
            XSSFWorkbook workbook = new XSSFWorkbook(pkg);

            // Get the first sheet
            XSSFSheet sheet = workbook.getSheetAt(0);

            XSSFRow header = sheet.getRow(0);
            // Iterate over the rows
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                // Get the row
                XSSFRow row = sheet.getRow(i);

                JsonObject r = new JsonObject();

                // Iterate over the cells
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    // Get the cell
                    XSSFCell cell = row.getCell(j);
                    r.addProperty(header.getCell(j).getStringCellValue(), cell.getStringCellValue());
                }
                r.addProperty("region", input.region);
                r.addProperty("exportFile", exportFile);
                out.add(r);
            }
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error while reading Excel File!! "+ e.getMessage());
        }
        return out;
    }

    public static String createExportFile() {
        try {
            String fileName = "/MarketingNameExport_" + LocalDateTime.now() + ".xlsx";
            // creates an Excel file at the specified location
            File fileOut = new File(fileName);
            fileOut.createNewFile();
            return fileName;
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean addRecordToExportFile(MarketingData data) {
        // Create a workbook object
        try {
            File file = new File(URLDecoder.decode(data.exportFile, StandardCharsets.UTF_8));
            OPCPackage pkg = OPCPackage.open(file);
            XSSFWorkbook wb = new XSSFWorkbook(pkg);
            XSSFSheet sheet;
            if (wb.getNumberOfSheets() == 0) {
                sheet = wb.createSheet("Export");
            } else {
                sheet = wb.getSheet("Export");
            }
            if (sheet.getLastRowNum() == 0) {
                XSSFRow header = sheet.createRow(0);
                header.createCell(0).setCellValue("Link");
                header.createCell(1).setCellValue("Marketing Name");
                header.createCell(2).setCellValue("Match %");
            }
            XSSFRow record = sheet.createRow(sheet.getLastRowNum() + 1);
            record.createCell(0).setCellFormula("HYPERLINK("+data.getUrl()+")");
            record.createCell(1).setCellValue(data.title);
            record.createCell(2).setCellValue(100 - data.percent * 100);

            FileOutputStream fileOut = new FileOutputStream(file);
            wb.write(fileOut);
            //closing the Stream
            fileOut.close();
            //closing the workbook
            wb.close();
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error while reading Excel File!! "+ e.getMessage());
            return false;
        }

        return true;
    }
}
