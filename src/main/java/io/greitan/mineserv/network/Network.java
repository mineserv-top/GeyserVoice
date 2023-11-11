package io.greitan.mineserv.network;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.greitan.mineserv.utils.Logger;

public class Network {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static boolean sendPostRequest(String url, Object data) {
        try {
            String jsonData = objectMapper.writeValueAsString(data);

            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Logger.debug("Rquest: " + jsonData.toString());
            Logger.debug("Status Code: " + response.statusCode());
            
            int statusCode = response.statusCode();
            if (statusCode == 200 || statusCode == 202 ) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Logger.error("Cant connect to voice chat server!");
            return false;
        }
    }

    public static void sendGetRequest(String url) {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            Logger.info("GET status: " + statusCode);

            if (statusCode == 200) {
                Logger.info("GET request successful!");
            } else {
                Logger.warn("GET request failed!");
            }

            HttpHeaders headers = response.headers();
            Map<String, List<String>> headerFields = headers.map();
            Logger.info("Response Headers: " + headerFields);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
