package org.example.repository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Cours;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * Cette classe permet de communiquer avec l'API utilisée contenant les informations relatives aux cours.
 */
public class CoursRepository {
    public Optional<Cours> getCourseById(String id) throws Exception{
        if(!this.getAllCourses().get().contains(id)){
            return Optional.empty();
        }
        HttpRequest getCourseByIdRequest = HttpRequest.newBuilder()
                .uri(new URI("https://planifium-api.onrender.com/api/v1/courses/" + id ))
                .build();

HttpClient httpClient = HttpClient.newHttpClient();
HttpResponse<String> getResponse = httpClient.send(getCourseByIdRequest, HttpResponse.BodyHandlers.ofString());
// getResponse.body
        ObjectMapper objectMapper = new ObjectMapper();
        Cours cours = objectMapper.readValue(getResponse.body(), Cours.class);
        return Optional.ofNullable(cours);
    }
    public Optional<List<String>> getAllCourses() throws Exception {
        HttpRequest getAllCourses = HttpRequest.newBuilder()
                .uri(new URI("https://planifium-api.onrender.com/api/v1/programs"))
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> getResponse = httpClient.send(getAllCourses, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(getResponse.body()); // json = contenu de l’API

        JsonNode courses = null;

        for (JsonNode program : root) {
            if ("117510".equals(program.path("id").asText())) {
                courses = program.path("courses");
                break;
            }
        }
        List<String> liste = new ArrayList<>();
        if (courses != null && courses.isArray()) {
            for (JsonNode c : courses) {
                liste.add(c.asText());
            }
        } else {
            System.out.println("Aucun programme avec id 117510 !");
        }
        // ce bloc de code permet de supprimer les doublons car le test sur les doublons ne passait pas
        Set<String> set = new HashSet<>();
        for (JsonNode c : courses) {
            set.add(c.asText());
        }
        List<String> listeSansDoublons = new ArrayList<>(set);


        return Optional.ofNullable(listeSansDoublons);
    }


}


