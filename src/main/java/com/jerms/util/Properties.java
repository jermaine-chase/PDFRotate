package com.jerms.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public class Properties {
    private static java.util.Properties PROPERTIES;
    private static String PROPERTIES_PATH;
    private static final String FILE = "application.properties";
    private static LocalDateTime TIME;
    public static void initPropertiesPath(String appPath) {
        PROPERTIES_PATH = appPath;
    }

    public static void init() {
        try {
            String p;
            if (PROPERTIES_PATH != null) {
                p = PROPERTIES_PATH;
            } else {
                p = new File(Properties.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            }
            Path path = Paths.get(p + System.getProperty("file.separator") + FILE);
            PROPERTIES = new java.util.Properties();
            PROPERTIES.load(Files.newInputStream(path));
            TIME = LocalDateTime.now();
        } catch (URISyntaxException | IOException ioe) {
            System.err.println("Error initializing properties!!! " + ioe.getMessage());
        }
    }

    private static boolean needsInit() throws IOException, URISyntaxException {
        if (PROPERTIES_PATH == null) {
            return true;
        }
        String p = PROPERTIES_PATH + System.getProperty("file.separator");
        Path path = Paths.get(p + FILE);
        long lastModified = Files.getLastModifiedTime(path).toMillis();
         return TIME == null ||
                LocalDateTime.now().minusMinutes(5).isAfter(TIME) ||
                PROPERTIES == null || PROPERTIES.isEmpty() ||
                lastModified > TIME.atZone(ZoneId.of(ZoneId.systemDefault().getId())).toInstant().toEpochMilli();
    }

    public static String getProperty(String propertyName) {
        try {
            if (needsInit()) {
                init();
            }
        } catch (URISyntaxException | IOException e) {
            System.err.println("Error initializing properties!!! " + e.getMessage());
        }
        return PROPERTIES.getProperty(propertyName);
    }

    public static List<String> getPropertyList(String propertyName) {

        String propertyValue = getProperty(propertyName);
        return Arrays.asList(propertyValue.split(","));
    }
}
