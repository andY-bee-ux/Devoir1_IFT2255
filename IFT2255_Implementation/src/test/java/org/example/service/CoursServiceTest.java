package org.example.service;
import org.example.model.Cours;
import org.example.model.Resultats;

import org.example.repository.CoursRepository;
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

    @Test
    @DisplayName("checkEligibility() : Gèreune réponse JSON invalide (ne plante pas)")
    void testCheckEligibility_ProtectionContreJsonCorrompu() throws Exception {
        //ARRANGE 
        when(mockRepo.getAllCoursesId())
                .thenReturn(Optional.of(List.of("IFT2255")));

        // Le serveur renvoie du HTML d'erreur ou du texte vide au lieu du JSON
        when(mockRepo.getCourseEligibility(anyString(), anyList()))
                .thenReturn("<html><body>500 Internal Server Error</body></html>");

        // ACT 
        String resultat = service.checkEligibility("IFT2255", List.of());

        // ASSERT 
        // On vérifie qu'on est bien tombé dans le catch
        // et que l'utilisateur reçoit un message propre au lieu d'une "crash page".
        assertEquals("Une erreur est survenue lors de la vérification d'éligibilité.", resultat);
    }

    
    

@Test
@DisplayName("difficulteCours() retourne le bon message pour un cours ")    
void testDifficulteCours() {      
        
        // ARRANGE
        CoursService service = CoursService.getInstance();
        Resultats coursDifficile = new Resultats("MAT1400");

        // ACT
        String message = service.difficulteCours(coursDifficile);

        // ASSERT
        assertEquals("Le cours Calcul 1 est considéré comme difficile avec un score de 1.55/5.", message);

}

@Test
@DisplayName("difficulteCours() retourne le bon message pour un cours facile")    
void testDifficulteCoursFacile() {      
        
        // ARRANGE
        CoursService service = CoursService.getInstance();
        Resultats coursFacile = new Resultats("ANG1933");

        // ACT
        String message = service.difficulteCours(coursFacile);

        // ASSERT
        assertEquals("Le cours Expression orale académique et professionnelle est considéré comme facile avec un score de 4.79/5", message);
}

@Test
@DisplayName("populariteCours() retourne le bon message pour un cours populaire")    
void testPopulariteCoursPopulaire() {      
        
        // ARRANGE
        CoursService service = CoursService.getInstance();
        Resultats coursPopulaire = new Resultats("IFT1015");

        // ACT
        String message = service.populariteCours(coursPopulaire);

        // ASSERT
        assertEquals("Le cours Programmation 1 est très populaire avec 658 participants.", message);
}

@Test
@DisplayName("populariteCours() retourne le bon message pour un cours absent des résultats")    
void testPopulariteCoursPeuPopulaire() {      
        // ARRANGE
        CoursService service = CoursService.getInstance();
        Resultats coursPeuPopulaire = new Resultats("ART1001");

        // ACT
        String message = service.populariteCours(coursPeuPopulaire);

        // ASSERT
        assertEquals("Le cours demandé est absent des résultats. Veuillez vérifier le sigle.", message);
}

@Test
@DisplayName("comparerDifficulte() retourne le bon message pour deux cours")
void testCompareDifficulteCours() {      
        
        // ARRANGE
        CoursService service = CoursService.getInstance();
        Resultats cours1 = new Resultats("IFT1015"); 
        Resultats cours2 = new Resultats("MAT1400"); 

        // ACT
        String message = service.comparerDifficulte(cours1, cours2);

        // ASSERT
        assertEquals("Le cours Programmation 1 est considéré comme plus facile que Calcul 1 avec un score de 2.29/5 contre 1.55/5.", message);
}

@Test
@DisplayName("comparerPopularite() retourne le bon message pour deux cours")    
void testComparePopulariteCours() {      
        
        // ARRANGE
        CoursService service = CoursService.getInstance();
        Resultats cours1 = new Resultats("IFT1015"); 
        Resultats cours2 = new Resultats("ANG1933"); 

        // ACT
        String message = service.comparerPopularite(cours1, cours2);

        // ASSERT
        assertEquals("Le cours Programmation 1 est plus populaire que Expression orale académique et professionnelle avec 658 participants contre 5.", message);

}
}                 



