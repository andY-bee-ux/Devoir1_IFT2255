package org.example.config;

import io.javalin.Javalin;
import org.example.controller.CoursController;

public class Routes {
    // Ce code provient du repertoire GitHub : ift2255-template-javalin fournit pour ce projet.

    /**
     * Cette methode permet d'initialiser les registres pour nos entites, ses registres contiennent les methodes GET,
     * POST, PUT et DELETE.
     * @param app Il s'agit d'une instanciation du serveur Javalin.
     **/
    public static void registre(Javalin app){
        registreUserRoutes(app);
        resgistreCoursRoutes(app);
        registreAvisRoutes(app);
        registreResultatRoutes(app);
    }

    /**
     *  Cette methode contient les methodes  GET, POST, PUT et DELETE pour l'entité USER.
     * @param app Il s'agit d'une instanciation du serveur Javalin.
     **/
    private static void registreUserRoutes(Javalin app){}

    /**
     *  Cette methode contient les methodes  GET, POST, PUT et DELETE pour l'entité COURS.
     * @param app Il s'agit d'une instanciation du serveur Javalin.
     **/
    private static void resgistreCoursRoutes(Javalin app){
        CoursController coursController = new CoursController();

        app.post("/cours/comparer", coursController::comparerCours);
        app.post("/cours/rechercher", coursController::rechercherCours);
        app.post("/cours/eligibilite",coursController::checkEligibility);
        app.post("/cours/comparer/combinaison", coursController::comparerCombinaisonCours);
        app.post("/avis", ctx -> {
            String body = ctx.body();
            System.out.println("Avis reçu: " + body);
            ctx.result("Avis reçu avec succès");
            
        });
    }

    /**
     *  Cette methode contient les methodes  GET, POST, PUT et DELETE pour l'entité AVIS.
     * @param app Il s'agit d'une instanciation du serveur Javalin.
     **/
    private static void registreAvisRoutes(Javalin app){}

    /**
     *  Cette methode contient les methodes  GET, POST, PUT et DELETE pour l'entité RESULTATS.
     * @param app Il s'agit d'une instanciation du serveur Javalin.
     **/
    private static void registreResultatRoutes(Javalin app){}
}