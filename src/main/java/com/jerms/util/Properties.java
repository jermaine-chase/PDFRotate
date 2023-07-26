package com.jerms.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class Properties {
    private static java.util.Properties PROPERTIES;
    private static final String FILE = "application.properties";
    private static LocalDateTime TIME;
    public static void init() {
        try {
            String p = new File(Properties.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath() + "/";
            Path path = Paths.get(p + FILE);
            PROPERTIES = new java.util.Properties();
                PROPERTIES.load(Files.newInputStream(path));
                TIME = LocalDateTime.now();
        } catch (URISyntaxException | IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static boolean needsInit() throws IOException, URISyntaxException {
        String p = new File(Properties.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath() + "/";
        Path path = Paths.get(p + FILE);
        long lastModified = Files.getLastModifiedTime(path).toMillis();
         return TIME == null ||
                LocalDateTime.now().minus(5, ChronoUnit.MINUTES).isAfter(TIME) ||
                PROPERTIES == null || PROPERTIES.isEmpty() ||
                lastModified > TIME.atZone(ZoneId.of(ZoneId.systemDefault().getId())).toInstant().toEpochMilli();
    }

    public static String getProperty(String propertyName) {
        try {
            if (needsInit()) {
                init();
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return PROPERTIES.getProperty(propertyName);
    }
}
