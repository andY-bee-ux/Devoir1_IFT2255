package org.example.service;

import org.example.model.Cours;
import org.example.repository.CoursRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CoursServiceTest {

    private static CoursService service;
    private static CoursRepository repo;

    @BeforeAll
    static void setup() {
        service = CoursService.getInstance();
        repo = CoursRepository.getInstance();

        
        repo.loadLocalJson();
      
    }

    @Test
    void testSearch_local_notEmpty() {
        List<Cours> results = service.search("IFT");
        assertFalse(results.isEmpty(), "La recherche 'IFT' devrait retourner des résultats.");
    }

    @Test
    void testSearchByExactId() {
        List<Cours> results = service.search("IFT2255");
        assertEquals(1, results.size(), "IFT2255 devrait retourner exactement un cours.");
        assertEquals("IFT2255", results.get(0).getId());
    }

    @Test
    void testSearchByName() {
        List<Cours> results = service.search("génie logiciel");
        assertFalse(results.isEmpty());
        assertEquals("IFT2255", results.get(0).getId());
    }

    @Test
    void testSearchByDescription() {
        List<Cours> results = service.search("développement");
        assertFalse(results.isEmpty(), "Le mot 'développement' existe dans plusieurs descriptions.");
    }

    @Test
    void testSearchCaseInsensitive() {
        List<Cours> results1 = service.search("ift2255");
        List<Cours> results2 = service.search("IFT2255");

        assertEquals(results1.size(), results2.size(),
                "La recherche devrait être insensible à la casse.");
    }

    @Test
    void testSearch_returnsMultiple() {
        List<Cours> results = service.search("IFT");
        assertTrue(results.size() > 1, "La recherche 'IFT' devrait retourner plusieurs cours.");
    }

    @Test
    void testSearch_noResults() {
        List<Cours> results = service.searchLocal("KJHFGDSHF");
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetAllCoursLocal_notEmpty() {
        List<Cours> list = service.getAllCoursLocal();
        assertFalse(list.isEmpty(), "Le JSON local doit contenir des cours.");
    }

    @Test
    void testSearchCoursesLive_ignored() {
        // On skip car ça dépend de Planifium, pas de nous...
    }
}
