package com.jerms.pdftools.webapp.util.pdf;

import com.google.gson.JsonObject;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import com.jerms.pdftools.webapp.util.file.RenameFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PdfRotate {

    public static ArrayList<String> rotateAndRename(JsonObject request) {
        ArrayList<String> output = new ArrayList<>();
        File folder = new File(request.get("source").getAsString());
        output.add(LocalDateTime.now() + ": STARTING ROTATE");
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                if (fileEntry.getName().endsWith("pdf")) {
                    try {
                        if (request.get("rotate").getAsBoolean()) {
                            PdfRotate.rotate(fileEntry.getAbsolutePath(),
                                    request.get("destination").getAsString() + fileEntry.getName());
                        }
                    } catch (IOException | DocumentException e) {
                        output.add(LocalDateTime.now().toString() + ": Error rotating " + fileEntry.getName() + "!! " + e.getMessage());
                    }
                }
            }
        }
        output.add(LocalDateTime.now() + ": ROTATE COMPLETE");

        if (request.get("rename").getAsBoolean()) {
            output.addAll(RenameFile.rename(request));
        }

        return output;
    }
    private static void rotate(String source, String dest) throws IOException, DocumentException {

        final PdfReader reader = new PdfReader(source);
        final int pagesCount = reader.getNumberOfPages();

        for (int n = 1; n <= pagesCount; n++) {
            final PdfDictionary page = reader.getPageN(n);
            final PdfNumber rotate = page.getAsNumber(PdfName.ROTATE);
            final int rotation =
                    rotate == null ? 90 : (rotate.intValue() + 90) % 360;

            page.put(PdfName.ROTATE, new PdfNumber(rotation));
        }

        PdfStamper stamper = new PdfStamper(reader, Files.newOutputStream(Paths.get(dest)));
        stamper.close();
        reader.close();
    }
}
