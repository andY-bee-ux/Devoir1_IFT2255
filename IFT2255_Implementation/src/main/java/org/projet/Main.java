package org.projet;

import io.javalin.Javalin;
import org.projet.controller.CoursController;

public class Main {
    public static void main(String[] args) {
        CoursController coursController = new CoursController();
        var app = Javalin.create().start(7000);
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
        app.get("/cours-programme/{id}",coursController::getCoursesForAProgram);
        app.get("/programme/courseBySemester/{id}/{session}",coursController::getCourseBySemester);
        app.get("/cours/horaires/{id}/{session}",coursController::getCourseSchedule);
    }
}
