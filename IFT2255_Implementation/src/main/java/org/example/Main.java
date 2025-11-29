package org.example;

import io.javalin.Javalin;
import org.example.controller.CoursController;
import org.example.service.CoursService;

public class Main {
    public static void main(String[] args) {

        CoursService service = CoursService.getInstance();
        CoursController controller = new CoursController();

        Javalin app = Javalin.create().start(7070);

        app.get("/cours/search", ctx -> {
            controller.handleSearchREST(ctx);

            if (!service.lastSearchUsedLocal()) {
                ctx.header("Source", "planifium");
            } else {
                ctx.header("Source", "local-json");
            }
        });

        System.out.println("PickCourse démarré sur http://localhost:7070");
    }
}
