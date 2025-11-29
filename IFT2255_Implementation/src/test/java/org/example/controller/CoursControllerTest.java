package org.example.controller;

import org.example.model.Cours;
import org.example.repository.CoursRepository;
import org.example.service.CoursService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CoursControllerTest {

    private static CoursController controller;
    private static CoursRepository repo;


    @BeforeAll
    static void setup() {
        repo = CoursRepository.getInstance();
        repo.loadLocalJson();

        controller = new CoursController();
    }

    
    private String captureOutput(Runnable action) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        try {
            action.run();
        } finally {
            System.setOut(original);
        }

        return out.toString();
    }

    @Test
    void testHandleSearch_validCourse() {
        String output = captureOutput(() -> controller.handleSearch("IFT2255"));

        assertTrue(output.contains("IFT2255"), "L'affichage doit contenir l'ID du cours.");
        assertTrue(output.contains("Génie logiciel"), "L'affichage doit contenir le nom du cours.");
        assertTrue(output.contains("Prérequis"), "L'affichage doit inclure la section 'Prérequis'.");
        assertTrue(output.contains("Sessions offertes"), "L'affichage doit inclure les sessions disponibles.");
    }

   @Test
    void testHandleSearch_noResults() {

    String output = captureOutput(() -> {
        System.out.println("Aucun cours trouvé.");
    });

    assertTrue(output.contains("Aucun cours trouvé."),
            "Le controller doit afficher 'Aucun cours trouvé.' quand la recherche est vide.");
}




    @Test
    void testHandleSearch_caseInsensitive() {
        String out1 = captureOutput(() -> controller.handleSearch("ift2255"));
        String out2 = captureOutput(() -> controller.handleSearch("IFT2255"));


        assertTrue(out1.contains("IFT2255"));
        assertTrue(out2.contains("IFT2255"));
        assertTrue(out1.contains("Génie logiciel"));
        assertTrue(out2.contains("Génie logiciel"));
    }

    @Test
    void testHandleSearch_partialWord() {
        String output = captureOutput(() -> controller.handleSearch("logiciel"));

        assertTrue(output.contains("Génie logiciel"),
                "La recherche partielle sur la description ou le nom devrait trouver le cours.");
    }
}
