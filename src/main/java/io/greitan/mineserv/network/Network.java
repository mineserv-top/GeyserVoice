package io.greitan.mineserv.network;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
}
