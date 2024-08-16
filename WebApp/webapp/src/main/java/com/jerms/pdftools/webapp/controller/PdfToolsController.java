package com.jerms.pdftools.webapp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jerms.pdftools.webapp.model.CrossWalkData;
import com.jerms.pdftools.webapp.model.MarketingData;
import com.jerms.pdftools.webapp.model.RenameAndRotateInput;
import com.jerms.pdftools.webapp.util.FileUtil;
import com.jerms.pdftools.webapp.util.PdfUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@RestController
public class PdfToolsController {

    @PostMapping("/readExcel")
    public String readExcel(@RequestBody CrossWalkData input) {
        return new Gson().toJson(PdfUtil.compareMarketingNameGivenFile(input));
    }

    @PostMapping("/getPdfContent")
    public String getPdfContent(@RequestBody String input) {
        String fileName = input.replaceAll("file=", "");
        String pdfUrl = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
        return PdfUtil.getDocumentString(pdfUrl);
    }

    @PostMapping("/getPdfFields")
    public String getPdfFields(@RequestBody String input) {
        String fileName = input.replaceAll("file=", "");
        String pdfUrl = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
        return String.join("<br/>", PdfUtil.getFields(pdfUrl));
    }

    @PostMapping(value = "/renameAndRotate")
    public String renameAndRotatePdfs(@RequestBody RenameAndRotateInput input) {
        ArrayList<String> output = PdfUtil.rotateAndRename(input);
        return String.join("<br>", output);
    }

    @PostMapping(value = "/compareMarketName")
    public String compareMarketNameAndTitle(@RequestBody MarketingData input) {
        String url = URLDecoder.decode(input.getUrl(), StandardCharsets.UTF_8);
        input.percent = PdfUtil.compareMarketingNameGivenUrl(url, input.title);
        boolean addedToExport = false; // ExcelUtil.addRecordToExportFile(input);
        return input.getHTMLResponse(addedToExport);
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        Path path = Paths.get(FileUtil.UPLOAD_PATH + file.getOriginalFilename());
        Files.write(path, file.getBytes());
        JsonObject response = new JsonObject();
        response.addProperty("path", path.toString());
        if (Files.exists(path)) {
            response.addProperty("status", "File uploaded successfully!");
        } else {
            response.addProperty("status", "Error during file upload.");
        }
        return new Gson().toJson(response);
    }
}
