package com.jerms;

import com.itextpdf.text.DocumentException;
import com.jerms.file.FileRename;
import com.jerms.pdf.PdfExplorer;
import com.jerms.util.Properties;
import com.jerms.pdf.PdfRotate;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class App
{
    static String sourceFolder;
    static String destinationFolder;
    public static void main( String[] args )
    {
        System.out.println("PROGRAM STARTED");
        Properties.initPropertiesPath("C:\\Users\\JermS\\TEST");
        sourceFolder = Properties.getProperty("PDF_LOCATION");
        destinationFolder = Properties.getProperty("PDF_DESTINATION");
        /*final File folder = new File(sourceFolder);
        listFilesForFolder(folder);*/
        List<String> list =  Properties.getPropertyList("PDF_URL_LIST");

        list.forEach( item -> {
            System.out.println("Percentage match: " + (100 - PdfExplorer.compareMarketingName(item) * 100 ));
        });

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
