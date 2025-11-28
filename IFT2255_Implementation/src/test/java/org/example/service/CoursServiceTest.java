package org.example.service;
import org.example.model.Cours;
import org.example.repository.CoursRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoursServiceTest {

    CoursRepository mockRepo;
    CoursService service;

    @BeforeEach
    void setUp() {
        mockRepo = mock(CoursRepository.class);
        service = CoursService.getInstance();
        service.setCoursRepository(mockRepo);
    }
    @Test
    void testValidateIdCoursValide() throws Exception {
        // Arrange
        when(mockRepo.getAllCoursesId()).thenReturn(Optional.of(Arrays.asList("IFT1025", "IFT2255")));

        // On utilise reflection car validateIdCours est private
        Method method = CoursService.class.getDeclaredMethod("validateIdCours", String.class);
        method.setAccessible(true);
        boolean result = (boolean)(method.invoke(service, "IFT1025"));

        // Assert
        assertTrue(result);
    }

    @Test
    void testComparerCoursCoursInexistant() throws Exception {
        // Arrange
        when(mockRepo.getAllCoursesId()).thenReturn(Optional.of(Arrays.asList("IFT1025")));
        when(mockRepo.getCourseById("IFT1025")).thenReturn(Optional.empty());

        // Act
        List<List<String>> result = service.comparerCours(new String[]{"IFT1025"}, new String[]{"id", "name"});

        // Assert
        assertNull(result, "Si le cours n'existe pas, la méthode devrait retourner null");
    }

    @Test
    void testComparerCoursExistants() throws Exception {
        // Création de cours mockés
        Cours cours1 = new Cours(
                Map.of("A2025", true),
                "IFT1025",
                "Cours de programmation 2",
                "Programmation 2",
                "Hiver",
                new String[]{"Lundi 9h", "Mercredi 11h"},
                new String[]{"IFT1001"},
                new String[]{},
                new String[]{},
                "https://udem.ca/IFT1025",
                3.0f,
                "Pré-requis: IFT1001",
                Map.of("Term1", true)
        );

        Cours cours2 = new Cours(
                Map.of("A2025", true),
                "IFT2255",
                "Cours d'algorithmes",
                "Algorithmes",
                "Automne",
                new String[]{"Mardi 10h", "Jeudi 12h"},
                new String[]{"IFT1025"},
                new String[]{},
                new String[]{},
                "https://udem.ca/IFT2255",
                3.0f,
                "Pré-requis: IFT1025",
                Map.of("Term1", true)
        );

        // Mock du repository
        when(mockRepo.getAllCoursesId()).thenReturn(Optional.of(Arrays.asList("IFT1025", "IFT2255")));
        when(mockRepo.getCourseById("IFT1025")).thenReturn(Optional.of(cours1));
        when(mockRepo.getCourseById("IFT2255")).thenReturn(Optional.of(cours2));

        // Critères à comparer
        String[] coursIds = {"IFT1025", "IFT2255"};
        String[] criteres = {"id", "name", "credits"};

        // Appel de la méthode
        List<List<String>> resultat = service.comparerCours(coursIds, criteres);

        // Vérifications
        assertNotNull(resultat);
        assertEquals(2, resultat.size());

        List<String> ligne1 = resultat.get(0);
        assertEquals(Arrays.asList("IFT1025", "IFT1025", "Programmation 2", "3.0"), ligne1);

        List<String> ligne2 = resultat.get(1);
        assertEquals(Arrays.asList("IFT2255", "IFT2255", "Algorithmes", "3.0"), ligne2);
    }

}
