package com.jerms.pdftools.webapp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import com.jerms.pdftools.webapp.model.CrossWalkData;
import com.jerms.pdftools.webapp.util.ExcelUtil;
import com.jerms.pdftools.webapp.util.FileUtil;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PdfUtil {
    public static double compareMarketingNameGivenUrl(String pdfUrl, String marketingName) {
        System.out.println("Started at " + LocalDateTime.now());
        String filePath = downLoadPdf(pdfUrl);
        if (filePath != null) {
            String documentText = getDocumentString(filePath);
            String pdfMarketingName = getPdfMarketingName(documentText).toString();
            System.out.println(pdfMarketingName);

            String urlMarketingName = marketingName;
            if (urlMarketingName == null) {
                String[] urlParts = pdfUrl.split("/");
                urlMarketingName = urlParts[urlParts.length - 1].replaceAll("_", " ");
            }
            System.out.println(urlMarketingName);

            deletePdf(filePath);
            System.out.println("Ended at " + LocalDateTime.now());
            return new JaroWinklerDistance().apply(urlMarketingName, pdfMarketingName);
        }
        // PDF not found case
        return -1.0;
    }

    public static JsonArray compareMarketingNameGivenFile(CrossWalkData input) {
        return ExcelUtil.readMappingFromExcel(input);
    }

    private static StringBuilder getPdfMarketingName(String documentText) {
        try {
            String[] lines = documentText.split("\n");
            StringBuilder pdfMarketingName = new StringBuilder();
            for (int idx = 1; !lines[idx].startsWith("Coverage"); idx++) {
                pdfMarketingName.append(lines[idx].replaceAll("\r", ""));
            }
            pdfMarketingName.delete(0, pdfMarketingName.indexOf(":") + 2);
            return pdfMarketingName;
        } catch (Exception e) {
            System.err.println("Error getting pdf Marketing Name!! "+ e.getMessage());
            return new StringBuilder();
        }
    }

    public static String getDocumentString(String pdfUrl) {
        try (PDDocument document = PDDocument.load(new File(pdfUrl))) {
            if (!document.isEncrypted()) {
                PDFTextStripper textStripper = new PDFTextStripper();
                return textStripper.getText(document);
            } else {
                System.err.println("Encrypted PDFs are not supported.");
            }
        } catch (IOException e) {
            System.err.println("Opening PDF failed!! " + e.getMessage());
        }
        return null;
    }

    public static String downLoadPdf(String url) {
        String fileName  = generatePdfName(url);
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            fileName = null;
            System.err.println("URI Initialization failed! " + e.getMessage());
            return fileName;
        }
        try (InputStream in = uri.toURL().openStream();
             FileOutputStream fos = new FileOutputStream(fileName)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            // System.out.println("PDF downloaded successfully!");
            return fileName;
        } catch (IOException e) {
            fileName = null;
            System.err.println("PDF download failed! " + e.getMessage());
        }
        return null;
    }

    private static void deletePdf(String filePath) {
        File fileToDelete = new File(filePath);

        fileToDelete.delete();//System.out.println("File deleted successfully. " + filePath);

    }

    private static String generatePdfName(String url) {
        String[] urlParts = url.split("/");
        return urlParts[urlParts.length - 1];
    }

    public static ArrayList<String> rotateAndRename(JsonObject request) {
        ArrayList<String> output = new ArrayList<>();
        File folder = new File(request.get("source").getAsString());
        output.add(LocalDateTime.now() + ": STARTING ROTATE");
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                if (fileEntry.getName().endsWith("pdf")) {
                    try {
                        if (request.get("rotate").getAsBoolean()) {
                            rotate(fileEntry.getAbsolutePath(),
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
            output.addAll(FileUtil.rename(request));
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
