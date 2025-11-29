package org.example.controller;

import org.example.model.Cours;
import org.example.service.CoursService;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class CoursController {

    private final CoursService service = CoursService.getInstance();

    public CoursController() {}

    public void handleSearch(String query) {

        System.out.println("Recherche de cours avec '" + query + "'...");

        List<Cours> results = service.search(query);

        if (results.isEmpty()) {
            System.out.println("Aucun cours trouvé.");
            System.out.println("---------------------------------------------");
            return;
        }

        for (Cours c : results) {

            String prereq =
                c.getPrerequisite_courses() == null ? "Aucun" : Arrays.toString(c.getPrerequisite_courses());

            String coreq =
                c.getConcomitant_courses() == null ? "Aucun" : Arrays.toString(c.getConcomitant_courses());

            String equivalences =
                c.getEquivalent_courses() == null ? "Aucune" : Arrays.toString(c.getEquivalent_courses());

            String contraintes =
                (c.getRequirement_text() == null || c.getRequirement_text().isBlank())
                ? "Aucune information"
                : c.getRequirement_text();


                String dispo;
            if (c.getAvailable_terms() == null) {
                dispo = "Non spécifié";
            } else {
                List<String> terms = new ArrayList<>();

                if (Boolean.TRUE.equals(c.getAvailable_terms().get("autumn"))) terms.add("Automne");
                if (Boolean.TRUE.equals(c.getAvailable_terms().get("winter"))) terms.add("Hiver");
                if (Boolean.TRUE.equals(c.getAvailable_terms().get("summer"))) terms.add("Été");

                dispo = terms.isEmpty() ? "Aucune" : String.join(", ", terms);
            }

            String periodes;
            if (c.getAvailable_periods() == null) {
                periodes = "Non spécifié";
            } else {
                List<String> periods = new ArrayList<>();

                if (Boolean.TRUE.equals(c.getAvailable_periods().get("daytime"))) periods.add("Jour");
                if (Boolean.TRUE.equals(c.getAvailable_periods().get("evening"))) periods.add("Soir");

                periodes = periods.isEmpty() ? "Aucune" : String.join(", ", periods);
            }

            // ----- Affichage -----
            System.out.println("\n=== " + c.getId() + " — " + c.getName() + " ===");
            System.out.println("Description : " + c.getDescription());
            System.out.println("Crédits : " + c.getCredits());
            System.out.println("Sessions offertes : " + dispo);
            System.out.println("Prérequis : " + prereq);
            System.out.println("Co-requis : " + coreq);
            System.out.println("Équivalences : " + equivalences);
            System.out.println("Contraintes/exigences : " + contraintes);
            System.out.println("Périodes : " + periodes);
            System.out.println("---------------------------------------------");
        }
    }
}
