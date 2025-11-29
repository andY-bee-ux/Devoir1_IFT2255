package org.example.repository;

import org.example.model.Cours;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CoursRepositoryTest {

    private static CoursRepository repo;

    @BeforeAll
    static void setup() {
        repo = CoursRepository.getInstance();
        repo.loadLocalJson();  
    }

    @Test
    void testLoadLocalJson_notNull() {
        List<Cours> courses = repo.getAllCoursesLocal();
        assertNotNull(courses, "La liste des cours ne devrait pas être nulle");
    }

    @Test
    void testLoadLocalJson_notEmpty() {
        List<Cours> courses = repo.getAllCoursesLocal();
        assertFalse(courses.isEmpty(), "Le fichier JSON devrait contenir au moins un cours");
    }

    @Test
    void testGetCourseByIdLocal_validId() {
        Optional<Cours> cours = repo.getCourseByIdLocal("IFT2255");
        assertTrue(cours.isPresent(), "IFT2255 devrait exister dans le JSON local.");
        assertEquals("IFT2255", cours.get().getId());
    }

    @Test
    void testGetCourseByIdLocal_invalidId() {
        Optional<Cours> cours = repo.getCourseByIdLocal("KJFDSKFD");
        assertTrue(cours.isEmpty(), "Un ID inexistant devrait retourner Optional.empty().");
    }

    @Test
    void testCoursFieldsMappedCorrectly() {
        Optional<Cours> opt = repo.getCourseByIdLocal("IFT2255");
        assertTrue(opt.isPresent());

        Cours c = opt.get();

        assertNotNull(c.getId());
        assertNotNull(c.getName());
        assertNotNull(c.getDescription());
        assertNotNull(c.getCredits());
    }

    

    @Test
    void testGetCourseById_apiIgnored() {
        // On skip car ça dépend de Planifium, pas de nous...
    }

    @Test
    void testGetAllCoursesId_apiIgnored() {
        // On skip car ça dépend de Planifium, pas de nous...
    }
}
