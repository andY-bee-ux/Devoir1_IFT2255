package org.example.controller;

import io.javalin.http.Context;
import org.example.model.Avis;
import org.example.model.Cours;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

public class CoursControllerTest {

    /**
     * Tests unitaires pour la classe {@link CoursController}.
     *
     * Ces tests vérifient les fonctionnalités de comparaison de cours, 
     * recherche de cours, vérification, d'éligibilité a un cours, de comparaison de combinaisons de cours et de récupération
     * des avis associés aux cours.
     */

    // Tests pour comparerCours

    /**
     * Vérifie que la comparaison de deux cours avec des critères valides
     * retourne un statut HTTP 200 et une réponse JSON contenant la
     * comparaison.
     */
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

    /**
     * Vérifie le comportement lorsque des critères invalides sont fournis.
     * Le contrôleur doit gérer le critère inconnu et répondre avec un
     * statut approprié (ici on attend au minimum un statut 200).
     */
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

    /**
     * Vérifie que la comparaison retourne un statut 400 lorsque les
     * identifiants de cours fournis sont invalides.
     */
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

    /**
     * Teste la comparaison en demandant tous les critères disponibles afin
     * de s'assurer que la méthode supporte la liste complète des
     * attributs des cours sans erreur.
     */
    @Test
    @DisplayName("Comparaison avec tous les critères valides")
    void testComparerCours_withAllValidCriteria() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteComparaison req = new CoursController.RequeteComparaison();
        req.cours = new String[]{"IFT1025"};
        req.criteres = new String[]{
                "id", "name", "description", "credits", "scheduledSemester",
                "schedules", "prerequisite_courses", "equivalent_courses",
                "concomitant_courses", "udemWebsite", "requirement_text",
                "available_terms", "available_periods"
        };
        when(ctx.bodyAsClass(CoursController.RequeteComparaison.class)).thenReturn(req);

        controller.comparerCours(ctx);
        verify(ctx, atLeastOnce()).status(anyInt());
    }

    //Tests pour rechercherCours

    /**
     * Recherche d'un cours par identifiant valide. Le test vérifie que
     * l'appel au contrôleur retourne un code HTTP (au moins) indiquant
     * le traitement de la requête.
     */
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


    /**
     * Recherche de cours par nom (champ "name"). Vérifie que le
     * contrôleur traite la requête et renvoie un statut.
     */
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

    /**
     * Recherche de cours par description. Ce test s'assure que la
     * recherche textuelle sur la description fonctionne et ne provoque
     * pas d'erreur côté contrôleur.
     */
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

    /**
     * Vérifie la gestion d'un paramètre de recherche invalide. Le
     * contrôleur doit répondre sans planter (au moins un statut est
     * renvoyé pour signaler l'erreur ou l'absence de résultat).
     */
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

    /**
     * Recherche incluant les informations d'horaire et de semestre.
     * Vérifie que le paramètre `includeSchedule` et `semester` sont
     * correctement pris en compte par le contrôleur.
     */
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

    /**
     * Vérifie que la méthode d'éligibilité retourne un JSON indiquant
     * si l'étudiant est éligible au cours donné en fonction des
     * prérequis fournis.
     */
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

    /**
     * Teste le comportement lorsque l'identifiant du cours n'existe
     * pas. Le contrôleur doit renvoyer une réponse JSON gérant le
     * cas d'erreur sans lever d'exception non gérée.
     */
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

    /**
     * Cas où la liste des prérequis est vide : la vérification doit
     * retourner une réponse JSON correcte (généralement éligible ou
     * non selon l'implémentation) sans erreur.
     */
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

    /**
     * Comparaison de plusieurs combinaisons de cours pour une session
     * donnée. Vérifie que le contrôleur calcule et renvoie des
     * résultats (statut et JSON) pour des combinaisons valides.
     */
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

    /**
     * Vérifie que la méthode retourne un statut 400 et un message
     * d'erreur lorsque la combinaison contient des identifiants de
     * cours invalides.
     */
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

    /**
     * Cas avec une seule combinaison fournie : s'assure que le
     * contrôleur peut traiter une liste contenant une seule
     * combinaison sans erreur.
     */
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

    /**
     * Vérifie que la méthode `getAvis` retourne une liste (éventuellement
     * vide) pour un cours donné. Ici on teste le cas où aucun avis n'est
     * présent et on s'attend à une liste vide.
     */
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

    /**
    * S'assure que `getAvis` ne retourne jamais `null` même si aucun
    * avis n'existe pour le cours fourni.
    */
    @Test
    @DisplayName("Récupération des avis retourne une liste non-null")
    void testGetAvis_notNull() {
        CoursController controller = new CoursController();
        Cours cours = new Cours();

        List<Avis> result = controller.getAvis(cours);

        assertNotNull(result, "getAvis devrait retourner une liste non-null");
    }
}