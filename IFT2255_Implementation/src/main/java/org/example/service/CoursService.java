package org.example.service;

import org.example.model.Cours;
import org.example.repository.CoursRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class CoursService {
    CoursRepository CoursRepository;
    public CoursService(CoursRepository CoursRepository) {
        this.CoursRepository = CoursRepository;
    }

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
 // method from https://www.baeldung.com/java-check-string-number
    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }
    // Cette méthode permet de vérifier si le id du cours est valide.
    private boolean validateIdCours(String id) {
        try {
            Optional<List<String>> listeCours = this.CoursRepository.getAllCourses();

            // Si l'Optional contient une liste → on vérifie si id est dedans
            return listeCours
                    .map(list -> list.contains(id))
                    .orElse(false);  // si vide → false
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }



    // je pense que ça sert à rien car dans planifium on peut pas get un cours by name.
    //private boolean validateNomCours(int id){return false;}

    // Cette méthode permet de comparer des cours selon certains critères
    public List<List<String>> comparerCours(String[] cours, String[] criteresComparaison) {

        List<List<String>> resultat = new ArrayList<>();

        // Charger les cours
        List<Cours> coursTrouves = new ArrayList<>();

        for (String nomCours : cours) {

            if (!validateIdCours(nomCours)) {
                System.out.println("Cours non valide : " + nomCours);
                return null;
            }

            try {
                Optional<Cours> opt = this.CoursRepository.getCourseById(nomCours);
                if (opt.isEmpty()) {
                    System.out.println("Cours introuvable : " + nomCours);
                    return null;
                }

                coursTrouves.add(opt.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }

        // Comparer selon les critères
        for (Cours coursObj : coursTrouves) {

            List<String> ligne = new ArrayList<>();
            ligne.add(coursObj.getId()); // toujours afficher l'id en premier ? à toi de voir

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
                        ligne.add(Arrays.toString(coursObj.getSchedules()));
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

            resultat.add(ligne);
        }

        return resultat;




}

    // A faire
    public List<String> comparerCombinaisonCours(Cours[][] cours){return new ArrayList<>();}

    // A faire
    public List<Cours> getAllCours(){return new ArrayList<>();}
}
