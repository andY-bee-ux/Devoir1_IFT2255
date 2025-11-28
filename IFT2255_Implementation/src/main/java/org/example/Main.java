package org.example;

import io.javalin.Javalin;
import org.example.controller.CoursController;

public class Main {
    public static void main(String[] args) {
        CoursController coursController = new CoursController();
        var app = Javalin.create().start(7000);
        // post car on veut pouvoir inclure du body à la requête.
        app.post("/cours/comparer", coursController::comparerCours);
    }
}
