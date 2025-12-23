package org.projet.controller;

import io.javalin.http.Context;

import org.projet.exception.HoraireException;
import org.projet.model.Avis;
import org.projet.model.Cours;
import org.projet.service.CoursService;

import java.util.*;

/**
 * Cette classe permet de gérer les requêtes des utilisateurs relatives à la manipulation de cours.
 * @author Andréa Noukoua
 */
public class CoursController {
    // CoursService est un Singleton, donc on récupère l'instance existante.
    private CoursService coursService =CoursService.getInstance();

    public List<Avis> getAvis(Cours cours){return new ArrayList<>();}

    // A faire
    public List<String> comparerCombinaisonCours(int[] idCours) {
        return new ArrayList<>();}



    public void checkEligibility(Context ctx){
        RequeteEligibilite req = ctx.bodyAsClass(RequeteEligibilite.class);
        String resultat = coursService.checkEligibility(req.idCours,req.listeCours);
        ctx.json(resultat);
    }





    public void rechercherCours(Context ctx){
        RequeteRecherche req = ctx.bodyAsClass(RequeteRecherche.class);
        Optional<List<Cours>> resultat = coursService.rechercherCours(req.param,req.valeur,req.includeSchedule,req.semester);
        if(resultat.isPresent()){
            ctx.status(200);
            ctx.json(resultat.get());
        }else{
            ctx.status(404);
            ctx.json("Cours pas trouvé. Veuillez reessayer. Pour rappel, les paramètres possibles sont id, name et description.");
        }

    }
    /**
     * Cette méthode permet de traiter la requête de l'utilisateur relative à la comparaison de cours.
     * @param ctx le contexte javalin qui contient la requête HTTP de l'utilisateur ainsi que notre réponse.
     */

    public void comparerCours(Context ctx) {
       /* cette ligne de code map le body de la requête avec un objet Java. Cela permet de
       récupérer la liste de cours et celle des critères.
       */
        RequeteComparaison req = ctx.bodyAsClass(RequeteComparaison.class);

        // coursService s'occupera de la logique métier.
        List<List<String>> resultat =
                coursService.comparerCours(req.cours, req.criteres,req.session);

        if (resultat == null) {
            ctx.status(400);
            ctx.json("La comparaison n'a pas pu être effectuée. Vérifiez le format des critères de comparaison et celui des ids de Cours. Pour rappel, les critères autorisés sont les suivants : id,name,description, credits, scheduledSemester, schedules, prerequisite_courses, equivalent_courses, concomitant_courses, udemWebsite, requirement_text, available_terms, available_periods]");
            return;
        }

        ctx.status(200);
        ctx.json(resultat);
    }

    /**
     * Cette méthode permet de comparer des ensembles de cours.
     * @param ctx
     */
    public void comparerCombinaisonCours(Context ctx){
     RequeteComparaisonCombinaison req = ctx.bodyAsClass(RequeteComparaisonCombinaison.class);
        List<List<String>> resultat = coursService.comparerCombinaisonCours(req.listeCours, req.session);
        if (resultat == null) {
            ctx.status(400);
            ctx.json("Requête invalide");
            return;
        }
        ctx.status(200);
        ctx.json(resultat);
    }
    /**
     * Cette classe permet de parser le json du body de la requête comparaison. La classe est interne donc
     * on peut déclarer les attributs publics.
     */
    public static class RequeteComparaison {
        public String[] cours;
        public String[] criteres;
        public String session;
    }

    /**
     * Cette classe permet de parser le json du body de la requête recherche. La classe est interne donc
     * on peut déclarer les attributs publics.
     */

    public static class RequeteRecherche{
        public String param;
        public String valeur;
        public String includeSchedule;
        public String semester;
    }
    /**
     * Cette classe permet de parser le json du body de la requête eligibilite. La classe est interne donc
     * on peut déclarer les attributs publics.
     */
    public static class RequeteEligibilite{
        public String idCours;
        public List<String> listeCours;
    }

    /**
     * Cette classe permet de parser le json du body de la requête comparaisonCombinaison. La classe est interne donc
     * on peut déclarer les attributs publics.
     */
    public static class RequeteComparaisonCombinaison{
        public List<List<String>> listeCours;
        public String session;
    }

   
    

    /**
     * Requête utilisée pour vérifier l’éligibilité d’un étudiant à un cours,
     * en tenant compte des cours complétés et du cycle d’études.
     */
    public static class RequeteEligibiliteNew{
        public String idCours;
        public List<String> listeCours;
        public Integer cycle;
    }

    /**
     * Cette méthode vérifie l’éligibilité d’un étudiant à un cours donné.
     * Le controller délègue la logique métier au {@code CoursService} et
     * retourne un message indiquant si l’étudiant est éligible ou non.
     *
     * @param ctx contexte HTTP Javalin contenant la requête JSON
     */
    public void checkEligibilityNew(Context ctx){
        RequeteEligibiliteNew req = ctx.bodyAsClass(RequeteEligibiliteNew.class);
        String resultat = coursService.checkEligibilityNew(req.idCours,req.listeCours, req.cycle);
        ctx.json(resultat);
    }

   
    
    /**
     * Requête utilisée pour générer les horaires possibles d’un ensemble de cours.
     */
    public static class RequeteHoraire {
        public List<String> idCours;
        public String session;
        public Boolean sections;
        public Map<String, Map<String, String>> choix; 
    }

    /**
     * Cette méthode génère toutes les combinaisons d’horaires possibles pour un ensemble de cours.
     * Si l’option {@code sections} est activée, seuls les horaires correspondant
     * aux sections choisies sont retournés.
     *
     * @param ctx contexte HTTP Javalin contenant la requête JSON
     */
    public void genererHoraire(Context ctx) {
        try {
            RequeteHoraire req = ctx.bodyAsClass(RequeteHoraire.class);

            Map<String, Map<String, Map<String, List<List<String>>>>> horaires =
                    coursService.genererEnsembleHoraire(req.idCours, req.session);

            if (Boolean.TRUE.equals(req.sections)) {
                ctx.json(coursService.appliquerChoix(horaires, req.choix));
            } else {
                ctx.json(horaires);
            }

        } catch (HoraireException e) {
            ctx.status(400);
            ctx.json(e.getMessage());
        } catch (Exception e) {
            ctx.status(500);
            ctx.json("Erreur interne du serveur.");
        }
    }   
}
