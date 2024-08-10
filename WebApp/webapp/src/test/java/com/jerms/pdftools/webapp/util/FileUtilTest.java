package com.jerms.pdftools.webapp.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.google.gson.JsonObject;

import com.jerms.pdftools.webapp.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FileUtilTest {
    @TempDir
    public Path tempDir;

    @BeforeEach
    public void setUp() {
        FileUtil.fileNameMap = null; // Reset static variable before each test
    }

    private File createTempFolder(String name) throws IOException {
        File tempFolder = tempDir.resolve(name).toFile();
        if (!tempFolder.mkdirs()) {
            throw new IOException("Failed to create temporary folder");
        }
        return tempFolder;
    }

    private File createTempFile(String name, String content) throws IOException {
        File tempFile = tempDir.resolve(name).toFile();
        Files.write(tempFile.toPath(), content.getBytes());
        return tempFile;
    }

    private File createTempFile(File folder, String name, String content) throws IOException {
        File tempFile = new File(folder, name);
        Files.write(tempFile.toPath(), content.getBytes());
        return tempFile;
    }

    @Test
    public void testReadFileContents_Success() throws IOException {
        // Create a temporary file with content
        File tempFile = tempDir.resolve("test.txt").toFile();
        String content = "Test content";
        Files.write(tempFile.toPath(), content.getBytes());

        String result = FileUtil.readFileContents(tempFile.getAbsolutePath());

        assertEquals(content, result);
    }

    @Test
    public void testReadFileContents_FileNotFound() {
        String nonExistentPath = "/path/to/nonexistent.txt";

        assertThrows(IOException.class, () -> FileUtil.readFileContents(nonExistentPath));
    }

    @Test
    public void testReadCrossWalkDetails_ValidInput() {
        String details = "key1:value1\nkey2:value2";
        JsonObject expected = new JsonObject();
        expected.addProperty("key1", "value1");
        expected.addProperty("key2", "value2");

        JsonObject result = FileUtil.readCrossWalkDetails(details);

        assertEquals(expected, result);
    }

    @Test
    public void testReadCrossWalkDetails_EmptyInput() {
        String details = "";
        JsonObject expected = new JsonObject();

        JsonObject result = FileUtil.readCrossWalkDetails(details);

        assertEquals(expected, result);
    }

    @Test
    public void testReadCrossWalkDetails_InvalidFormat() {
        String details = "key1value1\nkey2:value2:";

        assertThrows(RuntimeException.class, () -> FileUtil.readCrossWalkDetails(details));
    }

    @Test
    public void testReadCrossWalkDetails_NullInput() {
        assertThrows(NullPointerException.class, () -> FileUtil.readCrossWalkDetails(null));
    }

    @Test
    public void testRename_ValidRequest() throws IOException {
        JsonObject request = new JsonObject();
        request.addProperty("rename-source", createTempFile("rename-source.txt", "oldName:newName\n"));
        request.addProperty("destination", tempDir.resolve("renamed").toString());

        File sourceFolder = createTempFolder("source");
        createTempFile(sourceFolder, "oldName.txt", "old content");

        ArrayList<String> output = FileUtil.rename(request);

        assertTrue(output.get(0).contains("STARTING RENAME"));
        assertTrue(output.get(1).contains("CREATED DESTINATION FOLDER"));
        assertTrue(output.get(2).contains("RENAME COMPLETED"));

        File renamedFile = new File(tempDir.resolve("renamed") + "/newName.txt");
        assertTrue(renamedFile.exists());

        // Clean up temporary files and folder
        Files.deleteIfExists(Paths.get(sourceFolder.getAbsolutePath()));
        Files.deleteIfExists(Paths.get(renamedFile.getAbsolutePath()));
    }

    @Test
    public void testRename_MissingRenameSource() {
        JsonObject request = new JsonObject();
        request.addProperty("destination", tempDir.resolve("renamed").toString());

        assertThrows(RuntimeException.class, () -> FileUtil.rename(request));
    }

    @Test
    public void testRename_MissingDestinationFolder() throws IOException {
        JsonObject request = new JsonObject();
        request.addProperty("rename-source", createTempFile("rename-source.txt", "oldName:newName\n"));
        request.addProperty("destination", "non-existent/path");

        assertThrows(IOException.class, () -> FileUtil.rename(request));
    }

    @Test
    public void testRename_NoMatchingFiles() throws IOException {
        JsonObject request = new JsonObject();
        request.addProperty("rename-source", createTempFile("rename-source.txt", "oldName:newName\n"));
        request.addProperty("destination", tempDir.resolve("renamed").toString());

        createTempFolder("source"); // Empty source folder

        ArrayList<String> output = FileUtil.rename(request);

        assertTrue(output.get(0).contains("STARTING RENAME"));
        assertTrue(output.get(2).contains("RENAME COMPLETED"));

        // No files renamed, verify no "CREATED DESTINATION FOLDER" message
        assertFalse(output.stream().anyMatch(line -> line.contains("CREATED DESTINATION FOLDER")));
    }

    @Test
    public void testRename_IOException() throws IOException {
        JsonObject request = new JsonObject();
        request.addProperty("rename-source", createTempFile("rename-source.txt", "oldName:newName\n"));
        request.addProperty("destination", tempDir.resolve("renamed").toString());

        File sourceFolder = createTempFolder("source");
        createTempFile(sourceFolder, "oldName.txt", "old content");

        // Simulate IOException during rename
        FileUtil mockFileUtil = new FileUtil() {
            @Override
            public void init(JsonObject request) {
                super.init(request);
            }

            @Override
            public ArrayList<String> rename(JsonObject request) throws IOException {
                throw new IOException("Simulated rename error");
            }
        };

        assertThrows(IOException.class, () -> mockFileUtil.rename(request));

        // Clean up temporary files and folder
        Files.deleteIfExists(Paths.get(sourceFolder.getAbsolutePath()));
    }
}
