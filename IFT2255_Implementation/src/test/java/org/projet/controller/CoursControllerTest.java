package org.projet.controller;

import io.javalin.http.Context;
import org.projet.model.Avis;
import org.projet.model.Cours;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

public class CoursControllerTest {

    //Tests pour comparerCours

    @Test
    @DisplayName("Comparaison de deux cours avec critères de comparaison valides")
    void testComparerCours_withValidCriteria() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);

        CoursController.RequeteComparaison req = new CoursController.RequeteComparaison();
        req.cours = new String[]{"IFT1025", "IFT2255"};
        req.criteres = new String[]{"name", "credits"};

        when(ctx.bodyAsClass(CoursController.RequeteComparaison.class)).thenReturn(req);

        controller.comparerCours(ctx);

        verify(ctx).status(200);
        verify(ctx).json(any(List.class));
    }

    @Test
    @DisplayName("Comparaison de cours avec critères invalides")
    void testComparerCours_withInvalidCriteria() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteComparaison req = new CoursController.RequeteComparaison();
        req.cours = new String[]{"IFT1025", "IFT2255"};
        req.criteres = new String[]{"nom", "credits"};
        when(ctx.bodyAsClass(CoursController.RequeteComparaison.class)).thenReturn(req);
        controller.comparerCours(ctx);
        // Critère invalide retourne quand même 200 avec "Critère inconnu"
        verify(ctx).status(200);
    }

    @Test
    @DisplayName("Comparaison avec cours invalides")
    void testComparerCours_withInvalidCourseIds() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteComparaison req = new CoursController.RequeteComparaison();
        req.cours = new String[]{"INVALID1", "INVALID2"};
        req.criteres = new String[]{"name", "credits"};
        when(ctx.bodyAsClass(CoursController.RequeteComparaison.class)).thenReturn(req);

        controller.comparerCours(ctx);
        verify(ctx).status(400);
    }


    //Tests pour rechercherCours

    @Test
    @DisplayName("Recherche de cours par ID valide")
    void testRechercherCours_byValidId() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteRecherche req = new CoursController.RequeteRecherche();
        req.param = "id";
        req.valeur = "IFT1025";
        req.includeSchedule = "false";
        req.semester = null;

        when(ctx.bodyAsClass(CoursController.RequeteRecherche.class)).thenReturn(req);

        controller.rechercherCours(ctx);

        verify(ctx).status(anyInt());
    }

    @Test
    @DisplayName("Recherche de cours par nom")
    void testRechercherCours_byName() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteRecherche req = new CoursController.RequeteRecherche();
        req.param = "name";
        req.valeur = "Algorithmic";
        req.includeSchedule = "false";
        req.semester = null;

        when(ctx.bodyAsClass(CoursController.RequeteRecherche.class)).thenReturn(req);

        controller.rechercherCours(ctx);

        verify(ctx).status(anyInt());
    }

    @Test
    @DisplayName("Recherche de cours par description")
    void testRechercherCours_byDescription() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteRecherche req = new CoursController.RequeteRecherche();
        req.param = "description";
        req.valeur = "fundamentals";
        req.includeSchedule = "false";
        req.semester = null;

        when(ctx.bodyAsClass(CoursController.RequeteRecherche.class)).thenReturn(req);

        controller.rechercherCours(ctx);

        verify(ctx).status(anyInt());
    }

    @Test
    @DisplayName("Recherche avec paramètre invalide")
    void testRechercherCours_byInvalidParam() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteRecherche req = new CoursController.RequeteRecherche();
        req.param = "invalid_param";
        req.valeur = "value";
        req.includeSchedule = "false";
        req.semester = null;

        when(ctx.bodyAsClass(CoursController.RequeteRecherche.class)).thenReturn(req);

        controller.rechercherCours(ctx);

        verify(ctx, atLeastOnce()).status(anyInt());
    }

    @Test
    @DisplayName("Recherche avec schedule et semester")
    void testRechercherCours_withScheduleAndSemester() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteRecherche req = new CoursController.RequeteRecherche();
        req.param = "id";
        req.valeur = "IFT1025";
        req.includeSchedule = "true";
        req.semester = "FALL";

        when(ctx.bodyAsClass(CoursController.RequeteRecherche.class)).thenReturn(req);

        controller.rechercherCours(ctx);

        verify(ctx).status(anyInt());
    }

    //Tests pour checkEligibility

    @Test
    @DisplayName("Vérification d'éligibilité avec cours et prérequis valides")
    void testCheckEligibility_validCourseAndPrerequisites() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteEligibilite req = new CoursController.RequeteEligibilite();
        req.idCours = "IFT2255";
        req.listeCours = List.of("IFT1025", "IFT1030");

        when(ctx.bodyAsClass(CoursController.RequeteEligibilite.class)).thenReturn(req);

        controller.checkEligibility(ctx);

        verify(ctx).json(any());
    }

    @Test
    @DisplayName("Vérification d'éligibilité avec cours invalide")
    void testCheckEligibility_invalidCourse() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteEligibilite req = new CoursController.RequeteEligibilite();
        req.idCours = "INVALID_COURSE";
        req.listeCours = List.of("IFT1025");

        when(ctx.bodyAsClass(CoursController.RequeteEligibilite.class)).thenReturn(req);

        controller.checkEligibility(ctx);

        verify(ctx).json(any());
    }

    @Test
    @DisplayName("Vérification d'éligibilité avec liste de prérequis vide")
    void testCheckEligibility_emptyPrerequisites() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteEligibilite req = new CoursController.RequeteEligibilite();
        req.idCours = "IFT2255";
        req.listeCours = List.of();

        when(ctx.bodyAsClass(CoursController.RequeteEligibilite.class)).thenReturn(req);

        controller.checkEligibility(ctx);

        verify(ctx).json(any());
    }

    //Tests pour comparerCombinaisonCours

    @Test
    @DisplayName("Comparaison de combinaisons de cours valides")
    void testComparerCombinaisonCours_valid() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteComparaisonCombinaison req = new CoursController.RequeteComparaisonCombinaison();
        req.listeCours = List.of(
                List.of("IFT1025", "IFT1030"),
                List.of("IFT2255", "IFT2000")
        );
        req.session = "FALL";

        when(ctx.bodyAsClass(CoursController.RequeteComparaisonCombinaison.class)).thenReturn(req);

        controller.comparerCombinaisonCours(ctx);

        verify(ctx).status(anyInt());
        verify(ctx).json(any());
    }

    @Test
    @DisplayName("Comparaison de combinaisons avec cours invalide")
    void testComparerCombinaisonCours_invalidCourseId() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteComparaisonCombinaison req = new CoursController.RequeteComparaisonCombinaison();
        req.listeCours = List.of(
                List.of("INVALID1", "INVALID2")
        );
        req.session = "FALL";

        when(ctx.bodyAsClass(CoursController.RequeteComparaisonCombinaison.class)).thenReturn(req);

        controller.comparerCombinaisonCours(ctx);

        verify(ctx).status(400);
        verify(ctx).json("Requête invalide");
    }

    @Test
    @DisplayName("Comparaison de combinaisons avec une seule combinaison")
    void testComparerCombinaisonCours_singleCombination() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteComparaisonCombinaison req = new CoursController.RequeteComparaisonCombinaison();
        req.listeCours = List.of(
                List.of("IFT1025", "IFT1030", "IFT2255")
        );
        req.session = "WINTER";

        when(ctx.bodyAsClass(CoursController.RequeteComparaisonCombinaison.class)).thenReturn(req);

        controller.comparerCombinaisonCours(ctx);

        verify(ctx).status(anyInt());
    }

    //Tests pour getAvis

    @Test
    @DisplayName("Récupération des avis pour un cours")
    void testGetAvis() {
        CoursController controller = new CoursController();
        Cours cours = new Cours();
        cours.setId("IFT1025");

        List<Avis> result = controller.getAvis(cours);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Récupération des avis retourne une liste non-null")
    void testGetAvis_notNull() {
        CoursController controller = new CoursController();
        Cours cours = new Cours();

        List<Avis> result = controller.getAvis(cours);

        assertNotNull(result, "getAvis devrait retourner une liste non-null");
    }
}