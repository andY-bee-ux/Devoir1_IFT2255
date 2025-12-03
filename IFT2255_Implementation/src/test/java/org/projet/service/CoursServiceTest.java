package org.projet.service;

import org.projet.model.Cours;
import org.projet.repository.CoursRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Cette classe permet de générer des tests unitaires
 */

class CoursServiceTest {

    private CoursRepository mockRepo;
    private CoursService service;

    @BeforeEach
    void setUp() throws Exception {
        mockRepo = mock(CoursRepository.class);

        Field instanceField = CoursService.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        service = CoursService.getInstance();

        Field repoField = CoursService.class.getDeclaredField("coursRepository");
        repoField.setAccessible(true);
        repoField.set(service, mockRepo);
    }

    @Test
    @DisplayName("validateIdCours() retourne true pour un id valide")
    void testValidateIdCoursValide() throws Exception {
        when(mockRepo.getAllCoursesId())
                .thenReturn(Optional.of(List.of("IFT1025", "IFT2255")));

        Method m = CoursService.class.getDeclaredMethod("validateIdCours", String.class);
        m.setAccessible(true);

        boolean res = (boolean) m.invoke(service, "IFT1025");

        assertTrue(res);
    }

    @Test
    @DisplayName("validateIdCours() retourne false pour un id invalide")
    void testValidateIdCoursInvalide() throws Exception {
        when(mockRepo.getAllCoursesId())
                .thenReturn(Optional.of(List.of("IFT1025", "IFT2255")));

        Method m = CoursService.class.getDeclaredMethod("validateIdCours", String.class);
        m.setAccessible(true);

        boolean res = (boolean) m.invoke(service, "IAMTIDJANI02");

        assertFalse(res);
    }

    @Test
    @DisplayName("checkEligibility() retourne un message d'erreur si l'id du cours est invalide")
    void testCheckEligibilityIdCoursInvalide() throws Exception {
        when(mockRepo.getAllCoursesId())
                .thenReturn(Optional.of(List.of("IFT1025", "IFT2255")));

        String r = service.checkEligibility("TJ5035", List.of("IFT1025"));

        assertEquals("L'id du cours est invalide", r);
        verify(mockRepo, never()).getCourseEligibility(anyString(), anyList());
    }

    @Test
    @DisplayName("checkEligibility() retourne un message si des cours complétés sont invalides")
    void testCheckEligibilityCoursCompletesInvalides() throws Exception {

        when(mockRepo.getAllCoursesId())
                .thenReturn(Optional.of(List.of("IFT2255")));

        String r = service.checkEligibility("IFT2255", List.of("TJOFF3334"));

        assertEquals("Il y a des cours complétés invalides", r);
        verify(mockRepo, never()).getCourseEligibility(anyString(), anyList());
    }

    @Test
    @DisplayName("checkEligibility() éligible")
    void testCheckEligibilityEligible() throws Exception {
        when(mockRepo.getAllCoursesId())
                .thenReturn(Optional.of(List.of("IFT1025", "IFT2255")));

        when(mockRepo.getCourseEligibility("IFT2255", List.of("IFT1025")))
                .thenReturn("""
                    {
                      "eligible": true,
                      "missing_prerequisites": []
                    }
                """);

        String r = service.checkEligibility("IFT2255", List.of("IFT1025"));

        assertEquals("Vous êtes éligible à ce cours!", r);
    }

    @Test
    @DisplayName("checkEligibility() non éligible")
    void testCheckEligibilityNonEligible() throws Exception {
        when(mockRepo.getAllCoursesId())
                .thenReturn(Optional.of(List.of("IFT1025", "IFT2255", "IFT1000")));

        when(mockRepo.getCourseEligibility("IFT2255", List.of("IFT1000")))
                .thenReturn("""
                    {
                      "eligible": false,
                      "missing_prerequisites": ["IFT1025"]
                    }
                """);

        String r = service.checkEligibility("IFT2255", List.of("IFT1000"));

        assertEquals(
                "Vous n'êtes pas éligible à ce cours. Il vous manque les prerequis suivants : IFT1025",
                r
        );
    }

    @Test
    @DisplayName("checkEligibility() gère l'exception du repository")
    void testCheckEligibilityExceptionRepository() throws Exception {
        when(mockRepo.getAllCoursesId())
                .thenReturn(Optional.of(List.of("IFT1025", "IFT2255")));

        when(mockRepo.getCourseEligibility(anyString(), anyList()))
                .thenThrow(new RuntimeException("Erreur Planifium"));

        String r = service.checkEligibility("IFT2255", List.of("IFT1025"));

        assertEquals("Une erreur est survenue lors de la vérification d'éligibilité.", r);
    }

