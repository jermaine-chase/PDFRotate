package com.jerms.pdftools.webapp.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jerms.pdftools.webapp.model.MarketingData;
import com.jerms.pdftools.webapp.util.pdf.PdfExplorer;
import com.jerms.pdftools.webapp.util.pdf.PdfRotate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@RestController
public class PdfToolsController {

    @PostMapping("/readExcel")
    public String readExcel(@RequestBody String input) {
        String fileName = input.replaceAll("file=", "");
        return new Gson().toJson(PdfExplorer.compareMarketingNameGivenFile(fileName));
    }

    @PostMapping("/renameAndRotate")
    public String renameAndRotatePdfs(@RequestBody String input) {
        JsonObject request = JsonParser.parseString(input).getAsJsonObject();
        ArrayList<String> output = PdfRotate.rotateAndRename(request);
        return String.join("<br>", output);
    }

    @PostMapping(value = "/compareMarketName")
    public String compareMarketNameAndTitle(@RequestBody MarketingData input) {
        String url = URLDecoder.decode(input.pstageUrl, StandardCharsets.UTF_8);
        String title = input.title != null ?input.title:null;
        //return url + " => Percentage match: " + (100 - PdfExplorer.compareMarketingNameGivenUrl(url, title) * 100);
        return input.getHTMLResponse(PdfExplorer.compareMarketingNameGivenUrl(url, title));
    }
}
