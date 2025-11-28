package org.example.controller;

import io.javalin.http.Context;
import org.example.model.Avis;
import org.example.model.Cours;
import org.example.service.CoursService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Cette classe permet de gérer les requêtes des utilisateurs relatives à la manipulation de cours.
 * @author Andréa Noukoua
 */
public class CoursController {
    // CoursService est un Singleton, donc on récupère l'instance existante.
    private CoursService coursService =CoursService.getInstance();
//    private Scanner scanner = new Scanner(System.in);
//    private static final Map<String, String> CRITERE_MAP = Map.ofEntries(
//            Map.entry("id", "id"),
//            Map.entry("nom", "name"),
//            Map.entry("description", "description"),
//            Map.entry("semestre", "scheduledSemester"),
//            Map.entry("horaires", "schedules"),
//            Map.entry("cours prerequis", "prerequisite_courses"),
//            Map.entry("cours equivalents", "equivalent_courses"),
//            Map.entry("cours concomitants", "concomitant_courses"),
//            Map.entry("site udem", "udemWebsite"),
//            Map.entry("credits", "credits"),
//            Map.entry("exigences", "requirement_text"),
//            Map.entry("terms", "available_terms"),
//            Map.entry("periods", "available_periods")
//    );

    public List<Avis> getAvis(Cours cours){return new ArrayList<>();}

    // A faire
    public List<String> comparerCombinaisonCours(int[] idCours) {
        return new ArrayList<>();}

    /**
     * Route: POST /cours/compare
     * Description : Compare deux cours envoyés dans le body JSON.
     * On utilise POST car on ENVOIE un body.
     *
     * Exemple de body :
     * {
     *   "cours1": "IFT1025",
     *   "cours2": "IFT2255"
     * }
     */

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
                coursService.comparerCours(req.cours, req.criteres);

        if (resultat == null) {
            ctx.status(400);
            ctx.json("La comparaison n'a pas pu être effectuée. Vérifiez le format des critères de comparaison et celui des ids de Cours. Pour rappel, les critères autorisés sont les suivants : [id,name,description, credits, scheduledSemester, schedules, prerequisite_courses, equivalent_courses, concomitant_courses, udemWebsite, requirement_text, available_terms, available_periods]");
            return;
        }

        ctx.status(200);
        ctx.json(resultat);
    }

    /**
     * Cette classe permet de parser le json du body de la requête. La classe est interne donc
     * on peut déclarer les attributs publics.
     */
    public static class RequeteComparaison {
        public String[] cours;
        public String[] criteres;
    }

}
