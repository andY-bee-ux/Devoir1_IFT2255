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
        repo = CoursRepository.getInstance();
        service = CoursService.getInstance();

        repo.loadLocalJson();
    }

    @Test
    void testSearch_notEmpty() {
        List<Cours> results = service.search("IFT");
        assertFalse(results.isEmpty(), "Une recherche sur 'IFT' devrait retourner des résultats.");
        assertTrue(service.lastSearchUsedLocal(), "Cette recherche devrait être résolue localement.");
    }

    @Test
    void testSearchByExactId() {
        List<Cours> results = service.search("IFT2255");
        assertEquals(1, results.size(), "IFT2255 devrait retourner exactement un cours.");
        assertEquals("IFT2255", results.get(0).getId());
        assertTrue(service.lastSearchUsedLocal());
    }

    @Test
    void testSearchByName() {
        List<Cours> results = service.search("génie logiciel");
        assertFalse(results.isEmpty(), "Le nom du cours devrait être détecté.");
        assertTrue(service.lastSearchUsedLocal());
    }

    @Test
    void testSearchByDescription() {
        List<Cours> results = service.search("développement");
        assertFalse(results.isEmpty(), "Le terme 'développement' devrait apparaître dans les descriptions.");
        assertTrue(service.lastSearchUsedLocal());
    }

    @Test
    void testSearchCaseInsensitive() {
        List<Cours> r1 = service.search("ift2255");
        List<Cours> r2 = service.search("IFT2255");

        assertEquals(r1.size(), r2.size(),
                "La recherche doit être insensible à la casse.");
        assertTrue(service.lastSearchUsedLocal());
    }

    @Test
    void testSearch_returnsMultiple() {
        List<Cours> results = service.search("IFT");
        assertTrue(results.size() > 1, "La recherche 'IFT' devrait retourner plusieurs cours.");
        assertTrue(service.lastSearchUsedLocal());
    }

    @Test
    void testSearch_noLocalResults() {
        List<Cours> results = service.searchLocal("SKDJFHDAS");
        assertTrue(results.isEmpty(), "Une recherche sur un ID inexistant devrait renvoyer une liste vide.");
    }

    @Test
    void testGetAllCoursLocal_notEmpty() {
        List<Cours> list = service.getAllCoursLocal();
        assertFalse(list.isEmpty(), "La liste des cours devrait être disponible.");
    }

    @Test
    void testSearchLive_ignored() {
        // On ignore ce test car il dépend de planifium 
        assertTrue(true);
    }
}
