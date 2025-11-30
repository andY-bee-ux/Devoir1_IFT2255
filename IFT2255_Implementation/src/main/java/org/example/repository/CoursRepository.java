package org.example.repository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Cours;
import org.example.util.HttpClientApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * Cette classe permet de communiquer avec l'API utilisée contenant les informations relatives aux cours.
 * CoursRepository sera un Singleton car on ne veut avoir qu'une instance ( ça ne sert à rien d'en avoir plusieurs).
 */
public class CoursRepository {


    private static final String BASE_URL = "https://planifium-api.onrender.com/api/v1/";

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
                .uri(new URI(BASE_URL+"courses/"+ id ))
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
                .uri(new URI(BASE_URL+"programs"))
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
    
    public Optional<List<String>> getAllCoursesName() throws Exception {return null;}

    public boolean doesCourseExist(String id){
       String baseUrl = BASE_URL + "courses/{"+id+"}";
       Boolean exists = false;
       try{
           HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl).openConnection();
           connection.setRequestMethod("GET");
           connection.setConnectTimeout(5000);
           connection.setReadTimeout(5000);
           int responseCode = connection.getResponseCode();
           if(responseCode == 200){
               exists = true;
           }
         }catch (IOException e) {
           System.out.println("Erreur lors de la récupération des requêtes : " + e.getMessage());  
        }
         return exists;
    }

    public Map<String,String> detailsCours(String id){
        Map<String,String> details = new HashMap<>();
        StringBuilder sb1 = new StringBuilder(), sb2 = new StringBuilder(), sb3= new StringBuilder();
        String baseUrl = BASE_URL + "courses/{"+id+"}";

        try{
            HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            ObjectMapper mapper = new ObjectMapper();

            JsonNode jsonNode = mapper.readTree(response.toString());

            details.put("id", jsonNode.get("id").asText());
            details.put("name", jsonNode.get("name").asText());
            details.put("credits", jsonNode.get("credits").asText());
            details.put("description", jsonNode.get("description").asText());
            details.put("requirement_text", jsonNode.get("requirement_text").asText());
            for(JsonNode node1 : jsonNode.get("equivalent_courses")){
                sb1.append(node1.asText()).append(",");
            }
            details.put("equivalent_courses", sb1.toString());
            for(JsonNode node2 : jsonNode.get("prerequisite_courses")){
                sb2.append(node2.asText()).append(",");
            }
            details.put("prerequisite_courses", sb2.toString());
            for(JsonNode node3 : jsonNode.get("concomitant_courses")){
                sb3.append(node3.asText()).append(",");
            }
            details.put("concomitant_courses", sb3.toString());
        }catch (IOException e) {
            System.out.println("Erreur lors de la récupération des requêtes : " + e.getMessage());
        }
        return details;
    }

    public List<String> getCoursHoraires(String cours, String session){
        List<String> horaires = new ArrayList<>();

        String baseUrl = BASE_URL + "schedules";
        Map<String, String> params = Map.of(
                "courses_list", "[\"" + cours + "\"]",
                "min_semester", session
        );

        URI uri = HttpClientApi.buildUri(baseUrl, params);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.toString());

            JsonNode jsonNode1 = jsonNode.get(0);
            String id = jsonNode1.get("_id").asText();

            List<String> sections = new ArrayList<>();
            for (JsonNode section : jsonNode1.get("sections")) {
                String bloc = section.get("name").asText();
                if (bloc.length() == 1) {
                    sections.add(bloc);
                }
            }

            return imprimerHoraire(id, sections);

        } catch (IOException e) {
            System.out.println("Erreur lors de la récupération des requêtes : " + e.getMessage());
        }

        return horaires;
    }

    private List<String> imprimerHoraire(String id, List<String> sections){
        List<String> resultat = new ArrayList<>();
        String baseUrl = BASE_URL + "schedules/ics";

        for (String section : sections) {
            String scheduleId = id + section;
            URI uri = HttpClientApi.buildUri(
                    baseUrl,
                    Map.of("schedule_ids", scheduleId)
            );

            resultat.add("Section " + section + " : " + uri);
        }

        return resultat;
    }
}


