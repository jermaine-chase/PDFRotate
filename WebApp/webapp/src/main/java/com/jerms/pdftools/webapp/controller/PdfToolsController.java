package com.jerms.pdftools.webapp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jerms.pdftools.webapp.model.CrossWalkData;
import com.jerms.pdftools.webapp.model.MarketingData;
import com.jerms.pdftools.webapp.util.PdfUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        return PdfUtil.getDocumentString(URLDecoder.decode(fileName, StandardCharsets.UTF_8));
    }

    @PostMapping("/renameAndRotate")
    public String renameAndRotatePdfs(@RequestBody String input) {
        JsonObject request = JsonParser.parseString(
                URLDecoder.decode(input.substring(0, input.length() - 1), StandardCharsets.UTF_8)).getAsJsonObject();
        ArrayList<String> output = PdfUtil.rotateAndRename(request);
        return String.join("<br>", output);
    }

    @PostMapping(value = "/compareMarketName")
    public String compareMarketNameAndTitle(@RequestBody MarketingData input) {
        String url = URLDecoder.decode(input.getUrl(), StandardCharsets.UTF_8);
        return input.getHTMLResponse(PdfUtil.compareMarketingNameGivenUrl(url, input.title));
    }
}
