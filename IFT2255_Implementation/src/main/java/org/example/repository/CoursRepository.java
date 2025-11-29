package org.example.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Cours;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class CoursRepository {

    private static CoursRepository instance;

    private final ObjectMapper mapper = new ObjectMapper();

    private List<Cours> localCourses = null;

    private CoursRepository() {}

    public static CoursRepository getInstance() {
        if (instance == null) {
            instance = new CoursRepository();
        }
        return instance;
    }

    
public void loadLocalJson() {
    try {
        var url = getClass().getClassLoader().getResource("courses.json");

        if (url == null) {
            throw new RuntimeException("courses.json introuvable dans src/main/data/");
        }

        File file = new File(url.getFile());
        localCourses = mapper.readValue(file, new TypeReference<List<Cours>>() {});
        
    } catch (Exception e) {
        throw new RuntimeException("Erreur lors du chargement du JSON local : " + e.getMessage(), e);
    }
}

   
    public List<Cours> getAllCoursesLocal() {
        if (localCourses == null) {
            loadLocalJson();
        }
        return localCourses;
    }

   
    public Optional<Cours> getCourseByIdLocal(String id) {
        if (localCourses == null) {
            loadLocalJson();
        }
        return localCourses.stream()
                .filter(c -> c.getId().equalsIgnoreCase(id))
                .findFirst();
    }


   
    public Optional<Cours> getCourseById(String id) throws Exception {

        if (!this.getAllCoursesId().orElse(List.of()).contains(id)) {
            return Optional.empty();
        }

        HttpRequest getCourseByIdRequest = HttpRequest.newBuilder()
                .uri(new URI("https://planifium-api.onrender.com/api/v1/courses/" + id))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(
                getCourseByIdRequest,
                HttpResponse.BodyHandlers.ofString()
        );

        Cours cours = mapper.readValue(response.body(), Cours.class);

        return Optional.ofNullable(cours);
    }

    public Optional<List<String>> getAllCoursesId() throws Exception {

        HttpRequest getAllCourses = HttpRequest.newBuilder()
                .uri(new URI("https://planifium-api.onrender.com/api/v1/programs"))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(
                getAllCourses,
                HttpResponse.BodyHandlers.ofString()
        );

        JsonNode root = mapper.readTree(response.body());
        List<String> liste = new ArrayList<>();

        for (JsonNode program : root) {
            JsonNode courses = program.path("courses");

            if (courses.isArray()) {
                for (JsonNode c : courses) {
                    liste.add(c.asText());
                }
            }
        }

        return Optional.of(new ArrayList<>(new HashSet<>(liste)));
    }

    public Optional<List<String>> getAllCoursesName() throws Exception {
        return Optional.empty();
    }
}
