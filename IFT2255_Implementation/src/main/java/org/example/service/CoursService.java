package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Cours;
import org.example.repository.CoursRepository;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    private boolean overlap(ActivityInfo a, ActivityInfo b) {
        // Si les jours ne se chevauchent pas
        if (a.days.stream().noneMatch(b.days::contains)) return false;
        // Comparaison des heures
        return !(a.endTime.compareTo(b.startTime) <= 0 || b.endTime.compareTo(a.startTime) <= 0);
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
                System.out.println("Le cours n'a pas été trouvé. Veuillez vérifier la documentation et fournir un body json correct.")
                return Optional.empty();
            }

            // Filtrage par session si nécessaire
            if (session != null && !session.isEmpty()) {
                List<Cours> filtered = coursListOpt.get().stream()
                        .filter(cours -> cours.getSchedules().stream()
                                .anyMatch(schedule -> session.equalsIgnoreCase(schedule.getSemester())))
                        .toList();

                return filtered.isEmpty() ? Optional.empty() : Optional.of(filtered);
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
     * @param idCours
     * @param listeCours
     * @return
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
            String responseBody = this.coursRepository.getCourseEligibility(idCours, listeCours);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            String retour = "";
            if (root.get("eligible").asBoolean()) {
                retour = "Vous êtes éligible à ce cours!";
            } else {
                retour = "Vous n'êtes pas éligible à ce cours. Il vous manque le(s) prerequis suivants:";
                JsonNode missing = root.get("missing_prerequisites");

                List<String> coursManquants = new ArrayList<>();

                for (JsonNode item : missing) {
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

}