    /**
     * Test de comparerCombinaisonCours() avec une combinaison valide.
     * Vérifie que la sortie contient bien la combinaision, les crédits et les horaires.
     */
    @Test
    @DisplayName("comparerCombinaisonCours() : combinaison valide")
    void testComparerCombinaisonCoursValide() throws Exception {
        // Création d'un cours fictif
        Cours c1 = new Cours();
        c1.setId("IFT1025");
        c1.setCredits(3);

        // Mock du repository
        when(mockRepo.getAllCoursesId()).thenReturn(Optional.of(List.of("IFT1025")));
        when(mockRepo.getCourseBy("id", "IFT1025", "true", null))
                .thenReturn(Optional.of(List.of(c1)));

        List<List<String>> combinaisons = List.of(List.of("IFT1025"));
        List<List<String>> resultat = service.comparerCombinaisonCours(combinaisons, null);

        assertNotNull(resultat);
        assertEquals(1, resultat.size());
        assertTrue(resultat.get(0).get(1).contains("IFT1025"));
        //System.out.println(resultat.get(0).get(2));
        assertTrue(resultat.get(0).get(2).contains("3")); // crédits
    }

    /**
     * Test de comparerCombinaisonCours() avec un ID de cours invalide.
     * Doit retourner null.
     */
    @Test
    @DisplayName("comparerCombinaisonCours() : cours invalide")
    void testComparerCombinaisonCoursCoursInvalide() throws Exception {
        when(mockRepo.getAllCoursesId()).thenReturn(Optional.of(List.of("IFT1025")));

        List<List<String>> combinaisons = List.of(List.of("ANDYCHLOE"));
        List<List<String>> resultat = service.comparerCombinaisonCours(combinaisons, null);

        assertNull(resultat);
    }

    /**
     * Test de comparerCombinaisonCours() avec plusieurs cours et vérification des conflits.
     */
    @Test
    @DisplayName("comparerCombinaisonCours() : plusieurs cours avec conflit")
    void testComparerCombinaisonCoursConflit() throws Exception {
        Cours c1 = new Cours();
        c1.setId("IFT1025");
        c1.setCredits(3);

        Cours.Schedule schedule1 = new Cours.Schedule();
        schedule1.setSemester("H2025");
        Cours.Section section1 = new Cours.Section();
        section1.setName("A");
        Cours.Volet volet1 = new Cours.Volet();
        Cours.Activity act1 = new Cours.Activity();
        act1.setDays(List.of("LUN"));
        act1.setStart_time("08:00");
        act1.setEnd_time("10:00");
        volet1.setActivities(List.of(act1));
        section1.setVolets(List.of(volet1));
        schedule1.setSections(List.of(section1));
        c1.setSchedules(List.of(schedule1));

        Cours c2 = new Cours();
        c2.setId("IFT2255");
        c2.setCredits(3);

        Cours.Schedule schedule2 = new Cours.Schedule();
        schedule2.setSemester("H2025");
        Cours.Section section2 = new Cours.Section();
        section2.setName("B");
        Cours.Volet volet2 = new Cours.Volet();
        Cours.Activity act2 = new Cours.Activity();
        act2.setDays(List.of("LUN"));
        act2.setStart_time("09:00");
        act2.setEnd_time("11:00");
        volet2.setActivities(List.of(act2));
        section2.setVolets(List.of(volet2));
        schedule2.setSections(List.of(section2));
        c2.setSchedules(List.of(schedule2));

        when(mockRepo.getAllCoursesId()).thenReturn(Optional.of(List.of("IFT1025", "IFT2255")));
        when(mockRepo.getCourseBy("id", "IFT1025", "true", null)).thenReturn(Optional.of(List.of(c1)));
        when(mockRepo.getCourseBy("id", "IFT2255", "true", null)).thenReturn(Optional.of(List.of(c2)));

        List<List<String>> combinaisons = List.of(List.of("IFT1025", "IFT2255"));
        List<List<String>> resultat = service.comparerCombinaisonCours(combinaisons, "H2025");

        assertNotNull(resultat);
        assertEquals(1, resultat.size());

        String conflits = resultat.get(0).get(resultat.get(0).size() - 1);
        assertTrue(conflits.contains("CONFLIT")); // Il doit détecter un conflit
    }

}