package org.projet.controller;

import io.javalin.http.Context;

import org.projet.exception.HoraireException;
import org.projet.model.Avis;
import org.projet.model.Cours;
import org.projet.model.Resultats;
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

   


    public void checkEligibility(Context ctx){
        RequeteEligibilite req = ctx.bodyAsClass(RequeteEligibilite.class);
        String resultat = coursService.checkEligibility(req.idCours,req.listeCours);
        ctx.json(resultat);
    }





    public void rechercherCours(Context ctx){
        try{
            RequeteRecherche req = ctx.bodyAsClass(RequeteRecherche.class);
            Optional<List<Cours>> resultat = coursService.rechercherCours(req.param,req.valeur,req.includeSchedule,req.semester);
            if(resultat.isPresent()){
                ctx.status(200);
                ctx.json(resultat.get());
            }else{
                ctx.status(404);
                ctx.json("Cours pas trouvé. Veuillez reessayer. Pour rappel, les paramètres possibles sont id, name et description.");
            }
        }catch (Exception e){
            ctx.status(404);
            ctx.json("Contexte invalide");
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

        try {
            RequeteComparaison req = ctx.bodyAsClass(RequeteComparaison.class);
            List<List<String>> resultat =
                    coursService.comparerCours(req.cours, req.criteres, req.session);

            if (resultat == null) {
                ctx.status(400);
                ctx.json("Requête invalide");
                return;
            }

            ctx.status(200);
            ctx.json(resultat);

        } catch (RuntimeException e) {
            ctx.status(400);
            ctx.json("Requête invalide");
        } catch (Exception e) {
            ctx.status(404);
            ctx.json("Contexte invalide");
        }
    }


    /**
     * Cette méthode permet d’analyser la difficulté globale de plusieurs combinaisons de cours.
     * La difficulté est estimée à partir des résultats académiques agrégés
     * (moyenne des scores des cours composant chaque combinaison).
     *
     * @param ctx le contexte javalin contenant la requête HTTP de l'utilisateur.
     */
    public void difficulteCombinaisonCours(Context ctx) {
        RequeteComparaisonCombinaison req =
                ctx.bodyAsClass(RequeteComparaisonCombinaison.class);

        try {
            List<String> resultats =
                    coursService.difficulteCombinaisonCours(req.listeCours);

            ctx.status(200);
            ctx.json(resultats);

        } catch (RuntimeException e) {
            ctx.status(400);
            ctx.json("Requête invalide");
        } catch (Exception e) {
             ctx.status(404);
             ctx.json("Contexte invalide");
         }
    }

    /**
     * Cette méthode permet d’analyser la popularité globale de plusieurs combinaisons de cours.
     * La popularité est estimée à partir du nombre total de participants
     * des cours composant chaque combinaison.
     *
     * @param ctx le contexte javalin contenant la requête HTTP de l'utilisateur.
     */
    public void populariteCombinaisonCours(Context ctx) {
        RequeteComparaisonCombinaison req =
                ctx.bodyAsClass(RequeteComparaisonCombinaison.class);

        try {
            List<String> resultats =
                    coursService.populariteCombinaisonCours(req.listeCours);

            ctx.status(200);
            ctx.json(resultats);

        } catch (RuntimeException e) {
            ctx.status(200);
            ctx.json(new ArrayList<>());
        }
    }

    /**
     * Cette méthode permet de comparer plusieurs combinaisons de cours
     * selon leurs statistiques agrégées.
     * La comparaison porte à la fois sur la difficulté globale
     * et la popularité globale de chaque combinaison.
     *
     * @param ctx le contexte javalin qui contient la requête HTTP de l'utilisateur
     *            ainsi que la réponse retournée.
     */
    public void comparerCombinaisonStats(Context ctx) {
        RequeteComparaisonCombinaison req =
                ctx.bodyAsClass(RequeteComparaisonCombinaison.class);

        try {
            List<Map<String, String>> resultats =
                    coursService.comparerCombinaisonStats(req.listeCours);

            ctx.status(200);
            ctx.json(resultats);

        } catch (RuntimeException e) {
            ctx.status(200);
            ctx.json(new ArrayList<>());
        }
    }

    /**
     * Cette méthode permet de comparer plusieurs combinaisons de cours
     * uniquement à partir des informations issues du catalogue.
     * Les critères considérés incluent notamment les crédits,
     * les périodes communes, les sessions communes et les prérequis.
     *
     * @param ctx le contexte javalin qui contient la requête HTTP de l'utilisateur
     *            ainsi que la réponse retournée.
     */
    public void comparerCombinaisonCatalogue(Context ctx) {
        RequeteComparaisonCombinaison req =
                ctx.bodyAsClass(RequeteComparaisonCombinaison.class);

        try {
            List<List<String>> resultat =
                    coursService.comparerCombinaisonCatalogue(req.listeCours);

            ctx.status(200);
            ctx.json(resultat);

        } catch (RuntimeException e) {
            ctx.status(200);
            ctx.json(new ArrayList<>());
        }
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
        try {
            RequeteEligibiliteNew req = ctx.bodyAsClass(RequeteEligibiliteNew.class);
            String resultat = coursService.checkEligibilityNew(req.idCours,req.listeCours, req.cycle);
            ctx.json(resultat);
        }catch (Exception e){
            ctx.status(404);
            ctx.json("Contexte invalide");
        }
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

            Object reponse;

            if (Boolean.TRUE.equals(req.sections)) {
                reponse = coursService.appliquerChoix(horaires, req.choix);
            } else {
                reponse = horaires;
            }

            ctx.status(200);
            ctx.json(reponse);

        } catch (Exception e) {
            ctx.status(200);
            ctx.json(new HashMap<>());
        }
    }


    /**
     * Cette methode permet d'obtenir les cours offerts dans un programme donne.
     * @param ctx ID du programme.
     **/
    public void getCoursesForAProgram(Context ctx){
        String id = ctx.pathParam("id");
        List<String> details = coursService.getCoursesForAProgram(id);

        if (details.isEmpty()) {
            ctx.status(404).json(Map.of("error", "Les paramètres fournis sont invalides ou le programme n'existe pas."));
            return;
        }

        ctx.status(200).json(details);
    }

    /**
     * Cette methode permet d'obtenir la liste des cours disponible pour un trimestre donnee dans un programme.
     * @param ctx ID du programme dans lequel il faut effectuer la recherche et
     *            du trimestre pour laquelle on effectue la recherche.
     **/
    public void getCourseBySemester(Context ctx){
        String id = ctx.pathParam("id");
        String session = ctx.pathParam("session");

        List<String> details = coursService.getCourseBySemester(session,id);

        if (details.isEmpty()) {
            ctx.status(404).json(Map.of("error","Les paramètres fournis sont invalides ou le programme n'existe pas ou le cours n'existe pas."));
            return;
        }

        ctx.status(200).json(details);
    }

    /**
     * Cette methode permet d'obtenir l'horaire d'un cours pour un trimestre donné.
     * @param ctx ID du cours
     * et le trimestre pour lequel on désire obtenir l'horaire.
     **/
    public void getCourseSchedule(Context ctx){
        String courseID  = ctx.pathParam("id"); //ID du cours.
        String session = ctx.pathParam("session");  //Trimestre pour lequel on effectue la recherche.

        Map<String,Map<String,Object>> details = coursService.getCourseScheduleMap(courseID,session);

        if (details.isEmpty()) {
            ctx.status(404).json(Map.of("error","Les paramètres fournis sont invalides ou le programme n'existe pas ou le cours n'existe pas."));
            return;
        }
        ctx.status(200).json(details);
    }

    /**
* Analyse la difficulté du cours envoyé via JSON.
 * @param ctx Contexte Javalin.
 */
