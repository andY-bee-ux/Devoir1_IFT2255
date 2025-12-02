package org.example.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CoursTest {

    @Test
    @DisplayName("Le constructeur vide devrait créer une instance non nulle")
    void testConstructeurVide() {
        // ACT
        Cours cours = new Cours();

        // ASSERT
        assertNotNull(cours, "L'instance de Cours ne devrait pas être null après instanciation");
    }

    @Test
    @DisplayName("Les Setters et Getters de base devraient fonctionner")
    void testGettersSetters() {
        // ARRANGE
        Cours cours = new Cours();
        String idAttendu = "IFT1015";
        String nomAttendu = "Programmation I";

        // ACT
        cours.setId(idAttendu);
        cours.setName(nomAttendu);

        // ASSERT
        assertEquals(idAttendu, cours.getId(), "L'ID ne correspond pas");
        assertEquals(nomAttendu, cours.getName(), "Le nom ne correspond pas");
    }

    @Test
    @DisplayName("Test de la structure imbriquée (Schedule -> Section)")
    void testStructureImbriquee() {
        // 1. On prépare les données (ARRANGE)
        Cours cours = new Cours();
        Cours.Schedule schedule = new Cours.Schedule();
        Cours.Section section = new Cours.Section();
        
        // On remplit le niveau le plus bas (Section)
        section.setName("Section A");
        section.setCapacity("50");
        
        // On l'ajoute au niveau intermédiaire (Schedule)
        List<Cours.Section> sections = new ArrayList<>();
        sections.add(section);
        schedule.setSections(sections);
        schedule.setSemester("Hiver 2024");

        // On ajoute tout ça au Cours
        List<Cours.Schedule> schedules = new ArrayList<>();
        schedules.add(schedule);
        cours.setSchedules(schedules);

        // 2. On vérifie (ASSERT)
        // Vérifie que la liste n'est pas vide
        assertNotNull(cours.getSchedules(), "La liste des horaires ne doit pas être null");
        assertFalse(cours.getSchedules().isEmpty(), "La liste des horaires doit contenir un élément");

        // On descend dans les niveaux pour voir si l'info "Section A" est toujours là
        Cours.Schedule scheduleRecupere = cours.getSchedules().get(0);
        Cours.Section sectionRecuperee = scheduleRecupere.getSections().get(0);

        assertEquals("Hiver 2024", scheduleRecupere.getSemester());
        assertEquals("Section A", sectionRecuperee.getName());
        assertEquals("50", sectionRecuperee.getCapacity());
    }

    @Test
    @DisplayName("Les prérequis (Tableau) et les disponibilités (Map) sont bien gérés")
    void testCollections() {
        // --- ARRANGE 
        Cours cours = new Cours();
        
        // On prépare un tableau de String
        String[] prealables = {"IFT1015", "MAT1903"};
        
        // On prépare une Map (Clé -> Valeur)
        Map<String, Boolean> disponibilites = new HashMap<>();
        disponibilites.put("Hiver", true);
        disponibilites.put("Ete", false);

        // --- ACT (Action) ---
        cours.setPrerequisite_courses(prealables);
        cours.setAvailable_terms(disponibilites);

        // --- ASSERT (Vérification) ---
        // Vérifions le tableau
        assertNotNull(cours.getPrerequisite_courses());
        assertEquals(2, cours.getPrerequisite_courses().length, "Il devrait y avoir 2 préalables");
        assertEquals("IFT1015", cours.getPrerequisite_courses()[0], "Le premier préalable est faux");

        // Vérifions la Map
        assertNotNull(cours.getAvailable_terms());
        assertTrue(cours.getAvailable_terms().get("Hiver"), "Le cours devrait être dispo en Hiver");
        assertFalse(cours.getAvailable_terms().get("Ete"), "Le cours ne devrait pas être dispo en Été");
    }

    @Test
    @DisplayName("Deux objets avec les mêmes données sont des instances différentes en mémoire")
    void testIdentite() {
        // ARRANGE
        Cours cours1 = new Cours();
        cours1.setId("IFT1015");

        Cours cours2 = new Cours();
        cours2.setId("IFT1015");

        // ASSERT
        // Vérifie que les IDs sont identiques (le contenu)
        assertEquals(cours1.getId(), cours2.getId());

        // Vérifie que les objets eux-mêmes sont différents (la case mémoire)
        assertNotSame(cours1, cours2, "Ce sont deux objets distincts en mémoire, même s'ils ont le même ID");
    }
}