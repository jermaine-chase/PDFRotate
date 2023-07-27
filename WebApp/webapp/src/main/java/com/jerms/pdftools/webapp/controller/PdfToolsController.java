package com.jerms.pdftools.webapp.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jerms.pdftools.webapp.util.pdf.PdfExplorer;
import com.jerms.pdftools.webapp.util.pdf.PdfRotate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@RestController
public class PdfToolsController {

    @PostMapping("/renameAndRotate")
    public String renameAndRotatePdfs(@RequestBody String input) {
        JsonObject request = JsonParser.parseString(input).getAsJsonObject();
        ArrayList<String> output = PdfRotate.rotateAndRename(request);
        return String.join("<br>", output);
    }

    @PostMapping("/compareMarketName")
    public String compareMarketName(@RequestBody String input) throws URISyntaxException {
        //JsonObject request = JsonParser.parseString(input).getAsJsonObject();
        ArrayList<String> output = new ArrayList<>();
        String urls = input;
        String[] urlsArray = urls.split("%0A");
        for (String url: urlsArray) {
            output.add("Percentage match: "
                    + (100 - PdfExplorer.compareMarketingName(URLDecoder.decode(url, StandardCharsets.UTF_8)) * 100 ));
        }

        return String.join("<br>", output);
    }
}
