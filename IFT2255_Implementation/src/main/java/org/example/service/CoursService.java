package org.example.service;

import org.example.model.Cours;
import org.example.repository.CoursRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CoursService {

    private final CoursRepository repo = CoursRepository.getInstance();
    private static CoursService instance;

    private boolean lastSearchUsedLocal = true;
    private final ObjectMapper mapper = new ObjectMapper();

    private CoursService() {}

    public static CoursService getInstance() {
        if (instance == null) {
            instance = new CoursService();
        }
        return instance;
    }

    public List<Cours> search(String query) {
        String q = query.toLowerCase();

        List<Cours> local = searchLocal(q);
        if (!local.isEmpty()) {
            lastSearchUsedLocal = true;
            return local;
        }

        lastSearchUsedLocal = false;
        return searchLive(q);
    }

    List<Cours> searchLocal(String q) {
        List<Cours> all = repo.getAllCoursesLocal();

        return all.stream()
                .filter(c ->
                        c.getId().toLowerCase().contains(q) ||
                        c.getName().toLowerCase().contains(q) ||
                        c.getDescription().toLowerCase().contains(q)
                )
                .toList();
    }

    private List<Cours> searchLive(String q) {
        try {
            String url = "https://planifium-api.onrender.com/api/v1/courses/" + q;

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response =
                    httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Erreur Planifium :'" + q + "' introuvable.");
            }

            Cours cours = mapper.readValue(response.body(), Cours.class);
            return List.of(cours);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<Cours> getAllCoursLocal() {
        return repo.getAllCoursesLocal();
    }

    public boolean lastSearchUsedLocal() {
        return lastSearchUsedLocal;
    }
}
