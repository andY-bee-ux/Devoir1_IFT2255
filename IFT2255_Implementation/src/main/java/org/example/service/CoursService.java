package org.example.service;

import org.example.model.Cours;
import org.example.repository.CoursRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CoursService {

    private final CoursRepository repo = CoursRepository.getInstance();

    private static CoursService instance;

    private CoursService() {}

    public static CoursService getInstance() {
        if (instance == null) {
            instance = new CoursService();
        }
        return instance;
    }

    /**
     * Recherche principale :
     * 1) fichier local ( courses.json) 
     * 2) appel API planifium si aucun résultat
     */
    public List<Cours> search(String query) {

        String q = query.toLowerCase();

        // recherche locale
        List<Cours> local = searchLocal(q);

        if (!local.isEmpty()) {
            return local;
        }

        // appel à planifium
        System.out.println("\nAucun résultat trouvé localement. Recherche en direct sur Planifium...");
        System.out.println("Ceci peut prendre plusieurs minutes...");

        List<Cours> live = searchLive(q);

        if (live.isEmpty()) {
            System.out.println("Aucun cours trouvé via Planifium non plus.");
        }

        return live;
    }


    List<Cours> searchLocal(String q) {
        List<Cours> all = repo.getAllCoursesLocal();

        return all.stream()
                .filter(c ->
                        c.getId().toLowerCase().contains(q)
                        || c.getName().toLowerCase().contains(q)
                        || c.getDescription().toLowerCase().contains(q)
                )
                .toList();
    }

    
    private List<Cours> searchLive(String q) {

        List<Cours> results = new ArrayList<>();

        try {
            List<String> ids = repo.getAllCoursesId().orElse(List.of());

            for (String id : ids) {
                Optional<Cours> opt = repo.getCourseById(id);

                if (opt.isPresent()) {
                    Cours c = opt.get();

                    if (c.getId().toLowerCase().contains(q)
                            || c.getName().toLowerCase().contains(q)
                            || c.getDescription().toLowerCase().contains(q)) {
                        results.add(c);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche API : " + e.getMessage());
        }

        return results;
    }

    public List<Cours> getAllCoursLocal() {
        return repo.getAllCoursesLocal();
    }
}
