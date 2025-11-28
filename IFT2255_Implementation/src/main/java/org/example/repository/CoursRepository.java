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
 * CoursRepository sera un Singleton car on ne veut avoir qu'une instance ( ça ne sert à rien d'en avoir plusieurs).
 */
public class CoursRepository {
    private static CoursRepository instance;
    private CoursRepository() {}
    public static CoursRepository getInstance() {
        if (instance == null) {
            instance = new CoursRepository();
        }
        return instance;
    }

    public Optional<Cours> getCourseById(String id) throws Exception{
        if(!this.getAllCoursesId().get().contains(id)){
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
    public Optional<List<String>> getAllCoursesId() throws Exception {
        HttpRequest getAllCourses = HttpRequest.newBuilder()
                .uri(new URI("https://planifium-api.onrender.com/api/v1/programs"))
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> getResponse = httpClient.send(getAllCourses, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(getResponse.body()); // json = contenu de l’API

        JsonNode courses = null;
        List<String> coursesId = new ArrayList<>();

        for (JsonNode program : root) {
           // if ("117510".equals(program.path("id").asText())) {
                courses = program.path("courses");
                coursesId.add(courses.asText());
               // break;
          //  }
        }
        List<String> liste = new ArrayList<>();
        for(String courseId : coursesId){
            if (courseId != null ) {
                for (JsonNode c : courses) {
                    liste.add(c.asText());
                }
            } else {
                System.out.println("Aucun cours trouvé");
            }
        }

        // ce bloc de code permet de supprimer les doublons car le test sur les doublons ne passait pas
        Set<String> set = new HashSet<>();
        set.addAll(liste);
        List<String> listeSansDoublons = new ArrayList<>(set);


        return Optional.ofNullable(listeSansDoublons);
    }
public Optional<List<String>> getAllCoursesName() throws Exception {
return null;
}

}


