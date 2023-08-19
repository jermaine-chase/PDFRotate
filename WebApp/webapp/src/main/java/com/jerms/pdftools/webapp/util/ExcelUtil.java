package com.jerms.pdftools.webapp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jerms.pdftools.webapp.model.CrossWalkData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ExcelUtil {
    public static JsonArray readMappingFromExcel(CrossWalkData input) {
        JsonArray out = new JsonArray();

        File file = new File(URLDecoder.decode(input.fileName, StandardCharsets.UTF_8));

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
                out.add(r);
            }
        } catch (IOException | InvalidFormatException e) {
            System.err.println("Error while reading Excel File!! "+ e.getMessage());
        }

        return out;
    }
}
