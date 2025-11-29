package org.example.controller;

import io.javalin.http.Context;
import org.example.model.Cours;
import org.example.service.CoursService;

import java.util.List;

public class CoursController {

    private final CoursService service = CoursService.getInstance();

    public CoursController() {}

    public void handleSearchREST(Context ctx) {
        String query = ctx.queryParam("query");

        if (query == null || query.isBlank()) {
            ctx.status(400).json("Le paramètre 'query' est obligatoire.");
            return;
        }

        try {
            List<Cours> results = service.search(query);

            if (results.isEmpty()) {
                ctx.status(404).json("Aucun cours trouvé pour : " + query);
                return;
            }

            ctx.status(200).json(results);

        } catch (RuntimeException e) {
            ctx.status(404).json("Aucun cours trouvé pour : " + query);
        }
    }
}
