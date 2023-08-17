package com.jerms.pdftools.webapp.util.pdf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jerms.pdftools.webapp.util.file.ReadExcel;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PdfExplorer {
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
        return 0.0;
    }

    public static JsonArray compareMarketingNameGivenFile(String fileUrl) {
        return ReadExcel.readMappingFromExcel(fileUrl);
        /*try {
            // Create a list to store CompletableFuture objects
            List<CompletableFuture<String>> completableFutures = new ArrayList<>();

            // Number of iterations in the loop
            int numIterations = 20;
            int numBatches = (int) Math.ceil(results.size() / numIterations);
            for (int idx = 0; idx < numBatches; idx++) {
                for (int i = 0; i < numIterations; i++) {
                    JsonObject row = results.get(idx * numIterations + i).getAsJsonObject();
                    String url = row.get("P-Stage URL").getAsString();
                    String title = row.get("Title").getAsString();

                    // Create and execute CompletableFuture for the method call
                    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> title + " => Percentage match: "
                            + (100 - compareMarketingNameGivenUrl(url, title) * 100));
                    completableFutures.add(future);
                }

                // Wait for all CompletableFuture objects to complete (blocking operation)
                CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                        completableFutures.toArray(new CompletableFuture[0])
                );
                allFutures.get();

                // Retrieve results from the completed CompletableFuture objects
                for (int i = 0; i < numIterations; i++) {
                    output.add(completableFutures.get(i).get());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error checking marketing name!! "+ e.getMessage());
        }
        return output;*/
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
            System.out.println("PDF downloaded successfully!");
            return fileName;
        } catch (IOException e) {
            fileName = null;
            System.err.println("PDF download failed! " + e.getMessage());
        }
        return null;
    }

    private static void deletePdf(String filePath) {
        File fileToDelete = new File(filePath);

        if (fileToDelete.delete()) {
            System.out.println("File deleted successfully. " + filePath);
        } else {
            System.out.println("Failed to delete the file. " + filePath);
        }
    }

    private static String generatePdfName(String url) {
        String[] urlParts = url.split("/");
        return urlParts[urlParts.length - 1];
    }
}
