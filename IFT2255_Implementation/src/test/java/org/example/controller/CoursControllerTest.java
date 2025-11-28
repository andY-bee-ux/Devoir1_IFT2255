package org.example.controller;
import io.javalin.http.Context;
import org.example.controller.CoursController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

public class CoursControllerTest {

    @Test
    @DisplayName("Comparaison de deux cours avec critères de comparaison valides")
    void testComparerCours_withMockContext() {
        // Créer un controller
        CoursController controller = new CoursController();

        // Mock du context
        Context ctx = mock(Context.class);

        // Simuler le body de la requête
        CoursController.RequeteComparaison req = new CoursController.RequeteComparaison();
        req.cours = new String[]{"IFT1025", "IFT2255"};
        req.criteres = new String[]{"name", "credits"};

        when(ctx.bodyAsClass(CoursController.RequeteComparaison.class)).thenReturn(req);

        // Appel de la méthode
        controller.comparerCours(ctx);

        // Vérifier que ctx.json(...) a été appelé
        verify(ctx).status(200);
        verify(ctx).json(any(List.class));
    }

    @Test
    @DisplayName("Comparaison de cours avec critères de comparaisons invalides")
    void testComparerCours_withoutMockContext() {
        CoursController controller = new CoursController();
        Context ctx = mock(Context.class);
        CoursController.RequeteComparaison req = new CoursController.RequeteComparaison();
        req.cours = new String[]{"IFT1025", "IFT2255"};
        // nom n'est pas un nom de critère valide
        req.criteres = new String[]{"nom", "credits"};
        when(ctx.bodyAsClass(CoursController.RequeteComparaison.class)).thenReturn(req);
        controller.comparerCours(ctx);
        verify(ctx).status(400);
    }
}

