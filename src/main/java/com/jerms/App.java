package com.jerms;

import com.itextpdf.text.DocumentException;
import com.jerms.file.FileRename;
import com.jerms.util.Properties;
import com.jerms.pdf.PdfRotate;

import java.io.File;
import java.io.IOException;

public class App
{
    final static String sourceFolder = Properties.getProperty("PDF_LOCATION");
    final static String destinationFolder = Properties.getProperty("PDF_DESTINATION");
    public static void main( String[] args )
    {
        System.out.println("PROGRAM STARTED");
        final File folder = new File(sourceFolder);
        listFilesForFolder(folder);
        System.out.println("PROGRAM ENDED");
    }

    public static void listFilesForFolder(final File folder) {
        boolean doRotate = "Y".equalsIgnoreCase(Properties.getProperty("ROTATE"));
        System.out.println("Rotate? " + doRotate);
        boolean doRename = "Y".equalsIgnoreCase(Properties.getProperty("RENAME"));
        System.out.println("Rename? " + doRename);

        System.out.println("STARTING ROTATE");
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                if (fileEntry.getName().endsWith("pdf")) {
                    try {
                        if (doRotate) {
                            PdfRotate.rotate(fileEntry.getAbsolutePath(), destinationFolder + fileEntry.getName());
                        }
                    } catch (IOException | DocumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("ROTATE COMPLETE");

        if (doRename) {
            FileRename.rename(destinationFolder);
        }
    }
}
