package com.jerms.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PdfRotate {
    public static void rotate(String source, String dest) throws IOException, DocumentException {

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
