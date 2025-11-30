package org.example.service;

import org.example.model.Cours;
import org.example.repository.CoursRepository;

import static org.mockito.ArgumentMatchers.refEq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * Cette classe permet de gérer la logique métier associée à la manipulation des cours.
 * La classe CoursService est un Singleton, car c'est pas nécessaire d'avoir plusieurs instances
 */
public class CoursService {
    // Le Course repository est un singleton, donc on récupère juste l'instance associée.
    private CoursRepository coursRepository = CoursRepository.getInstance();

    /**
     Ce bloc de code permet de définir la classe CoursService comme un Singleton pour
     garantir que cette dernière n'ait qu'unen instance.
     **/
    private static CoursService instance;
    private CoursService() {}
    public static CoursService getInstance() {
        if (instance == null) {
            instance = new CoursService();
        }
        return instance;
    }

    /**
     * Cette méthode permet de récupérer le CoursRepository associé à l'instance CoursService
     * @return l'instance CoursRepository
     */
    public CoursRepository getCoursRepository() {
        return coursRepository;
    }

    /**
     * Cette méthode gère la logique derrière la validation de l'id d'un cours ( permet de vérifier
     * si l'id donné correspond à un cours existant.)
     * @param id id du Cours à vérifier
     * @return un booléen indiquant si l'id est valide ou non
     */
    private boolean validateIdCours(String id) {
        try {
            // le getAllCourses retourne la liste de tous les cours de l'UdeM.
            Optional<List<String>> listeCours = this.coursRepository.getAllCoursesId();

            // Si l'Optional contient une liste , on vérifie si id est dedans
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

    // Verifie si le format de la session est valide.
    private boolean validateSession(String session){
        String year = session.substring(1);
        Character season = session.charAt(0);

        Boolean valid;
        Boolean seasonValide = season.equals('H') || season.equals('A') || season.equals('E');
        if(session.length() == 3 && seasonValide == true){
            try{
                Integer.parseInt(year);
                valid = true;
            }catch (NumberFormatException e){
                valid = false;
            }

        }else{
            valid = false;
        }
        return valid;
    }

    // je pense que ça sert à rien car dans planifium on peut pas get un cours by name.
    //private boolean validateNomCours(int id){return false;}

    // Cette méthode permet de comparer des cours selon certains critères
    public List<List<String>> comparerCours(String[] cours, String[] criteresComparaison) {
    

        List<List<String>> resultat = new ArrayList<>();

        // Charger les cours
        List<Cours> coursTrouves = new ArrayList<>();

        for (String nomCours : cours) {
// est-ce qu'on fait la validation ici ou bien on le fait de manière interactive pendant que l'utilisateur saisit son input?
            if (!validateIdCours(nomCours)) {
                System.out.println("Cours non valide : " + nomCours);
                return null;
            }

            try {
                Optional<Cours> opt = this.coursRepository.getCourseById(nomCours);
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
    
    public Map<String,String> voirDetailsCours(String id, String session){
        Map<String,String> horaires = horairesCours(id, session);
        Map<String,String> details = new HashMap<>();

        if(!horaires.isEmpty()){
            Map<String,String> contenu = this.coursRepository.detailsCours(id);
            details.putAll(contenu);
            int count = horaires.size();
            for(String key : horaires.keySet()){
                details.put("Horaires pour la session ("+count+")", horaires.get(key));
                count --;
            }
        }
        return details;
    }

    public Map<String,String> horairesCours(String id, String session){
        Map<String, String> horaires = new HashMap<>();

        if(this.coursRepository.doesCourseExist(id)==true && validateSession(session)==true){
            List<String> horaire = this.coursRepository.getCoursHoraires(id,session);
            int count = 0;
            String key;
            for(String h : horaire){
                key = session + "_" + count;
                horaires.put(key, h);
                count += 1;
            }
        }
        return horaires;
    }

    // A faire
    public List<String> comparerCombinaisonCours(Cours[][] cours){return new ArrayList<>();}

    // A faire
    public List<Cours> getAllCours(){return new ArrayList<>();}

    public void setCoursRepository(CoursRepository coursRepository) {
        this.coursRepository = coursRepository;
    }
}
