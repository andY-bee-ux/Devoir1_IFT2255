package org.example.repository;

import org.example.model.Cours;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CoursRepositoryTest {
    CoursRepository coursRepository = CoursRepository.getInstance();

    @Test
    @DisplayName("getAllCourses() devrait retourner une liste non vide")
    void testGetAllCoursesIdListeNonVide() throws Exception {

        // ACT
        Optional<List<String>> optListe = coursRepository.getAllCoursesId();

        // ASSERT : l'Optional doit être présent
        assertTrue(optListe.isPresent(), "La liste ne devrait pas être vide");

        List<String> liste = optListe.get();

        // ASSERT : il doit y avoir au moins 1 cours
        assertFalse(liste.isEmpty(), "Il devrait y avoir au moins un cours");

    }
    @Test
    @DisplayName("La liste de cours devrait contenir le cours ift2255")
    void testGetAllCoursesIdListeCoursIft2255() throws Exception {
        Optional<List<String>> optListe = coursRepository.getAllCoursesId();
        // je sais pas si c'est nécessaire de le remettre ici
        assertTrue(optListe.isPresent(), "La liste ne devrait pas être vide");
        List<String> liste = optListe.get();
        assertTrue(liste.contains("IFT2255"), "La liste devrait contenir le cours ift2255");
    }

    // je ne pense pas que ce soit nécessaire de tester l'échec car cela serait plus imputable à Planifium qu'à nous en cas d'erreur...
//    @Test
//    @DisplayName("La liste de cours ne devrait pas contenir ")
//    void testGetAllCoursesListeCoursIft2255() throws Exception {
//        Optional<List<String>> optListe = coursRepository.getAllCourses();
//        // je sais pas si c'est nécessaire de le remettre ici
//        assertTrue(optListe.isPresent(), "La liste ne devrait pas être vide");
//        List<String> liste = optListe.get();
//        assertTrue(liste.contains("ift2255"), "La liste devrait contenir le cours ift2255");
//    }

    // test d'invariance
    @Test
    @DisplayName("La méthode getCoursById() retourne bien le cours recherché")
    void testGetCoursByIdCoursIFT1025() throws Exception {
        <Optional<List<Cours>> optListe = coursRepository.getCourseBy("id", "IFT1025",null,null).get().get(0);
        assertTrue(optListe.isPresent(), "Ça devrait retourner un objet Cours");
        Cours cours = optListe.get();
        assertTrue( cours.getId().equals("IFT1025"));

    }

    @Test
    @DisplayName("La méthode getCoursById() retourne bien le cours recherché")
    void testGetCoursByIdCoursIFT1025BonNom() throws Exception {
        Optional<Cours> optListe = coursRepository.getCourseById("IFT1025");
        assertTrue(optListe.isPresent(), "Ça devrait retourner un objet Cours");
        Cours cours = optListe.get();
        assertTrue( cours.getName().equals("Programmation 2"), "le nom du cours de id IFT1025 est Programmation 2");

    }

    @Test
    @DisplayName("getCourseById() devrait retourner Optional.empty pour un id inexistant")
    void testGetCoursByIdInexistant() throws Exception {
        Optional<Cours> optCours = coursRepository.getCourseById("XYZ0000");
        assertTrue(optCours.isEmpty(), "Un id inexistant doit retourner Optional.empty()");
    }
    @Test
    @DisplayName("La liste de cours ne doit pas contenir de doublons")
    void testGetAllCoursesIdPasDeDoublons() throws Exception {
        Optional<List<String>> optListe = coursRepository.getAllCoursesId();
        assertTrue(optListe.isPresent());
        List<String> liste = optListe.get();

        Set<String> set = new HashSet<>(liste);
        Set<String> uniques = new HashSet<>();

        assertEquals(set.size(), liste.size(), "Il ne devrait pas y avoir de doublons dans la liste des cours");
    }


}
