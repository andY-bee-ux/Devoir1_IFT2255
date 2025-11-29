package org.example.controller;

import io.javalin.http.Context;
import org.example.model.Cours;
import org.example.repository.CoursRepository;
import org.example.service.CoursService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CoursControllerTest {

    private static CoursController controller;

    @BeforeAll
    static void setup() {
        CoursRepository.getInstance().loadLocalJson();
        controller = new CoursController();
    }

    @Test
    void testHandleSearch_validCourse() {
        Context ctx = mock(Context.class);

        when(ctx.queryParam("query")).thenReturn("IFT2255");
        when(ctx.status(anyInt())).thenReturn(ctx);  

        controller.handleSearchREST(ctx);

        verify(ctx).status(200);
        verify(ctx).json(argThat(results ->
                results instanceof java.util.List<?> &&
                ((java.util.List<?>) results).size() == 1 &&
                ((Cours)((java.util.List<?>) results).get(0)).getId().equals("IFT2255")
        ));
    }

    @Test
    void testHandleSearch_missingQuery() {
        Context ctx = mock(Context.class);

        when(ctx.queryParam("query")).thenReturn(null);
        when(ctx.status(anyInt())).thenReturn(ctx); 

        controller.handleSearchREST(ctx);

        verify(ctx).status(400);
        verify(ctx).json("Le paramètre 'query' est obligatoire.");
    }

    @Test
    void testHandleSearch_noResults() {
        Context ctx = mock(Context.class);

        when(ctx.queryParam("query")).thenReturn("FHDSKAJF");
        when(ctx.status(anyInt())).thenReturn(ctx); 

        controller.handleSearchREST(ctx);

        verify(ctx).status(404);
        verify(ctx).json("Aucun résultat trouvé pour : FHDSKAJF");
    }

    @Test
    void testHandleSearch_caseInsensitive() {
        Context ctx = mock(Context.class);

        when(ctx.queryParam("query")).thenReturn("ift2255");
        when(ctx.status(anyInt())).thenReturn(ctx); 

        controller.handleSearchREST(ctx);

        verify(ctx).status(200);
    }

    @Test
    void testHandleSearch_partialWord() {
        Context ctx = mock(Context.class);

        when(ctx.queryParam("query")).thenReturn("logiciel");
        when(ctx.status(anyInt())).thenReturn(ctx); 

        controller.handleSearchREST(ctx);

        verify(ctx).status(200);
    }
}
