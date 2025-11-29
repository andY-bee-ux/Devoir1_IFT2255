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
    void testLoadLocalData_notNull() {
        List<Cours> courses = repo.getAllCoursesLocal();
        assertNotNull(courses, "La liste des cours ne devrait jamais être nulle.");
    }

    @Test
    void testLoadLocalData_notEmpty() {
        List<Cours> courses = repo.getAllCoursesLocal();
        assertFalse(courses.isEmpty(), "Des cours devraient être disponibles.");
    }

    @Test
    void testGetCourseByIdLocal_validId() {
        Optional<Cours> cours = repo.getCourseByIdLocal("IFT2255");
        assertTrue(cours.isPresent(), "Le cours IFT2255 devrait être disponible.");
        assertEquals("IFT2255", cours.get().getId());
    }

    @Test
    void testGetCourseByIdLocal_invalidId() {
        Optional<Cours> cours = repo.getCourseByIdLocal("N_EXISTE_PAS");
        assertTrue(cours.isEmpty(), "Une recherche avec un ID inconnu devrait être vide.");
    }

    @Test
    void testCoursFieldsMappedCorrectly() {
        Optional<Cours> opt = repo.getCourseByIdLocal("IFT2255");
        assertTrue(opt.isPresent());

        Cours c = opt.get();

        assertNotNull(c.getId(), "L'ID doit être défini.");
        assertNotNull(c.getName(), "Le nom doit être défini.");
        assertNotNull(c.getDescription(), "La description doit être définie.");
        assertNotNull(c.getCredits(), "Les crédits doivent être définis.");
    }

    // Les méthodes utilisant Planifium ne sont pas testées ici, on ne controlle pas Planifium
    @Test
    void testGetCourseById_apiIgnored() {
        assertTrue(true);
    }

    @Test
    void testGetAllCoursesId_apiIgnored() {
        assertTrue(true);
    }
}
