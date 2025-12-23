package org.projet.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.projet.model.Cours;
import org.projet.repository.CoursRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Cette classe permet de gérer la logique métier associée à la manipulation des cours.
 * La classe CoursService est un Singleton, car il n'est pas nécessaire d'en avoir plusieurs
 * instances.
 */
public class CoursService {
    // Le Course repository est un singleton, donc on récupère juste l'instance associée.
    private CoursRepository coursRepository = CoursRepository.getInstance();

    /**
     * Ce bloc de code permet de définir la classe CoursService comme un Singleton pour
     * garantir que cette dernière n'ait qu'unen instance.
     **/
    private static CoursService instance;

    private CoursService() {
    }

    public static CoursService getInstance() {
        if (instance == null) {
            instance = new CoursService();
        }
        return instance;
    }

    /**
     * Cette méthode permet de récupérer le CoursRepository associé à l'instance CoursService
     *
     * @return l'instance CoursRepository
     */
    public CoursRepository getCoursRepository() {
        return coursRepository;
    }

    /**
     * Cette méthode permet de set le cours Repository. Elle a été ajoutée pour pouvoir faire passer
     * les tests avec Mockito, mais si non elle n'est pas vraiment nécessaire car CoursRepository
     * est un singleton.
     * @param coursRepository le coursRepository
     */
    public void setCoursRepository(CoursRepository coursRepository) {
        this.coursRepository = coursRepository;
    }
    // pour stocker les ids de cours issu de l'appel à getAllCoursesId() afin de réduire le nombre d'appels HTTP quand on appelle validateIdCours.
    public List<String> cacheCoursIds = new ArrayList<>();

    /**
     * Cette méthode gère la logique derrière la validation de l'id d'un cours ( permet de vérifier
     * si l'id donné correspond à un cours existant.)
     *
     * @param id id du Cours à vérifier
     * @return un booléen indiquant si l'id est valide ou non
     */

    private boolean validateIdCours(String id) {
        try {
            //  Si le cache est vide, on va chercher les données une seule fois
            if (cacheCoursIds.isEmpty()) {
                Optional<List<String>> listeCours = this.coursRepository.getAllCoursesId();
                cacheCoursIds = listeCours.orElse(List.of()); // évite les null
            }

            // On utilise le cache pour vérifier l'ID
            return cacheCoursIds.contains(id);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }


    }

