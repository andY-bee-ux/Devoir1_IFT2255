package org.projet.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.projet.model.Avis;

import org.jetbrains.annotations.NotNull;
import org.projet.exception.HoraireException;
import org.projet.model.Cours;
import org.projet.model.Resultats;
import org.projet.repository.CoursRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.net.URL;

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

    protected boolean validateIdCours(String id) {
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
 * Analyse la difficulté globale de plusieurs combinaisons de cours.
 * La difficulté est estimée à partir de la moyenne des scores
 * des cours composant chaque combinaison.
 *
 * @param listeDeListesDeCours liste des combinaisons de cours
 * @return une liste de messages décrivant la difficulté de chaque combinaison
 */
public List<String> difficulteCombinaisonCours(List<List<String>> listeDeListesDeCours) {

    List<String> out = new ArrayList<>();
    int index = 1;

    for (List<String> combinaison : listeDeListesDeCours) {

        double sommeScores = 0.0;
        int nb = 0;

        for (String sigle : combinaison) {
            Resultats r = getResultats(sigle);
            if (r.isCoursPresent()) {
                sommeScores += r.getScore();
                nb++;
            }
        }

        if (nb == 0) {
            out.add("Combinaison " + index + " : aucun cours valide dans les résultats.");
        } else {
            double scoreMoyen = Math.round((sommeScores / nb) * 100.0) / 100.0;
            out.add(
                "Combinaison " + index +
                " : difficulté " +
                (scoreMoyen >= 4.0 ? "faible" :
                scoreMoyen >= 2.5 ? "moyenne" : "élevée") +
                " (score moyen = " + scoreMoyen + "/5)"
            );
        }

        index++;
    }

    return out;
}

/**
 * Analyse la popularité globale de plusieurs combinaisons de cours.
 * La popularité est estimée à partir du nombre total de participants
 * des cours composant chaque combinaison.
 *
 * @param listeDeListesDeCours liste des combinaisons de cours
 * @return une liste de messages décrivant la popularité de chaque combinaison
 */
public List<String> populariteCombinaisonCours(List<List<String>> listeDeListesDeCours) {

    List<String> out = new ArrayList<>();
    int index = 1;

    for (List<String> combinaison : listeDeListesDeCours) {

        int total = 0;
        int nb = 0;

        for (String sigle : combinaison) {
            Resultats r = getResultats(sigle);
            if (r.isCoursPresent()) {
                total += r.getParticipants();
                nb++;
            }
        }

        if (nb == 0) {
            out.add("Combinaison " + index + " : aucun cours valide dans les résultats.");
        } else {
            out.add(
                "Combinaison " + index +
                " : popularité " +
                (total >= 200 ? "élevée" :
                total >= 100 ? "moyenne" : "faible") +
                " (" + total + " participants)"
            );
        }

        index++;
    }

    return out;
}



/**
 * Compare plusieurs combinaisons de cours selon leurs statistiques agrégées.
 * La comparaison porte à la fois sur la difficulté et la popularité globales.
 *
 * @param listeDeListesDeCours liste des combinaisons de cours
 * @return une liste de maps contenant les comparaisons pour chaque combinaison
 */
public List<Map<String, String>> comparerCombinaisonStats(
        List<List<String>> listeDeListesDeCours
) {

    List<Map<String, String>> resultat = new ArrayList<>();
    int index = 1;

    for (List<String> combinaison : listeDeListesDeCours) {

        double sommeScores = 0.0;
        int totalParticipants = 0;
        int nbCoursValides = 0;

        for (String sigle : combinaison) {
            Resultats res = getResultats(sigle);
            if (res.isCoursPresent()) {
                sommeScores += res.getScore();
                totalParticipants += res.getParticipants();
                nbCoursValides++;
            }
        }

        Map<String, String> ligne = new HashMap<>();
        ligne.put("combinaison", "Combinaison " + index);
        ligne.put("cours", combinaison.toString());

        if (nbCoursValides == 0) {
            ligne.put("difficulte", "Aucune donnée disponible");
            ligne.put("popularite", "Aucune donnée disponible");
        } else {
            double scoreMoyen =
        Math.round((sommeScores / nbCoursValides) * 100.0) / 100.0;

            ligne.put(
                "difficulte",
                (scoreMoyen >= 4.0 ? "faible" :
                scoreMoyen >= 2.5 ? "moyenne" : "élevée")
                + " (score moyen = " + scoreMoyen + "/5)"
            );

            ligne.put(
                "popularite",
                (totalParticipants >= 200 ? "élevée" :
                totalParticipants >= 100 ? "moyenne" : "faible")
                + " (" + totalParticipants + " participants)"
            );

        }

        resultat.add(ligne);
        index++;
    }

    return resultat;
}

/**
     * Cette méthode permet de comparer des ensembles de cours.
     * @param listeDeListesDeCours liste des ensembles de cours qu'on veut comparer.
     * @return un tableau contenant les comparaisons des ensembles de cours donnés.
     */

    public List<List<String>> comparerCombinaisonCours(
            List<List<String>> listeDeListesDeCours) {

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

            }

          

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
 * Compare plusieurs combinaisons de cours uniquement
 * à partir des informations issues du catalogue.
 *
 * @param listeDeListesDeCours liste des combinaisons de cours
 * @return un tableau de comparaison basé sur le catalogue
 */
public List<List<String>> comparerCombinaisonCatalogue(
        List<List<String>> listeDeListesDeCours
) {
    return comparerCombinaisonCours(listeDeListesDeCours);
}

   /**
 * Compare des cours basé sur la note moyenne de difficulté
 * des avis ( inofficiel).
 * @param idsCours liste des cours
 * @return un tableau de comparaison basé sur les dits cours.
 */

 public List<List<String>> comparerCoursParNoteDifficulteAvis(String[] idsCours) {
        List<List<String>> result = new ArrayList<>();

        for (String id : idsCours) {
            if (!validateIdCours(id)) {
                System.out.println("Cours non valide : " + id);
                continue; // On passe au suivant au lieu de return null
            }

            List<String> ligne = new ArrayList<>();
            ligne.add(id); // Ajouter le sigle du cours

            try {
                List<Avis> avis = avisService.getAvisParCours(id); // récupère les avis pour ce cours
                if (avis == null || avis.isEmpty()) {
                    ligne.add("Pas d'avis");
                } else {
                    float sum = 0;
                    for (Avis av : avis) {
                        sum += av.getNoteDifficulte();
                    }
                    float moyenne = sum / avis.size();
                    ligne.add(String.format("%.2f", moyenne)); // ajouter la moyenne formatée
                }
            } catch (Exception e) {
                ligne.add("Erreur récupération avis");
                e.printStackTrace();
            }

            result.add(ligne);
        }

        return result;
    }

     /**
 * Compare des cours basé sur la note moyenne de charge de travail
 * des avis ( inofficiel).
 * @param idsCours liste des cours
 * @return un tableau de comparaison basé sur les dits cours.
 */
    public List<List<String>> comparerCoursParChargeTravailAvis(String[] idsCours) {
        List<List<String>> result = new ArrayList<>();

        for (String id : idsCours) {
            if (!validateIdCours(id)) {
                System.out.println("Cours non valide : " + id);
                continue; // passer au suivant si le cours n'est pas valide
            }

            List<String> ligne = new ArrayList<>();
            ligne.add(id); // Ajouter le sigle du cours

            try {
                List<Avis> avis = avisService.getAvisParCours(id); // récupère les avis pour ce cours
                if (avis == null || avis.isEmpty()) {
                    ligne.add("Pas d'avis");
                } else {
                    float sum = 0;
                    for (Avis av : avis) {
                        sum += av.getNoteChargeTravail(); // utiliser noteChargeTravail
                    }
                    float moyenne = sum / avis.size();
                    ligne.add(String.format("%.2f", moyenne)); // ajouter la moyenne formatée
                }
            } catch (Exception e) {
                ligne.add("Erreur récupération avis");
                e.printStackTrace();
            }

            result.add(ligne);
        }

        return result;
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

            // On valide si ça ressemble à un sigle complet
            if (value.matches("^[A-Z]{3}\\d{4}$")) {
                if (!this.validateIdCours(value)) {
                    System.out.println("L'id de cours est invalide. Veuillez saisir un id valide ( Ex: IFT1025)");
                    return Optional.empty();
                }
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
     * Cette méthode permet d'extraire la partie numérique d’un identifiant de cours.
     * Par exemple, pour {@code "IFT2255"}, la méthode retourne {@code 2255}.
     * Si l’identifiant est invalide ou ne contient aucun chiffre,
     * la méthode retourne {@code -1}.
     *
     * @param idCours identifiant du cours
     * @return le numéro du cours ou {@code -1} si l’extraction échoue
     */

    private int extractCourseNumber(String idCours) {
        if (idCours == null) return -1;

        String digits = idCours.replaceAll("\\D+", "");
        if (digits.isBlank()) return -1;

        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Cette méthode vérifie l’éligibilité d’un étudiant à un cours donné.
     * @param idCours identifiant du cours
     * @param listeCours cours complétés
     * @param cycle cycle d’études de l’étudiant
     * @return message d’éligibilité
     */
    public String checkEligibilityNew(String idCours, List<String> listeCours, Integer cycle) {

        if (!validateIdCours(idCours)) {
            return "L'id du cours est invalide";
        }

        if (listeCours == null) {
            return "La liste des cours complétés est invalide";
        }

        boolean allValid = listeCours.stream()
                .allMatch(this::validateIdCours);

        if (!allValid) {
            return "Il y a des cours complétés invalides";
        }

        if (cycle == null) {
            return "Le cycle doit être fourni";
        }
        if (cycle < 1 || cycle > 4) {
            return "Le cycle fourni est invalide";
        }

        int courseNumber = extractCourseNumber(idCours);
        if (courseNumber == -1) {
            return "Impossible de déterminer le niveau du cours";
        }

        // La règle c'est qu'un étudiant de 1er cycle ne peut pas prendre un cours 6000+
        if (cycle == 1 && courseNumber >= 6000) {
            return "Ce cours est un cours de cycles supérieurs. Les étudiants de 1er cycle ne peuvent y être admissibles que dans des cas particuliers (ex. cheminement Honor).";
        }
        // PS: On n'impose pas de restriction pour les autres cycles vu que par exemple un étudiant de 2e cycle peut prendre des cours de 1er cycle et
        // nous n'avons pas d'information précise sur le cycle lié à un cours donné. 


        try {
            String responseBody =
                    coursRepository.getCourseEligibility(idCours, listeCours);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);

            if (root.get("eligible").asBoolean()) {
                return "Vous êtes éligible à ce cours!";
            }

            JsonNode prerequisManquants = root.get("missing_prerequisites");
            List<String> coursManquants = new ArrayList<>();

            for (JsonNode item : prerequisManquants) {
                coursManquants.add(item.asText());
            }

            return "Vous n'êtes pas éligible à ce cours. Il vous manque les prerequis suivants : "
                    + String.join(", ", coursManquants);

        } catch (Exception e) {
            return "Une erreur est survenue lors de la vérification d'éligibilité.";
        }
    }
    

    /**
     * Cette méthode génère toutes les combinaisons d’horaires possibles pour un ensemble de cours
     * donné, pour une session spécifique.
     * La structure retournée est organisée comme suit :
     * cours -> type de volet (TH/démo) -> section -> liste de blocs horaires.
     * Les horaires d’examens (intra, final) sont ignorés et les doublons
     * d’activités sont éliminés.
     *
     * @param idCours liste des identifiants de cours sélectionnés
     * @param session session académique visée (ex. Automne, Hiver)
     * @return une structure représentant tous les horaires possibles
     * @throws HoraireException si les paramètres sont invalides ou si un cours
     *         ne peut pas être récupéré
     */
    public Map<String, Map<String, Map<String, List<List<String>>>>> genererEnsembleHoraire(
            List<String> idCours,
            String session
    ) {

        if (idCours == null || idCours.isEmpty()) {
            throw new HoraireException("La liste des cours est vide ou inexistante.");
        }

        if (idCours.size() > 6) {
            throw new HoraireException(
                "Un ensemble de cours ne peut pas contenir plus de 6 cours."
            );
        }

        if (session == null || session.isBlank()) {
            throw new HoraireException("La session doit être spécifiée.");
        }

        // structure finale: cours -> volet (TH/démo) -> section -> blocs horaires
        Map<String, Map<String, Map<String, List<List<String>>>>> resultat = new HashMap<>();
        // pour éviter les doublons d'activités (un horaire peut apparaître plusieurs fois dans l'API)
        Set<String> seen = new HashSet<>();

        // on parcourt chaque cours demandé
        for (String id : idCours) {

            if (!validateIdCours(id)) {
                throw new HoraireException("Identifiant de cours invalide : " + id);
            }

            Optional<List<Cours>> opt;
            try {
                opt = coursRepository.getCourseBy("id", id, "true", null);
            } catch (Exception e) {
                throw new HoraireException(
                    "Erreur lors de la récupération du cours " + id
                );
            }

            if (opt.isEmpty()) {
                throw new HoraireException(
                    "Le cours " + id + " n’a pas pu être récupéré."
                );
            }

            Cours cours = opt.get().get(0);
            if (cours.getSchedules() == null) continue;

            resultat.putIfAbsent(cours.getId(), new HashMap<>());

            // on parcourt les hoaraires pour trouver ceux de la session demandée
            for (Cours.Schedule s : cours.getSchedules()) {

                if (!s.getSemester().equalsIgnoreCase(session)) continue;
                if (s.getSections() == null) continue;

                for (Cours.Section section : s.getSections()) {
                    if (section.getVolets() == null) continue;

                    String sectionName = section.getName();

                    for (Cours.Volet volet : section.getVolets()) {

                        String rawVoletName = volet.getName();
                        if (rawVoletName == null) continue;
                        
                        // on ignore les horaires d'examens vu que le but est d'afficher un horaire hebdomadaire pour un ensemble de cours
                        String lower = rawVoletName.toLowerCase();
                        if (lower.contains("intra") || lower.contains("final")) continue;

                        String voletKey = lower.equals("th") ? "TH" : "TP";

                        resultat.get(cours.getId())
                                .putIfAbsent(voletKey, new HashMap<>());

                        resultat.get(cours.getId())
                                .get(voletKey)
                                .putIfAbsent(sectionName, new ArrayList<>());

                        if (volet.getActivities() == null) continue;

                        for (Cours.Activity act : volet.getActivities()) {

                            String key = cours.getId() + "|" +
                                        voletKey + "|" +
                                        sectionName + "|" +
                                        act.getDays() + "|" +
                                        act.getStart_time() + "-" + act.getEnd_time();

                            if (seen.contains(key)) continue;
                            seen.add(key);

                            // ajout du bloc horaire [jours, heures]
                            List<String> bloc = new ArrayList<>();
                            bloc.add(act.getDays().toString());
                            bloc.add(act.getStart_time() + "-" + act.getEnd_time());

                            resultat.get(cours.getId())
                                    .get(voletKey)
                                    .get(sectionName)
                                    .add(bloc);
                        }
                    }
                }
            }
        }

        return resultat;
    }

    
    /**
     * Cette méthode permet d'appliquer les choix de sections (théorie et démonstration) effectués par l’utilisateur
     * à un ensemble d’horaires générés.
     * 
     * La méthode valide la cohérence des choix fournis, construit l’horaire
     * final correspondant et détecte les conflits horaires éventuels.
     * En cas d’erreurs multiples, celles-ci sont regroupées et retournées
     * à l’utilisateur.
     *
     * @param horaires ensemble des horaires possibles par cours
     * @param choix choix de sections effectués par l’utilisateur
     * @return un {@code ResultatHoraire} contenant l’horaire final et les conflits
     * @throws HoraireException si les choix sont invalides ou incohérents
     */
   public ResultatHoraire appliquerChoix(
            Map<String, Map<String, Map<String, List<List<String>>>>> horaires,
            Map<String, Map<String, String>> choix) {

        if (horaires == null || choix == null) {
            throw new HoraireException("Requête invalide.");
        }

        Map<String, List<List<String>>> resultat = new HashMap<>();
        List<String> erreurs = new ArrayList<>();

        // on vérifie qu'il n'y pas de choix fournis pour des cours non demandés 
        for (String coursId : choix.keySet()) {
            if (!horaires.containsKey(coursId)) {
                erreurs.add(
                    "Choix fourni pour un cours non sélectionné : " + coursId
                );
            }
        }

        // validation des choix pour chaque cours
        for (String coursId : horaires.keySet()) {

            if (!choix.containsKey(coursId)) {
                erreurs.add(
                    "Cours " + coursId +
                    " : aucun choix de sections n’a été fourni."
                );
                continue;
            }

            Map<String, Map<String, List<List<String>>>> volets =
                    horaires.get(coursId);

            Map<String, String> choixCours = choix.get(coursId);

            String sectionTH = choixCours.get("TH");
            String sectionTP = choixCours.get("TP");

            // une section de théorie est OBLIGATOIRE
            if (sectionTH == null) {
                erreurs.add(
                    "Cours " + coursId +
                    " : aucune section de théorie (TH) n’a été choisie."
                );
                continue;
            }

            // validation de la section de théorie
            if (!volets.containsKey("TH")
                    || !volets.get("TH").containsKey(sectionTH)) {
                erreurs.add(
                    "Cours " + coursId +
                    " : la section de théorie " + sectionTH + " n’existe pas."
                );
                continue;
            }

            // validation des horaires de la section de théorie
            if (volets.get("TH").get(sectionTH).isEmpty()) {
                erreurs.add(
                    "Cours " + coursId +
                    " : la section " + sectionTH + " ne comporte aucun horaire."
                );
                continue;
            }

            List<List<String>> blocs = new ArrayList<>();
            blocs.addAll(volets.get("TH").get(sectionTH));

            // validation de la section de TP (si applicable)
            boolean hasTP = volets.containsKey("TP") && !volets.get("TP").isEmpty();

            // si le cours a des démos, un choix est obligatoire
            if (hasTP && sectionTP == null) {
                erreurs.add(
                    "Cours " + coursId +
                    " : des séances de démonstration sont offertes, un choix de TP est obligatoire."
                );
                continue;
            }

            if (sectionTP != null) {

                // validation de la section de TP
                if (!volets.containsKey("TP")
                        || !volets.get("TP").containsKey(sectionTP)) {
                    erreurs.add(
                        "Cours " + coursId +
                        " : la section de TP " + sectionTP + " n’existe pas."
                    );
                    continue;
                }

                // validation de la correspondance TH/groupe de démo
                if (!sectionTP.startsWith(sectionTH)) {
                    erreurs.add(
                        "Cours " + coursId +
                        " : le TP " + sectionTP +
                        " ne correspond pas à la section théorique " + sectionTH + "."
                    );
                    continue;
                }

                blocs.addAll(volets.get("TP").get(sectionTP));
            }

            resultat.put(coursId, blocs);
        }

        // liste des erreurs rencontrées pour que l'utilisateur puisse voir partout où il y'a eu un problème
        if (!erreurs.isEmpty()) {
            throw new HoraireException(String.join("\n", erreurs));
        }

        // Détection des conflits horaires
        List<ConflitHoraireGroupe> conflits = detecterConflits(resultat);

        return new ResultatHoraire(resultat, conflits);
    }


    
    /**
     * Cette classe représente le résultat final d’une génération d’horaire.
     * Contient l’horaire retenu pour chaque cours ainsi que la liste
     * des conflits horaires détectés.
     */
    public class ResultatHoraire {
        public Map<String, List<List<String>>> horaire;
        public List<ConflitHoraireGroupe> conflits;

        public ResultatHoraire(
                Map<String, List<List<String>>> horaire,
                List<ConflitHoraireGroupe> conflits
        ) {
            this.horaire = horaire;
            this.conflits = conflits;
        }
    }

    /**
     * Cette classe représente un conflit horaire entre plusieurs cours
     * sur un même jour et un même intervalle de temps.
     */
    public class ConflitHoraireGroupe {
        public String jour;
        public String intervalle;
        public Set<String> cours;

        public ConflitHoraireGroupe(String jour, String intervalle) {
            this.jour = jour;
            this.intervalle = intervalle;
            this.cours = new HashSet<>();
        }
    }


    
    /**
     * Cette méthode détecte les conflits horaires dans un horaire final.
     * Deux activités sont en conflit si elles ont lieu le même jour
     * et que leurs intervalles horaires se chevauchent.
     *
     * @param horaireFinal horaire final par cours
     * @return une liste de groupes de conflits horaires
     */
    private List<ConflitHoraireGroupe> detecterConflits(
        Map<String, List<List<String>>> horaireFinal) {

    class Bloc {
        String cours;
        String jour;
        int debut;
        int fin;
    }

    List<Bloc> blocs = new ArrayList<>();

    // on extrait tous les blocs horaires
    for (String coursId : horaireFinal.keySet()) {
        for (List<String> b : horaireFinal.get(coursId)) {

            String jour = b.get(0)
                    .replace("[", "")
                    .replace("]", "");

            String[] heures = b.get(1).split("-");

            Bloc bloc = new Bloc();
            bloc.cours = coursId;
            bloc.jour = jour;
            bloc.debut = toMinutes(heures[0]);
            bloc.fin = toMinutes(heures[1]);

            blocs.add(bloc);
        }
    }

    // détection des conflits
    Map<String, ConflitHoraireGroupe> groupes = new HashMap<>();

    for (int i = 0; i < blocs.size(); i++) {
        for (int j = i + 1; j < blocs.size(); j++) {

            Bloc a = blocs.get(i);
            Bloc b = blocs.get(j);

            if (a.cours.equals(b.cours)) continue;
            if (!a.jour.equals(b.jour)) continue;

            boolean chevauchement =
                    a.debut < b.fin && b.debut < a.fin;

            if (chevauchement) {

                int debut = Math.max(a.debut, b.debut);
                int fin = Math.min(a.fin, b.fin);

                String intervalle =
                        format(debut) + "-" + format(fin);

                String key = a.jour + "|" + intervalle;

                groupes.putIfAbsent(
                        key,
                        new ConflitHoraireGroupe(a.jour, intervalle)
                );

                groupes.get(key).cours.add(a.cours);
                groupes.get(key).cours.add(b.cours);
            }
        }
    }

    return new ArrayList<>(groupes.values());
}
    /**
     * Cette méthode permet de convertir une heure au format {@code HH:mm} en minutes.
     *
     * @param time heure sous forme de chaîne
     * @return le nombre de minutes correspondantes
     */
    private int toMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60
            + Integer.parseInt(parts[1]);
    }
    /**
     * Cette méthode permet de convertir un nombre de minutes en une heure
     * formatée {@code HH:mm}.
     *
     * @param minutes nombre de minutes
     * @return l’heure formatée
     */
    private String format(int minutes) {
        return String.format("%02d:%02d", minutes / 60, minutes % 60);
    }

    /**
     * Cette methode retourne une liste de nom de programme qui contienne le {@code nom} fournis en parametre
     * @param nom Nom de programme que l'utilisateur utilise pour sa recherche.
     * @return une liste de proposition de programme qui correspond à la recherche de l'utilisateur.
     **/
    public List<String> foundProgramms(String nom){
        List<String> propositions = new ArrayList<>();
        List<Map<String, String>> programs = this.coursRepository.getAllPrograms();
        for(Map<String, String> program : programs) {
            if (program.get("name").contains(nom)) propositions.add(program.get("name"));
        }
        return propositions;
    }

    /**
     * Cette methode retourne un id de programme lorsqu'on lui passe le nom en parametre.
     * @param nom Nom du programme.
     * @return L'id du programme.
     **/
    public String foundProgrammId(String nom){
        String programmId = "";
        List<Map<String, String>> programs = this.coursRepository.getAllPrograms();
        for(Map<String, String> program : programs) {
            if (program.get("name").equals(nom)) programmId = program.get("id");
        }
        return programmId;
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

    /**
 * Récupère les données de performance et de participation pour un cours spécifique.
 * Cette méthode initialise un nouvel objet Resultats, ce qui déclenche 
 * la recherche et l'extraction des données depuis le fichier CSV historique.
 *
 * @param sigleCours Le code unique du cours (ex: "IFT1015") à rechercher dans la base de données.
 * @return Une instance de {@link Resultats} contenant les statistiques du cours, 
 * ou un objet avec des valeurs par défaut si le cours n'est pas trouvé.
 */    
public Resultats getResultats(String sigleCours) {
    return new Resultats(sigleCours);
}    



/**
     * Évalue la difficulté d'un cours en fonction de son score moyen.
     * Les seuils sont : >= 4.0 (facile), >= 2.5 (moyenne), < 2.5 (difficile).
     *
     * @param resultats L'objet contenant les données du cours à analyser.
     * @return Un message décrivant la difficulté ou un message d'erreur si le cours est absent.
     */
public String difficulteCours(Resultats resultats) {
    if (!resultats.isCoursPresent()) {
        return "Le cours demandé est absent des résultats. Veuillez vérifier le sigle.";
    }
    double score = resultats.getScore();
    if (score >= 4.0) {
        return "Le cours " + resultats.getNom() + " est considéré comme facile avec un score de " + score + "/5";
    } else if (score >= 2.5) {
        return "Le cours " + resultats.getNom() + " est considéré comme de difficulté moyenne avec un score de " + score + "/5";
    } else {
        return "Le cours " + resultats.getNom() + " est considéré comme difficile avec un score de " + score + "/5.";
    }}

/**
     * Évalue la popularité d'un cours en fonction du nombre de participants.
     * Les seuils sont : >= 200 (très populaire), >= 100 (modérément populaire), < 100 (peu populaire).
     *
     * @param resultats L'objet contenant les données du cours à analyser.
     * @return Un message décrivant la popularité ou un message d'erreur si le cours est absent.
     */
public String populariteCours(Resultats resultats) {
    if (!resultats.isCoursPresent()) {
        return "Le cours demandé est absent des résultats. Veuillez vérifier le sigle.";
    }
    int participants = resultats.getParticipants();
    if (participants >= 200) {
        return "Le cours " + resultats.getNom() + " est très populaire avec " + participants + " participants.";
    } else if (participants >= 100) {
        return "Le cours " + resultats.getNom() + " est modérément populaire avec " + participants + " participants.";
    } else {
        return "Le cours " + resultats.getNom() + " est peu populaire avec seulement " + participants + " participants.";
    }

}


/**
     * Compare le nombre de participants entre deux cours pour déterminer le plus populaire.
     *
     * @param res1 Les résultats du premier cours.
     * @param res2 Les résultats du deuxième cours.
     * @return Un message comparatif indiquant quel cours a le plus de participants.
     */
public String comparerPopularite(Resultats res1, Resultats res2) {
    if (!res1.isCoursPresent() || !res2.isCoursPresent()) {
        return "L'un des cours demandés est absent des résultats. Veuillez vérifier les sigles.";
    }
    int participants1 = res1.getParticipants();
    int participants2 = res2.getParticipants();

    if (participants1 > participants2) {
        return "Le cours " + res1.getNom() + " est plus populaire que " + res2.getNom() +
                " avec " + participants1 + " participants contre " + participants2 + ".";
    } else if (participants1 < participants2) {
        return "Le cours " + res2.getNom() + " est plus populaire que " + res1.getNom() +
                " avec " + participants2 + " participants contre " + participants1 + ".";
    } else {
        return "Les deux cours ont la même popularité avec " + participants1 + " participants chacun.";
    }

}


/**
     * Compare les scores de deux cours pour déterminer lequel est le plus facile.
     * Un score plus élevé indique un cours plus facile selon les critères établis.
     *
     * @param res1 Les résultats du premier cours.
     * @param res2 Les résultats du deuxième cours.
     * @return Un message comparatif indiquant la difficulté relative des deux cours.
     */
public String comparerDifficulte(Resultats res1, Resultats res2) {
    if (!res1.isCoursPresent() || !res2.isCoursPresent()) {
        return "L'un des cours demandés est absent des résultats. Veuillez vérifier les sigles.";
    }
    double score1 = res1.getScore();
    double score2 = res2.getScore();

    if (score1 > score2) {
        return "Le cours " + res1.getNom() + " est considéré comme plus facile que " + res2.getNom() +
                " avec un score de " + score1 + "/5 contre " + score2 + "/5.";
    } else if (score1 < score2) {
        return "Le cours " + res2.getNom() + " est considéré comme plus facile que " + res1.getNom() +
                " avec un score de " + score2 + "/5 contre " + score1 + "/5.";
    } else {
        return "Les deux cours ont la même difficulté avec un score de " + score1 + "/5 chacun.";
    }


}

}



