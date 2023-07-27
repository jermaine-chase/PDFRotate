package com.jerms.pdftools.webapp.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jerms.pdftools.webapp.util.pdf.PdfRotate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController(value = "/tools")
public class PdfToolsController {

    @PostMapping("renameAndRotate")
    public String renameAndRotatePdfs(@RequestBody String input) {
        JsonObject request = JsonParser.parseString(input).getAsJsonObject();
        ArrayList<String> output = PdfRotate.rotateAndRename(request);
        return String.join("<br>", output);
    }
}
