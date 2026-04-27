package com.run.ui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;

// all panels use this to talk to the backend
public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public static String post(String path, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String postFile(String path, File file) throws IOException, InterruptedException {
        String boundary = "----RunBoundary" + System.currentTimeMillis();
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        String mimeType  = Files.probeContentType(file.toPath());
        if (mimeType == null) mimeType = "application/octet-stream";

        String safeName = file.getName().replace("\"", "");
        String partHeader = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"file\"; filename=\"" + safeName + "\"\r\n"
                + "Content-Type: " + mimeType + "\r\n\r\n";
        String partFooter = "\r\n--" + boundary + "--\r\n";

        byte[] header = partHeader.getBytes(StandardCharsets.UTF_8);
        byte[] footer = partFooter.getBytes(StandardCharsets.UTF_8);
        byte[] body   = new byte[header.length + fileBytes.length + footer.length];
        System.arraycopy(header,    0, body, 0,                              header.length);
        System.arraycopy(fileBytes, 0, body, header.length,                  fileBytes.length);
        System.arraycopy(footer,    0, body, header.length + fileBytes.length, footer.length);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
