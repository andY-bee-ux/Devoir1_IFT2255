package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Cours;
import org.example.repository.CoursRepository;

import java.util.*;

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
     * Cette méthode gère la logique derrière la validation de l'id d'un cours ( permet de vérifier
     * si l'id donné correspond à un cours existant.)
     *
     * @param id id du Cours à vérifier
     * @return un booléen indiquant si l'id est valide ou non
     */
    private boolean validateIdCours(String id) {
        try {
            // le getAllCourses retourne la liste de tous les cours de l'UdeM.
            Optional<List<String>> listeCours = this.coursRepository.getAllCoursesId();

            // Si l'Optional contient une liste , on vérifie si id en fait partie
            return listeCours
                    .map(list -> list.contains(id))
                    .orElse(false);  // si c'est vide alors on retourne false
        }
        // Ce bloc permet de catch l'exception potentiellement lancée par getAllCourses().
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    /**
     * Cette méthode permet de comparer une liste de cours selon des critères donnés.
     * @param cours cours à comparer
     * @param criteresComparaison critères de comparaison
     * @return un tableau contenant les "valeurs" critères associés aux cours.
     */
    public List<List<String>> comparerCours(String[] cours, String[] criteresComparaison) {

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
                        ligne.add(Arrays.toString(coursObj.getSchedules().toArray()));
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

    public List<List<String>> comparerCombinaisonCours(List<List<String>> listeDeListesDeCours) {

        List<List<String>> resultat = new ArrayList<>();
        // index de la combinaison ( combinaison 1, combinaison 2 etc)
        int index = 1;
        // on parcourt chaque ensemble de cours
        for (List<String> combinaison : listeDeListesDeCours) {

            // On transforme les ids en objets Cours
            List<Cours> coursCombinaison = new ArrayList<>();

            for (String idCours : combinaison) {
                // on vérifie si les ids sont valides.
                if (!validateIdCours(idCours)) {
                    System.out.println("Cours non valide : " + idCours);
                    return null;
                }
                try {
                    Optional<List<Cours>> opt = this.coursRepository.getCourseBy("id", idCours, "true", null);
                    if (opt.isEmpty()) {
                        throw new RuntimeException("Cours introuvable : " + idCours);
                    }
                    coursCombinaison.add(opt.get().get(0));

                } catch (Exception e) {
                    throw new RuntimeException("Erreur lors du chargement de " + idCours, e);
                }
            }

            // Ce bloc permet de calculer les métriques de comparaison.
            int creditsTotaux = 0;
            Set<String> listeDeTousLesPrerequis = new HashSet<>();
            Set<String> listeDeTousLesConcomitants= new HashSet<>();
            Map<String,Boolean> periodesCommunes = null;
            Map<String,Boolean> sessionsCommunes = null;

            for (Cours c : coursCombinaison) {

                // crédits
                creditsTotaux += c.getCredits();

                // prerequis
                if (c.getPrerequisite_courses() != null)
                    listeDeTousLesPrerequis.addAll(Arrays.asList(c.getPrerequisite_courses()));

                // concomitants
                if (c.getConcomitant_courses() != null)
                    listeDeTousLesConcomitants.addAll(Arrays.asList(c.getConcomitant_courses()));
                // available periods
                Map<String, Boolean> periods = c.getAvailable_periods();

                if (periods != null) {

                    if (periodesCommunes == null) {
                        // Première initialisation : on ne garde que les périodes true
                        periodesCommunes = new HashMap<>();
                        for (Map.Entry<String, Boolean> entry : periods.entrySet()) {
                            if (entry.getValue()) {
                                periodesCommunes.put(entry.getKey(), true);
                            }
                        }

                    } else {
                        // Intersection : garde uniquement les périodes true chez TOUS les cours
                        periodesCommunes.entrySet().removeIf(e ->
                                !periods.getOrDefault(e.getKey(), false)
                        );
                    }

                }
                // available_terms
                Map<String, Boolean> terms = c.getAvailable_terms();

                if (terms!= null) {

                    if (sessionsCommunes == null) {
                            sessionsCommunes = new HashMap<>();
                        for (Map.Entry<String, Boolean> entry : terms.entrySet()) {
                            if (entry.getValue()) {
                                sessionsCommunes.put(entry.getKey(), true);
                            }
                        }

                    } else {
                        sessionsCommunes.entrySet().removeIf(e ->
                                !terms.getOrDefault(e.getKey(), false)
                        );
                    }

                }


            }

            // 3. Construire une ligne dans le tableau pour cette combinaison
            List<String> ligne = new ArrayList<>();
            ligne.add("Combinaison " + index);
            ligne.add("Nombre de cours=" + combinaison.size());
            ligne.add("Crédits=" + creditsTotaux);
            ligne.add("Liste de prérequis=" + listeDeTousLesPrerequis);
            // pb ça retourne les élements de la liste donnée.
            ligne.add("Liste de concomitants=" + listeDeListesDeCours);
            ligne.add("périodes communes=" +
                    (periodesCommunes == null ? "[]" : periodesCommunes.keySet().toString()));
            ligne.add("sessions communes=" +
                    (sessionsCommunes == null ? "[]" : sessionsCommunes.keySet().toString()));

            resultat.add(ligne);
            index++;
        }

        return resultat;
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
        // on vérifie si l'id est valide
        if (param.equalsIgnoreCase("id") && !this.validateIdCours(value)) {
            return Optional.empty();
        }

        // Nous aurions aussi voulu vérifier le name mais vu que les mots-clés et les noms ne sont pas uniques,
        // par exemple Programmation 1 correspond à deux cours, on s'est dits qu'on donnera la voie libre
        // à l'utilisateur pour l'instant, et il verra juste null si le mot-clé ne correspond à aucun cours
        // ( pour l'instant.)

        try {
            // Appel au repository
            Optional<List<Cours>> coursListOpt = this.coursRepository.getCourseBy(param, value,includeSchedule,session);

            // Si null ou liste vide, on return empty
            if (coursListOpt.isEmpty() || coursListOpt.get().isEmpty()) {
                return Optional.empty();
            }

            if (session != null && !session.isEmpty()) {
                List<Cours> filtered = new ArrayList<>();
                for (Cours cours : coursListOpt.get()) {
                    boolean hasSemester = cours.getSchedules().stream()
                            .anyMatch(schedule -> session.equals(schedule.getSemester()));
                    if (hasSemester) {
                        filtered.add(cours);
                    }
                }
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
            String responseBody = this.coursRepository.checkCourseEligibility(idCours, listeCours);
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



