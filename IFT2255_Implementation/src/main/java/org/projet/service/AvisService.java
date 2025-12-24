package org.projet.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.projet.model.Avis;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AvisService {

    private static AvisService instance;
    private final ObjectMapper mapper = new ObjectMapper();
    private List<Avis> avisStockes;

    /**
     * Cette méthode permet de charger le fichier JSON des avis ( Avis.json) lors de l'utilisation de l'instance
     * de AvisService.
     */
    private AvisService() {

        avisStockes = new ArrayList<>();

        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("Avis.json");

        if (is == null) {
            avisStockes = new ArrayList<>();
        } else {
            try {
                avisStockes = mapper.readValue(
                        is,
                        new TypeReference<List<Avis>>() {}
                );
            } catch (IOException e) {
                System.out.println("Erreur lecture fichier avis.json, initialisation vide");
                e.printStackTrace();
                avisStockes = new ArrayList<>();
            }
        }
    }

    /**
     * Cette méthode permet de récupérer l'instance de AvisService dans le cadre du patron de création Singleton.
     * @return l'instance de AvisService.
     */
    public static AvisService getInstance() {
        if (instance == null) {
            instance = new AvisService();
        }
        return instance;
    }

    /**
     * Cette méthode permet de valider l'Avis reçu du bot. A noter que ce dernier fait déjà une vérification
     * minimale à savoir si le sigle est du bon format, le commentaire ne contient pas d'insultes,
     * et les notes sont des entiers entre 0 et 5.
     * @param sigle  id du cours.
     * @param nomProf nom du professeur ( optionnel)
     * @param noteDifficulte  estimation du niveau de difficulté du cours
     * @param noteQualite    estimation de la qualité du cours
     * @param commentaire   commentaire subjectif.
     */
    public void validateAvis(String sigle, String nomProf,
                             int noteDifficulte, int noteQualite,
                             String commentaire) {

        if (!CoursService.getInstance().validateIdCours(sigle)) {
            throw new IllegalArgumentException("Cours inexistant");
        }

        if (noteDifficulte < 1 || noteDifficulte > 5) {
            throw new IllegalArgumentException("Note difficulté invalide");
        }

        if (noteQualite < 1 || noteQualite > 5) {
            throw new IllegalArgumentException("Note qualité invalide");
        }

        if (commentaire == null || commentaire.trim().isEmpty()) {
            throw new IllegalArgumentException("Commentaire vide");
        }
    }

    /**
     * Cette méthode permet d'enregistrer l'avis localement.
     * @param sigle sigle du cours
     * @param prof nom du prof
     * @param noteDifficulte note de la difficulté
     * @param noteQualite note de la qualité.
     * @param commentaire  commentaire subjectif.
     */
    public void enregistrerAvis(String sigle, String prof,
                                int noteDifficulte, int noteQualite,
                                String commentaire) {
        try {
            this.validateAvis(sigle, prof, noteDifficulte, noteQualite, commentaire);
            Avis avis = new Avis(
                    sigle,
                    prof,
                    noteQualite,
                    noteDifficulte,
                    commentaire,
                    true
            );

            avisStockes.add(avis);
        } catch (Exception e) {
            System.out.println("Erreur enregistrer avis");
        }
    }

    /**
     * Cette méthode permet de récupérer la liste des avis associés à un cours.
     * @param sigle  sigle du cours
     * @return  retourne la liste d'avis associée au cours donné.
     */
    public List<Avis> getAvisParCours(String sigle) {

        if (!CoursService.getInstance().validateIdCours(sigle)) {
            throw new IllegalArgumentException("Cours inexistant");
        }

        List<Avis> avisCours = new ArrayList<>();
        for (Avis avis : avisStockes) {
            if (avis.getSigleCours().equals(sigle)) {
                avisCours.add(avis);
            }
        }
        return avisCours;
    }
}
