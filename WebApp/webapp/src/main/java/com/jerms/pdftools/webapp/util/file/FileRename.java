package com.jerms.pdftools.webapp.util.file;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FileRename {

    public static JsonObject fileNameMap;

    public static void init(JsonObject request) {
        String details = null;
        String path = request.get("rename-source").getAsString();
        if (request.has("rename-source") && !path.isBlank()) {
            try {
                details = readFileContents(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (request.has("cross-walk") && !request.get("cross-walk").getAsString().isBlank()) {
            details = request.get("cross-walk").getAsString();
        }

        if (details != null) {
            fileNameMap = readCrossWalkDetails(details);
        }
    }

    public static String readFileContents(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    public static JsonObject readCrossWalkDetails(String details) {
        JsonObject crossWalk = new JsonObject();
        String[] detailsArray = details.split("\n");
        for (String entry: detailsArray) {
            String[] entryArray = entry.split(":");
            crossWalk.addProperty(entryArray[0], entryArray[1]);
        }

        return crossWalk;
    }

    public static ArrayList<String> rename(JsonObject request) {
        init(request);
        String path = request.get("destination").getAsString();
        ArrayList<String> output = new ArrayList<>();
        output.add(LocalDateTime.now() + ": STARTING RENAME");
        final File folder = new File(path);
        if (!folder.exists()) {
            try {
                folder.createNewFile();
                output.add(LocalDateTime.now() + ": CREATED DESTINATION FOLDER: " + path);
            } catch (IOException e) {
                output.add(LocalDateTime.now() + ": Error - " + e.getMessage());
            }
        }
        for (File fileEntry : folder.listFiles()) {
            if (fileNameMap.has(fileEntry.getName())) {
                File newFile = new File(path + fileNameMap.get(fileEntry.getName()).getAsString());
                fileEntry.renameTo(newFile);
            }
        }
        output.add(LocalDateTime.now() + ": RENAME COMPLETED");
        return output;
    }
}
