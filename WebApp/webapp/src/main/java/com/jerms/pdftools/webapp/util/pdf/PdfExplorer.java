package com.jerms.pdftools.webapp.util.pdf;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class PdfExplorer {
    public static double compareMarketingName(String pdfUrl) throws URISyntaxException {
        String filePath = downLoadPdf(pdfUrl);
        if (filePath != null) {
            String documentText = getDocumentString(filePath);

            StringBuilder pdfMarketingName = getPdfMarketingName(documentText);
            System.out.println(pdfMarketingName);

            String[] urlParts = pdfUrl.split("/");
            String urlMarketingName = urlParts[urlParts.length - 1].replaceAll("_", " ");
            System.out.println(urlMarketingName);

            deletePdf(filePath);
            return new JaroWinklerDistance().apply(urlMarketingName, pdfMarketingName.toString());
        }
        return 0.0;
    }

    private static StringBuilder getPdfMarketingName(String documentText) {
        String[] lines = documentText.split("\n");
        StringBuilder pdfMarketingName = new StringBuilder();
        for (int idx = 1; !lines[idx].startsWith("Coverage") && idx < lines.length; idx++) {
            pdfMarketingName.append(lines[idx].replaceAll("\r", "").replaceAll(" \\| ", "").replaceAll("Integrated", " "));
        }
        pdfMarketingName.delete(0, pdfMarketingName.indexOf(":") + 2);
        return pdfMarketingName;
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

    public static String downLoadPdf(String url) throws URISyntaxException {
        String fileName  = generatePdfName(url);
        URI uri = new URI(url);
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
