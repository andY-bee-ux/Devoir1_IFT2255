package org.projet.repository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.projet.model.Cours;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Cette classe permet de communiquer avec l'API Planifium pour récupérer les informations relatives aux Cours.
 * Elle implémente l'interface IRepository ( qui pourrait donc être implémentée par d'autres reposotiries
 * communiquant avec d'autres sources de données), afin de réduire le couplage avec Planifium.
 * CoursRepository sera un Singleton car on ne veut avoir qu'une instance ( ça ne sert à rien d'en avoir plusieurs).
 */
public class CoursRepository implements IRepository {
    private static CoursRepository instance;
    private CoursRepository() {}
    public static CoursRepository getInstance() {
        if (instance == null) {
            instance = new CoursRepository();
        }
        return instance;
    }

    /**
     * Cette méthode permet de récupérer un Cours de la source de données utilisée.
     * @param param paramètre de la recherche ( id, nom, ou description)
     * @param value valeur de la recherche.
     * @param includeScheduleBool "true" ou "false" dépendamment de si on veut inclure ou non le schedule
     * @param semester le semestre si jamais on veut inclure le schedule
     * @return un type Optional de Cours
     * @throws Exception
     */
    public Optional<List<Cours>> getCourseBy(
            String param,
            String value,
            String includeScheduleBool,
            String semester
    ) throws Exception {

        // URL de base commune aux trois requêtes possibles.
        StringBuilder uri = new StringBuilder("https://planifium-api.onrender.com/api/v1/courses");

        // Cas 1 : recherche par id → /courses/{id}
        if (param.equalsIgnoreCase("id")) {
            uri.append("/").append(value);
        } else {
            // Cas 2 : query → /courses?name=xxx ou ?description=xxx etc.
            uri.append("?")
                    .append(param)
                    .append("=")
                    // afin d'encoder les caractères spéciaux car si non ça ne fonctionne pas.
                    // Malgré l'encodage, cela ne fonctionne pas si l'expression recherchée contient des accents.
                    .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
        }

        // Booleen qui permettra de savoir si on a déjà un query dans la liste de queries afin de commencer par un ?.
        boolean hasQuery = uri.toString().contains("?");

        // Ajouter include_schedule=true si demandé ( si non ce sera null)
        if ("true".equalsIgnoreCase(includeScheduleBool)) {
            uri.append(hasQuery ? "&" : "?");
            uri.append("include_schedule=true");
            hasQuery = true;
        }

        // Ajouter le semester si demandé
        if (semester != null && !semester.isEmpty() && includeScheduleBool == "true") {
            uri.append(hasQuery ? "&" : "?");
            uri.append("schedule_semester=").append(URLEncoder.encode(semester, StandardCharsets.UTF_8));
        }else  if(semester != null && !semester.isEmpty() && includeScheduleBool == "false" ){
            return Optional.empty();
        }

        // Construire la requête
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri.toString()))
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
       // si la requête n'a pas abouti, on retourne un Optional.empty.
        if (response.statusCode() != 200) {
            return Optional.empty();
        }

        // Parsing JSON
        ObjectMapper mapper = new ObjectMapper();
        List<Cours> coursList;
        // on traite ce cas séparemment car la recherche par id ne retourne qu'un cours.
        if (param.equalsIgnoreCase("id")) {
            Cours cours = mapper.readValue(response.body(), Cours.class);
            coursList = List.of(cours);
        }

        /* La recherche par nom ne devrait aussi retourner qu'un cours mais il y a des exemples
            pour lesquels ça en retourne plusieurs ( Programmation 1 -> IFT1016 et IFT1015),
            aussi on peut aussi rechercher par mot-clé pour le nom ( par exemple Programmation).
         */

        else {
            coursList = mapper.readValue(
                    response.body(),
                    mapper.getTypeFactory().constructCollectionType(List.class, Cours.class)
            );
        }

        return Optional.of(coursList);
    }

    /**
     * Cette méthode permet de récupérer tous les ids de Cours de Planifium.
     * @return String contenant tous les cours de Planifium ( de l'Udem par ricochet).
     * @throws Exception
     */

    public Optional<List<String>> getAllCoursesId() throws Exception {
        // Envoi de la requête permet de récupérer tous les programmes avec des informations détaillées.
        HttpRequest getAllPrograms = HttpRequest.newBuilder()
                .uri(new URI("https://planifium-api.onrender.com/api/v1/programs"))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response =
                httpClient.send(getAllPrograms, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        List<String> allCourses = new ArrayList<>();
         // Le json obtenu contient pour chaque programme une liste de cours qui se trouve dans segments->blocs->courses
        // Parcours de chaque programme
        for (JsonNode program : root) {

            JsonNode segments = program.path("segments");
            if (!segments.isArray()) continue;

            // Parcours des segments
            for (JsonNode segment : segments) {

                JsonNode blocs = segment.path("blocs");
                if (!blocs.isArray()) continue;

                // Parcours des blocs
                for (JsonNode bloc : blocs) {

                    JsonNode courses = bloc.path("courses");
                    if (!courses.isArray()) continue;

                    // Ajout des identifiants de cours
                    for (JsonNode courseId : courses) {
                        allCourses.add(courseId.asText());
                    }
                }
            }
        }
        // set afin de supprimer les doublons car des programmes peuvent avoir des cours en commun.
        Set<String> set = new HashSet<>();
        set.addAll(allCourses);
        List<String> listeSansDoublons = new ArrayList<>(set);


        return Optional.of(listeSansDoublons);

    }

    /**
     * Cette méthode permet de récupérer le body response de la requête Planifium permet de vérifier
     * l'éligibilité à un cours.
     * @param courseId id du cours
     * @param completedCourses liste de cours complétés
     * @return response body de Planifium
     * @throws Exception
     */
    public String getCourseEligibility(String courseId, List<String> completedCourses) throws Exception {

        // Construire l'objet JSON
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode requestBody = mapper.createObjectNode();
        // Les noms des propriétés ont été choisis conformément à ceux du response body.
        requestBody.put("course_id", courseId);
        requestBody.putPOJO("completed_courses", completedCourses);

        String jsonBody = mapper.writeValueAsString(requestBody);

        // Ce bloc de code permet de construire la requête POST
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://planifium-api.onrender.com/api/v1/courseplan/check-eligibility"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        // Récupérer le body
        String responseBody = response.body();
        return responseBody;





        }


    }



