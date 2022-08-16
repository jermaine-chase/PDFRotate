package com.jerms;

import com.itextpdf.text.DocumentException;
import com.jerms.pdf.PdfRotate;

import java.io.File;
import java.io.IOException;

public class App
{
    final static String sourceFolder = "/Users/u565341/Desktop/SBCs/Individual SBCs/URL SBCs/";
    // final static String sourceFolder = "/Users/JermS/Desktop/PDF/";
    final static String destinationFolder = sourceFolder + "dest/";
    public static void main( String[] args )
    {

        final File folder = new File(sourceFolder);
        listFilesForFolder(folder);
    }

    public static void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                try {
                    PdfRotate.rotate(fileEntry.getAbsolutePath(), destinationFolder + fileEntry.getName());
                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
