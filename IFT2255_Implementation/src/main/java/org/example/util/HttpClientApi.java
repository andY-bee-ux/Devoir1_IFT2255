package org.example.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

public class HttpClientApi {
    // Ce code provient du repertoire GitHub : ift2255-template-javalin fournit pour ce projet.
    private final HttpClient client;
    private ObjectMapper mapper;

    /**
     *  Il s'agit du constructeur de la classe HttpClientApi.
     **/
    public HttpClientApi() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper();
    }

    /**
     * Cette methode effectue des methodes GET via un URL fourni en parametre.
     * @param uri URI sur lequel on doit effectuer des methodes GET.
     **/
    public HttpClientApiResponse get(URI uri) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", "application/json")
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return new HttpClientApiResponse(
                    response.statusCode(),
                    HttpStatus.reasonPhrase(response.statusCode()),
                    response.body());

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt(); // best practice if interrupted
            return new HttpClientApiResponse(500, "Internal Server Error", e.getMessage());
        }
    }

    /**
     *
     **/
    public <T> T get(URI uri, Class<T> clazz) {
        HttpClientApiResponse raw = get(uri);
        if (raw.getStatusCode() >= 200 && raw.getStatusCode() < 300) {
            try {
                return mapper.readValue(raw.getBody(), clazz);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse JSON: " + e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("Request failed: " + raw.getStatusCode() + " - " + raw.getStatusMessage());
        }
    }

    /**
     *
     **/
    public <T> T get(URI uri, TypeReference<T> typeRef) {
        HttpClientApiResponse raw = get(uri);
        if (raw.getStatusCode() >= 200 && raw.getStatusCode() < 300) {
            try {
                return mapper.readValue(raw.getBody(), typeRef);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse JSON: " + e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("Request failed: " + raw.getStatusCode() + " - " + raw.getStatusMessage());
        }
    }

    /**
     *
     **/
    public HttpClientApiResponse post(URI uri, String jsonBody) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return new HttpClientApiResponse(
                    response.statusCode(),
                    HttpStatus.reasonPhrase(response.statusCode()),
                    response.body());

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return new HttpClientApiResponse(500, "Internal Server Error", e.getMessage());
        }
    }

    /**
     *
     **/
    public static URI buildUri(String baseUrl, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(baseUrl);
        if (params != null && !params.isEmpty()) {
            sb.append("?");
            params.forEach((key, value) -> {
                sb.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8))
                        .append("&");
            });
            sb.deleteCharAt(sb.length() - 1); // remove trailing &
        }
        return URI.create(sb.toString());
    }

}
