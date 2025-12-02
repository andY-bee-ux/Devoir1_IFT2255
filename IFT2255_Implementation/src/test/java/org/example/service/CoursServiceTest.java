package org.example.service;

import org.example.repository.CoursRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
}
