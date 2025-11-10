package com.itgnomes;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

public class PdfGrabberHandler implements RequestHandler<Map<String, String>, Map<String, String>> {

    @Override
    public Map<String, String> handleRequest(Map<String, String> input, Context context) {
        String url = input != null ? input.get("url") : null;
        Map<String, String> response = new HashMap<>();

        if (url == null || url.isBlank()) {
            response.put("error", "Missing or empty 'url' field");
            return response;
        }

        try {
            byte[] pdfBytes = downloadUrlBytes(url);
            String base64 = Base64.getEncoder().encodeToString(pdfBytes);
            response.put("base64", base64);
            return response;
        } catch (Exception e) {
            response.put("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
            return response;
        }
    }

    private byte[] downloadUrlBytes(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(15_000);
        conn.setReadTimeout(30_000);
        conn.setInstanceFollowRedirects(true);

        int code = conn.getResponseCode();
        if (code >= 400) {
            InputStream err = conn.getErrorStream();
            String errMsg = err != null ? readStreamAsString(err) : "HTTP error code: " + code;
            conn.disconnect();
            throw new IOException("Failed to download PDF: " + errMsg);
        }

        try (InputStream in = conn.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return out.toByteArray();
        } finally {
            conn.disconnect();
        }
    }

    private String readStreamAsString(InputStream in) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
    }
}