    /**
     * Cette méthode permet de comparer une liste de cours selon des critères donnés.
     * @param cours cours à comparer
     * @param criteresComparaison critères de comparaison
     * @param session utile lorsque l'utilisateur veut comparer les horaires des cours ( il devra idéalement préciser la session sur laquelle faire la comparaison)
     * @return un tableau contenant les "valeurs" critères associés aux cours.
     */
    public List<List<String>> comparerCours(String[] cours, String[] criteresComparaison,String session) {

        List<List<String>> resultatDeComparaison = new ArrayList<>();

        // Charger les cours
        List<Cours> coursTrouves = new ArrayList<>();


        for (String idCours : cours) {
            if (!validateIdCours(idCours)) {
                System.out.println("Cours non valide : " + idCours);
                return null;
            }

            try {
                Optional<List<Cours>> opt = this.coursRepository.getCourseBy("id", idCours,"true",null);
                if (opt.isEmpty()) {
                    System.out.println("Cours introuvable : " + idCours);
                    return null;
                }

                coursTrouves.add(opt.get().get(0));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }

        // Ce bloc de code fait un mapping entre les critères demandés et les propriétés des cours, et
        // construit la ligne correspondante au cours en question dans le tableau.
        for (Cours coursObj : coursTrouves) {

            List<String> ligne = new ArrayList<>();
            ligne.add(coursObj.getId());

            for (String critere : criteresComparaison) {

                switch (critere) {

                    case "id":
                        ligne.add(coursObj.getId());
                        break;

                    case "name":
                        ligne.add(coursObj.getName());
                        break;

                    case "description":
                        ligne.add(coursObj.getDescription());
                        break;

                    case "scheduledSemester":
                        ligne.add(coursObj.getScheduledSemester());
                        break;

                    case "schedules":
                        if (session == null || session.isBlank()) {
                            // Si l'utilisateur ne spécifie pas de session, on affiche tous les schedules
                            ligne.add(
                                    coursObj.getSchedules().stream()
                                            .map(Cours.Schedule::toString)
                                            .collect(Collectors.joining("\n---\n"))
                            );
                        } else {
                            // Si non, on affiche uniquement les horaires pour le semester choisi
                            ligne.add(
                                    coursObj.getSchedules().stream()
                                            .map(schedule -> schedule.toStringPourSemester(session))
                                            .collect(Collectors.joining("\n---\n"))
                            );
                        }
                        break;



                    case "prerequisite_courses":
                        ligne.add(Arrays.toString(coursObj.getPrerequisite_courses()));
                        break;

                    case "equivalent_courses":
                        ligne.add(Arrays.toString(coursObj.getEquivalent_courses()));
                        break;

                    case "concomitant_courses":
                        ligne.add(Arrays.toString(coursObj.getConcomitant_courses()));
                        break;

                    case "udemWebsite":
                        ligne.add(coursObj.getUdemWebsite());
                        break;

                    case "credits":
                        ligne.add(String.valueOf(coursObj.getCredits()));
                        break;

                    case "requirement_text":
                        ligne.add(coursObj.getRequirement_text());
                        break;

                    case "available_terms":
                        ligne.add(coursObj.getAvailable_terms() != null
                                ? coursObj.getAvailable_terms().toString()
                                : "null");
                        break;
                    // les valeurs possibles sont : P ( presentiel), SD( à distance synchrone) et AD ( à distance asynchrone) et MD apparemment ( avec ANG1933).
                    case "mode":
                        if (coursObj.getSchedules() == null || coursObj.getSchedules().isEmpty()) {
                            ligne.add("Aucun horaire");
                        } else {
                            // Filtrer par session si précisée
                            List<String> modes = new ArrayList<>();
                            for (Cours.Schedule s : coursObj.getSchedules()) {
                                if (session != null && !session.isBlank() &&
                                        !s.getSemester().equalsIgnoreCase(session)) {
                                    continue; // ignore les autres sessions
                                }

                                if (s.getSections() == null) continue;

                                for (Cours.Section section : s.getSections()) {
                                    if (section.getVolets() == null) continue;
                                    for (Cours.Volet volet : section.getVolets()) {
                                        if (volet.getActivities() == null) continue;
                                        for (Cours.Activity act : volet.getActivities()) {
                                            if (act.getMode() != null && !modes.contains(act.getMode())) {
                                                modes.add(act.getMode());
                                            }
                                        }
                                    }
                                }
                            }

                            if (modes.isEmpty()) {
                                ligne.add("Aucun mode pour cette session");
                            } else {
                                ligne.add(String.join(", ", modes));
                            }
                        }
                        break;


                    case "available_periods":
                        ligne.add(coursObj.getAvailable_periods() != null
                                ? coursObj.getAvailable_periods().toString()
                                : "null");
                        break;

                    default:
                        ligne.add("Critère inconnu : " + critere);
                }
            }

            resultatDeComparaison.add(ligne);
        }

        return resultatDeComparaison;


    }

    /**
     * Cette méthode permet de comparer des ensembles de cours.
     * @param listeDeListesDeCours liste des ensembles de cours qu'on veut comparer.
     * @return un tableau contenant les comparaisons des ensembles de cours donnés.
     */

    public List<List<String>> comparerCombinaisonCours(
            List<List<String>> listeDeListesDeCours,
            String sessionChoisie
    ) {

        List<List<String>> resultat = new ArrayList<>();
        int index = 1;

        for (List<String> combinaison : listeDeListesDeCours) {

            // Transformer les ids en objets Cours
            List<Cours> coursCombinaison = new ArrayList<>();

            for (String idCours : combinaison) {
                if (!validateIdCours(idCours)) {
                    System.out.println("Cours non valide : " + idCours);
                    return null;
                }
                try {
                    Optional<List<Cours>> opt =
                            this.coursRepository.getCourseBy("id", idCours, "true", null);

                    if (opt.isEmpty())
                        throw new RuntimeException("Cours introuvable : " + idCours);

                    coursCombinaison.add(opt.get().get(0));

                } catch (Exception e) {
                    throw new RuntimeException("Erreur lors du chargement de " + idCours, e);
                }
            }

            // métriques de comparaison
            int creditsTotaux = 0;
            Set<String> prerequis = new HashSet<>();
            Set<String> concomitants = new HashSet<>();
            Map<String, Boolean> periodesCommunes = null;
            Map<String, Boolean> sessionsCommunes = null;

            // horaires pour la session choisie
            List<ActivityInfo> activities = new ArrayList<>();

            for (Cours c : coursCombinaison) {

                creditsTotaux += c.getCredits();

                if (c.getPrerequisite_courses() != null)
                    prerequis.addAll(Arrays.asList(c.getPrerequisite_courses()));

                if (c.getConcomitant_courses() != null)
                    concomitants.addAll(Arrays.asList(c.getConcomitant_courses()));

                // PÉRIODES COMMUNES
                Map<String, Boolean> periods = c.getAvailable_periods();
                if (periods != null) {
                    if (periodesCommunes == null) {
                        periodesCommunes = new HashMap<>();
                        for (var e : periods.entrySet()) {
                            if (e.getValue()) periodesCommunes.put(e.getKey(), true);
                        }
                    } else {
                        periodesCommunes.entrySet().removeIf(
                                e -> !periods.getOrDefault(e.getKey(), false)
                        );
                    }
                }

                // SESSIONS COMMUNES
                Map<String, Boolean> terms = c.getAvailable_terms();
                if (terms != null) {
                    if (sessionsCommunes == null) {
                        sessionsCommunes = new HashMap<>();
                        for (var e : terms.entrySet()) {
                            if (e.getValue()) sessionsCommunes.put(e.getKey(), true);
                        }
                    } else {
                        sessionsCommunes.entrySet().removeIf(
                                e -> !terms.getOrDefault(e.getKey(), false)
                        );
                    }
                }

                // EXTRACTION DE L'HORAIRE POUR LA SESSION CHOISIE
                if (c.getSchedules() != null) {
                    for (Cours.Schedule s : c.getSchedules()) {

                        if (!s.getSemester().equalsIgnoreCase(sessionChoisie))
                            continue; // Ignore les autres sessions

                        if (s.getSections() == null) continue;

                        for (Cours.Section section : s.getSections()) {
                            if (section.getVolets() == null) continue;
                            for (Cours.Volet volet : section.getVolets()) {
                                if (volet.getActivities() == null) continue;
                                for (Cours.Activity act : volet.getActivities()) {
                                    activities.add(new ActivityInfo(
                                            c.getId(),
                                            section.getName(),
                                            act.getDays(),
                                            act.getStart_time(),
                                            act.getEnd_time()
                                    ));
                                }
                            }
                        }
                    }
                }
            }

            // ---- DÉTECTION DES CONFLITS
            List<String> conflits = detectConflits(activities);

            // ---- Construction de la ligne
            List<String> ligne = new ArrayList<>();
            ligne.add("Combinaison " + index);
            ligne.add("Cours=" + combinaison);
            ligne.add("Crédits=" + creditsTotaux);
            ligne.add("Prérequis=" + prerequis);
            ligne.add("Concomitants=" + concomitants);
            ligne.add("Périodes communes=" +
                    (periodesCommunes == null ? "[]" : periodesCommunes.keySet()));
            ligne.add("Sessions communes=" +
                    (sessionsCommunes == null ? "[]" : sessionsCommunes.keySet()));
            ligne.add("Horaires=" + activities);
            ligne.add("Conflits=" + conflits);

            resultat.add(ligne);
            index++;
        }

        return resultat;
    }
    static class ActivityInfo {
        String coursId;
        String section;
        List<String> days;
        String start;
        String end;
        LocalTime startTime;
        LocalTime endTime;

        ActivityInfo(String coursId, String section, List<String> days, String start, String end) {
            this.coursId = coursId;
            this.section = section;
            this.days = days;
            this.start = start;
            this.end = end;
            this.startTime = LocalTime.parse(start);
            this.endTime = LocalTime.parse(end);
        }

        @Override
        public String toString() {
            return coursId + " [" + section + "] " + days + " " + start + "-" + end;
        }
    }

    /**
     * Cette méthode permet de détecter des conflits horaires pour une liste d'activités données.
     * @param acts liste d'activités
     * @return la liste des conflits.
     */
    private List<String> detectConflits(List<ActivityInfo> acts) {
        List<String> conflits = new ArrayList<>();

        for (int i = 0; i < acts.size(); i++) {
            ActivityInfo a = acts.get(i);
            for (int j = i + 1; j < acts.size(); j++) {
                ActivityInfo b = acts.get(j);

                // Ignore si c'est le même cours
                if (a.coursId.equals(b.coursId)) continue;

                // Vérifie si mêmes jours
                boolean memeJour = a.days.stream().anyMatch(b.days::contains);
                if (!memeJour) continue;

                // Compare les heures
                if (overlap(a.start, a.end, b.start, b.end)) {
                    conflits.add(a + " CONFLIT AVEC " + b);
                }
            }
        }
        return conflits;
    }

    private boolean overlap(String s1, String e1, String s2, String e2) {
        return s1.compareTo(e2) < 0 && s2.compareTo(e1) < 0;
    }





    /**
     * Cette méthode permet de gérer la logique derrière la recherche de cours, que ce soit une recherche simple ou détaillée.
     * @param param paramètre de la recherche ( id, nom ou description)
     * @param value valeur de la recherche ( par exemple IFT1025)
     * @param includeSchedule "true" ou "false" indiquant si on veut inclure ou non le schedule ( absent pour la recherche simple)
     * @param session session si on veut être plus spécifiqeu ( absent pour la recherche simple)
     * @return la liste de cours associée à la recherche.
     */


    public Optional<List<Cours>> rechercherCours(String param, String value, String includeSchedule, String session) {
        // Vérification que param est valide (id, name, description)
        if (param == null ||
                !(param.equalsIgnoreCase("id") || param.equalsIgnoreCase("name") || param.equalsIgnoreCase("description"))) {
            System.out.println("Param doit être 'id', 'name' ou 'description'");
            return Optional.empty();

        }


        // Transformation en upper case si param == id ( pour ne pas générer d'erreur si l'utilisateur saisit ift1015 par exemple)
        if (param.equalsIgnoreCase("id") && value != null) {
            value = value.toUpperCase();
            // Vérifier la validité de l'ID
            if (!this.validateIdCours(value)) {
                System.out.println("L'id de cours est invalide. Veuillez saisir un id valide ( Ex: IFT1025)");
                return Optional.empty();
            }
        }

        // Vérification includeSchedule vs session
        if ((includeSchedule == null || includeSchedule.equalsIgnoreCase("false"))
                && session != null && !session.isEmpty()) {
            System.out.println("Impossible de filtrer par semester si includeSchedule=false");
            return Optional.empty();
        }

        try {
            // Appel au repository
            Optional<List<Cours>> coursListOpt = this.coursRepository.getCourseBy(param, value, includeSchedule, session);

            // Si null ou liste vide, on return empty
            if (coursListOpt.isEmpty() || coursListOpt.get().isEmpty()) {
                System.out.println("Le cours n'a pas été trouvé. Veuillez vérifier la documentation et fournir un body json correct.");
                return Optional.empty();
            }

            // Filtrage par session si nécessaire
            if (session != null && !session.isEmpty()) {

                List<Cours> filteredCours = coursListOpt.get().stream()
                        .map(cours -> {
                            // filtrer uniquement les schedules correspondant à la session
                            List<Cours.Schedule> schedulesFiltres = cours.getSchedules().stream()
                                    .filter(schedule -> session.equalsIgnoreCase(schedule.getSemester()))
                                    .toList();

                            // s'il n'y a aucun schedule pour cette session → ignorer ce cours
                            if (schedulesFiltres.isEmpty()) {
                                return null;
                            }

                            // remplacer la liste des schedules par les schedules filtrés
                            cours.setSchedules(schedulesFiltres);

                            return cours;
                        })
                        .filter(Objects::nonNull)
                        .toList();

                return filteredCours.isEmpty() ? Optional.empty() : Optional.of(filteredCours);
            }


            // Sinon retourner la liste
            return coursListOpt;

        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des cours : " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Cette méthode permet de vérifier l'éligibilité à un cours.
     * @param idCours id du cours dont on veut vérifier notre éligibilité.
     * @param listeCours liste des cours déjà faits
     * @return un message indiquant si on est éligible ou non.
     */

    public String checkEligibility(String idCours, List<String> listeCours) {


        if (!validateIdCours(idCours)) {
            return "L'id du cours est invalide";
        }

        boolean allValid = listeCours.stream()
                .allMatch(this::validateIdCours);

        if (!allValid) {
            return "Il y a des cours complétés invalides";
        }


        try {
            // on récupère le corps de la réponse de la requête.
            String responseBody = this.coursRepository.getCourseEligibility(idCours, listeCours);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            String retour = "";
            if (root.get("eligible").asBoolean()) {
                retour = "Vous êtes éligible à ce cours!";
            } else {
                retour = "Vous n'êtes pas éligible à ce cours. Il vous manque le(s) prerequis suivants:";
                JsonNode prerequisManquants = root.get("missing_prerequisites");

                List<String> coursManquants = new ArrayList<>();

                for (JsonNode item : prerequisManquants) {
                    coursManquants.add(item.asText());
                }

                retour = "Vous n'êtes pas éligible à ce cours. Il vous manque les prerequis suivants : "+ String.join(",", coursManquants);

            }

            return retour;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Une erreur est survenue lors de la vérification d'éligibilité.";

        }


    }

    /**
     * Cette methode permet d'obtenir les cours offerts dans un programme donne.
     * @param programID ID du programme.
     * @return Une liste contenant les ID des cours offerts pour un programme.
     **/
    public List<String> getCoursesForAProgram(String programID){
        List<String> listeCours = new ArrayList<>();
        String BASE_URL = "https://planifium-api.onrender.com/api/v1/programs";
        Map<String, String> params = Map.of(
                "programs_list", programID
        );
        URI uri  = getStringBuilder(BASE_URL,params);
        try{
            HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
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

            JsonNode json = mapper.readTree(response.toString());
            for(int i = 0; i < json.size(); i++){
                JsonNode courses = json.get(i).get("courses");

                if (courses != null) {
                    for (JsonNode c : courses) {
                        listeCours.add(c.asText());
                    }
                } else {
                    System.out.println("Aucun cours pour le programme " + programID +" .");
                }
            }
        }catch (IOException e) {
            System.out.println("Erreur lors de la récupération des requêtes : " + e.getMessage());
        }
        return listeCours;
    }

    /**
     * Cette methode permet d'obtenir la liste des cours disponible pour un trimestre donnee dans un programme.
     * @param programID ID du programme dans lequel il faut effectuer la recherche.
     * @param semester Il s'agit du trimestre pour laquelle on effectue la recherche.
     * @return Une liste contenant les ID des cours offerts pour le trimestre.
     **/
    public List<String> getCourseBySemester(String semester, String programID){
        return getCoursesForAProgram(programID)
                .parallelStream()
                .filter(id -> isCourseAvailable(id, semester))
                .toList();
    }


    /**
     * Cette methode permet d'obtenir l'horaire d'un cours pour un trimestre donné (structure interne).
     * @param courseID ID du cours.
     * @param semester trimestre pour lequel on désire obtenir l'horaire
     * @return Une map contenant les détails structurés par section.
     **/
    public Map<String,Map<String,Object>> getCourseScheduleMap(String courseID, String semester){
        Map<String,Map<String,Object>> courses = new HashMap<>();

        Optional<JsonNode> scheduleOpt = fetchSchedule(courseID, semester);
        if (scheduleOpt.isEmpty()) {
            System.out.println("Cours non disponible");
            return courses;
        }

        JsonNode jsonNode = scheduleOpt.get();
        for(JsonNode sections : jsonNode.get("sections")){
            Map<String,Object> details = new HashMap<>();
            StringBuilder profs = new StringBuilder();
            if(sections.get("teachers").isArray() && !sections.get("teachers").isEmpty()){
                for(JsonNode teachers : sections.get("teachers")){
                    profs.append(teachers.asText()).append("; ");
                }
                details.put("Professeur(s) :", profs.toString());
            }else{
                details.put("Professeur(s) :", "À communiquer");
            }
            details.put("Capacité:",sections.get("capacity").asText());
            int places = Integer.parseInt(sections.get("capacity").asText()) - Integer.parseInt(sections.get("number_inscription").asText());
            details.put("Places restantes :", String.valueOf(places));
            int increment = 1;
            for(JsonNode volets : sections.get("volets")){
                Map<String,Object> volets_activities = new HashMap<>();
                volets_activities.put("Volets : ",volets.get("name").asText());
                int count = 1;
                for (JsonNode activites : volets.get("activities")){
                    Map<String,Object> horaires = new HashMap<>();
                    StringBuilder jours = new StringBuilder();
                    if(activites.get("days").isArray() && !activites.get("days").isEmpty()){
                        for(JsonNode days : activites.get("days")){
                            switch (days.asText()){
                                case "Lu":
                                    jours.append("Lundi ; ");
                                    break;
                                case "Ma":
                                    jours.append("Mardi ; ");
                                    break;
                                case "Me" :
                                    jours.append("Mercredi ; ");
                                    break;
                                case "Je" :
                                    jours.append("Jeudi ; ");
                                    break;
                                case "Ve" :
                                    jours.append("Vendredi ; ");
                                    break;
                                case "Sa" :
                                    jours.append("Samedi ; ");
                                    break;
                                case "Di" :
                                    jours.append("Dimanche ; ");
                                    break;
                                default:
                                    jours.append(days.asText());

                            }
                        }
                        horaires.put("Jours :",  jours.toString());
                    }else{
                        horaires.put("Jours :", "");
                    }
                    horaires.put("Heures : ",activites.get("start_time").asText() + " - " + activites.get("end_time").asText());
                    horaires.put("Date de debut : ", activites.get("start_date").asText());
                    horaires.put("Date de fin : ", activites.get("end_date").asText());
                    horaires.put("Salle : ",activites.get("room").asText() + " " + activites.get("pavillon_name").asText());
                    horaires.put("Campus : ", activites.get("campus").asText());
                    horaires.put("Mode d'enseignement : ", activites.get("mode").asText());
                    volets_activities.put("Horaire (" + count + ") :", horaires);
                    count++;
                }
                volets_activities.put("Volets : ", volets.get("name").asText());
                details.put("Volet ("+ increment+") :", volets_activities);
                increment++;
            }
            courses.put("Section : "+sections.get("name").asText(),details);
        }
        return courses;
    }

    /**
     * Cette methode permet d'obtenir une sortie formatée (lisible) de l'horaire d'un cours.
     * @param courseID ID du cours.
     * @param semester trimestre pour lequel on désire obtenir l'horaire
     * @return Une liste de chaînes formatées décrivant les sections et horaires.
     **/
    public List<String> getCourseSchedule(String courseID, String semester){
        Map<String,Map<String,Object>> raw = getCourseScheduleMap(courseID, semester);
        List<String> formatted = new ArrayList<>();
        for (Map.Entry<String, Map<String,Object>> entry : raw.entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey()); // Section : A
            Map<String,Object> details = entry.getValue();
            // Professeurs
            Object prof = details.get("Professeur(s) :");
            if (prof != null) {
                sb.append("\nProfesseur(s) : ").append(prof.toString());
            }
            // Places restantes
            Object places = details.get("Places restantes :");
            if (places != null) {
                sb.append("\nPlaces restantes : ").append(places.toString());
            }
            // Parcourir les volets et horaires
            for (String k : details.keySet()) {
                if (k.startsWith("Volet (")) {
                    Object vo = details.get(k);
                    if (vo instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String,Object> vmap = (Map<String,Object>) vo;
                        for (Map.Entry<String,Object> vk : vmap.entrySet()) {
                            if (vk.getKey().startsWith("Horaire")) {
                                Object h = vk.getValue();
                                if (h instanceof Map) {
                                    @SuppressWarnings("unchecked")
                                    Map<String,Object> hmap = (Map<String,Object>) h;
                                    Object jours = hmap.get("Jours :");
                                    if (jours != null) sb.append("\n").append(jours.toString());
                                    Object heures = hmap.get("Heures : ");
                                    if (heures != null) sb.append(heures.toString());
                                }
                            }
                        }
                    }
                }
            }
            formatted.add(sb.toString());
        }
        return formatted;
    }

    /**
     *  Cette methode forme des URL en prenant en compte des paramètres de recherche.
     * @param BASE_URL URL de base sur lequel il faudra appliquer des paramètres.
     * @param params Paramètres qui doivent être ajouté a l'URL pour effectuer une recherche optimal.
     * @return Un URI valide.
     **/
    @NotNull
    private static URI getStringBuilder(String BASE_URL,Map<String, String> params) {
        // Allow overriding the Planifium base host for testing (e.g., local HTTP server)
        String override = System.getProperty("planifium.base");
        if (override != null && !override.isBlank()) {
            try {
                URI orig = URI.create(BASE_URL);
                URI over = URI.create(override);
                String combined = over.toString().replaceAll("/+$", "") + orig.getPath();
                BASE_URL = combined;
            } catch (Exception ignored) {
                // if parsing fails, fall back to the provided BASE_URL
            }
        }

        StringBuilder sb = new StringBuilder(BASE_URL);
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

    /**
     * Cette methode permet de verifier si un cours est disponible pour un trimestre donné.
     * @param id ID du cours.
     * @param semester Trimestre pour lequel on desire verifier la disponibilité d'un cours.
     * @return Un booléen qui est vrai si le cours est disponible et faux si le cours est indisponible.
     **/
    private boolean isCourseAvailable(String id, String semester){
        return fetchSchedule(id, semester).isPresent();
    }

    /**
     * Cette methode retourne le contenu pour un cours disponible pour un trimestre donné.
     * @param courseID ID du cours.
     * @param semester Trimestre pour lequel on desire verifier la disponibilité d'un cours.
     * @return Un Optional qui est vide si le cours est indisponible, sinon il retourne le contenu JsonNode.
     **/
    private Optional<JsonNode> fetchSchedule(String courseID, String semester){
        String baseUrl = "https://planifium-api.onrender.com/api/v1/schedules";
        Map<String, String> params = Map.of(
                "courses_list", "[\"" + courseID + "\"]",
                "min_semester", semester
        );

        URI uri = getStringBuilder(baseUrl, params);

        try {
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            ObjectMapper mapper = new ObjectMapper();;
            JsonNode json = mapper.readTree(connection.getInputStream());

            if (json.isArray()) {
                for (JsonNode node : json) {
                    if (semester.equals(node.get("semester").asText())) {
                        return Optional.of(node);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur API schedules : " + e.getMessage());
        }
        return Optional.empty();
    }
}



