package org.projet;

import io.javalin.Javalin;
import org.projet.controller.CoursController;

public class Main {
    public static void main(String[] args) {
        CoursController coursController = new CoursController();
        var app = Javalin.create().start(7070);
        // post car on veut pouvoir inclure du body à la requête.
        app.post("/cours/comparer", coursController::comparerCours);
        app.post("/cours/rechercher", coursController::rechercherCours);
        app.post("/cours/eligibilite",coursController::checkEligibility);
        app.post("/cours/comparer/combinaison", coursController::comparerCombinaisonCours);
        app.post("/avis", ctx -> {
            String body = ctx.body();
            System.out.println("Avis reçu: " + body);
            ctx.result("Avis reçu avec succès");
            
        });
        app.post("/cours/eligibilitenew", coursController::checkEligibilityNew);
        app.post("/horaire", coursController::genererHoraire);
    }
}