public void difficulteCours(Context ctx) {
    try {
        RequeteUnCours req = ctx.bodyAsClass(RequeteUnCours.class);
        Resultats res = coursService.getResultats(req.sigle);
        String difficulte = coursService.difficulteCours(res);
        ctx.json(difficulte);
    }catch (Exception e) {
        ctx.status(404);
        ctx.json("Contexte invalide");
    }
    }  


/**
* Analyse la popularité du cours envoyé via JSON.
* @param ctx Contexte Javalin.
 */  
public void populariteCours(Context ctx) {
    try{
        RequeteUnCours req = ctx.bodyAsClass(RequeteUnCours.class);
        Resultats res = coursService.getResultats(req.sigle);
        String popularite = coursService.populariteCours(res);
        ctx.json(popularite);
    }catch (Exception e) {
        ctx.status(404);
        ctx.json("Contexte invalide");
    }
    }  

    /**
     * Cette classe permet de parser le json du body de la requête difficulte ou popularite. La classe est interne donc
     * on peut déclarer les attributs publics.
     */
    public static class RequeteUnCours{
        public String sigle;
    }    

       
/**
 * Cette méthode permet de comparer les statistiques de deux cours.
 * @param ctx le contexte javalin qui contient la requête HTTP de l'utilisateur ainsi que notre réponse.
 */ 
public void comparerDeuxCours(Context ctx) {
    try {
        RequeteDeuxCours req = ctx.bodyAsClass(RequeteDeuxCours.class);

        Resultats res1 = coursService.getResultats(req.sigle1);
        Resultats res2 = coursService.getResultats(req.sigle2);


        Map<String, String> reponses = new HashMap<>();
        reponses.put("popularite", coursService.comparerPopularite(res1, res2));
        reponses.put("difficulte", coursService.comparerDifficulte(res1, res2));

        ctx.json(reponses);
    }catch (Exception e) {
        ctx.status(404);
        ctx.json("Contexte invalide");
    }
    }  


/**
 * Cette classe permet de parser le json du body de la requête comparaisonStats. La classe est interne donc
 * on peut déclarer les attributs publics.
 */    
public static class RequeteDeuxCours{
        public String sigle1;
        public String sigle2;
    }

/**
 * Cette classe permet de parser le json du body de la requête stats. La classe est interne donc
 * on peut déclarer les attributs publics.
 */
public static class RequeteStats {
        public String sigle;
}    



public static class RequeteResultats {
        public String sigle;
}

/**
 * Récupère et renvoie les résultats d'un cours au format JSON.
 * @param ctx Le contexte de la requête HTTP.
 */
public void voirResultats(Context ctx) {
    try {
        RequeteResultats req = ctx.bodyAsClass(RequeteResultats.class);
        Resultats res = coursService.getResultats(req.sigle);
        String message = res.voirResultats();
        ctx.json(message);
    }catch (Exception e) {
        ctx.status(404);
        ctx.json("Contexte invalide");
    }
    }
}
