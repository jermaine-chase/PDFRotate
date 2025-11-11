package com.itgnomes;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class PdfGrabberHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        context.getLogger().log("Input: " + input);

        Map<String, Object> response = new HashMap<>();

        // ✅ Required CORS headers (case and spacing matter)
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "OPTIONS,POST,GET");
        headers.put("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Amz-Date,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Max-Age", "3600"); // optional but helpful for caching

        // --- Identify request method ---
        Map<String, Object> requestContext = (Map<String, Object>) input.get("requestContext");
        Map<String, Object> http = requestContext != null ? (Map<String, Object>) requestContext.get("http") : null;
        String method = http != null ? (String) http.get("method") : "GET";

        // --- Handle OPTIONS (CORS preflight) ---
        if ("OPTIONS".equalsIgnoreCase(method)) {
            response.put("statusCode", 200);
            response.put("headers", headers);
            response.put("body", "{\"message\": \"CORS preflight OK\"}");
            return response;
        }

        try {
            // Check if this is an API Gateway proxy request
            String body;
            if (input.containsKey("body")) {
                // API Gateway proxy format
                body = (String) input.get("body");
                if (body == null || body.isBlank()) {
                    return createErrorResponse(400, "Missing request body", headers);
                }
            } else {
                // Direct invocation - input is the actual payload
                body = objectMapper.writeValueAsString(input);
            }

            // Parse the body to get the URL
            @SuppressWarnings("unchecked")
            Map<String, String> payload = objectMapper.readValue(body, Map.class);
            String url = payload.get("url");

            if (url == null || url.isBlank()) {
                return createErrorResponse(400, "Missing or empty 'url' field", headers);
            }

            // Download and encode PDF
            byte[] pdfBytes = downloadUrlBytes(url);
            String base64 = Base64.getEncoder().encodeToString(pdfBytes);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("base64", base64);

            // Return API Gateway proxy response format
            response.put("statusCode", 200);
            response.put("headers", headers);
            response.put("body", objectMapper.writeValueAsString(responseBody));
            return response;

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            return createErrorResponse(500, e.getMessage() != null ? e.getMessage() : "Unknown error", headers);
        }
    }

    private Map<String, Object> createErrorResponse(int statusCode, String errorMessage, Map<String, String> headers) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", errorMessage);

        try {
            response.put("statusCode", statusCode);
            response.put("headers", headers);
            response.put("body", objectMapper.writeValueAsString(errorBody));
        } catch (Exception e) {
            response.put("statusCode", 500);
            response.put("body", "{\"error\":\"" + errorMessage + "\"}");
        }
        return response;
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
