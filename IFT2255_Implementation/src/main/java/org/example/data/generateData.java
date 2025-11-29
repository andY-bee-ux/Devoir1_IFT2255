package org.example.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Cours;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class generateData {

    public static void main(String[] args) throws Exception {

        System.out.println("Téléchargement de tous les cours...");

        HttpRequest getAllCourses = HttpRequest.newBuilder()
                .uri(new URI("https://planifium-api.onrender.com/api/v1/courses"))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(
                getAllCourses,
                HttpResponse.BodyHandlers.ofString()
        );

        ObjectMapper mapper = new ObjectMapper();
        List<Cours> courses = mapper.readValue(
                response.body(),
                new TypeReference<List<Cours>>() {}
        );

        System.out.println("Nombre de cours reçus : " + courses.size());

        File file = new File("courses.json");
        mapper.writeValue(file, courses);

        System.out.println("Fichier généré : courses.json");
    }
}
