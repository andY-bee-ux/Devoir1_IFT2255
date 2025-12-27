package org.projet.service;

import org.projet.exception.HoraireException;
import org.projet.model.Cours;
import org.projet.model.Resultats;
import org.projet.repository.CoursRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.ByteArrayInputStream;

import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.util.function.BiFunction;
import java.net.*;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.io.IOException;



/**
 * Cette classe permet de générer des tests unitaires
 */

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    // Helper: start a simple HTTP server for tests and set system property to redirect Planifium base URL
    private HttpServer startServer(BiFunction<String,String,String> responder) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            String response = responder.apply(path, query);
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();
        int port = server.getAddress().getPort();
        System.setProperty("planifium.base", "http://172.17.0.1:" + port);
        return server;
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


    /**
     * Test de comparerCombinaisonCours() avec une combinaison valide.
     * Vérifie que la sortie contient bien la combinaision, les crédits et les horaires.
     */
    @Test
    @DisplayName("comparerCombinaisonCours() : combinaison valide")
    void testComparerCombinaisonCoursValide() throws Exception {
        // Création d'un cours fictif
        Cours c1 = new Cours();
        c1.setId("IFT1025");
        c1.setCredits(3);

        // Mock du repository
        when(mockRepo.getAllCoursesId()).thenReturn(Optional.of(List.of("IFT1025")));
        when(mockRepo.getCourseBy("id", "IFT1025", "true", null))
                .thenReturn(Optional.of(List.of(c1)));

        List<List<String>> combinaisons = List.of(List.of("IFT1025"));
        List<List<String>> resultat = service.comparerCombinaisonCours(combinaisons, null);

        assertNotNull(resultat);
        assertEquals(1, resultat.size());
        assertTrue(resultat.get(0).get(1).contains("IFT1025"));
        //System.out.println(resultat.get(0).get(2));
        assertTrue(resultat.get(0).get(2).contains("3")); // crédits
    }

    /**
     * Test de comparerCombinaisonCours() avec un ID de cours invalide.
     * Doit retourner null.
     */
    @Test
    @DisplayName("comparerCombinaisonCours() : cours invalide")
    void testComparerCombinaisonCoursCoursInvalide() throws Exception {
        when(mockRepo.getAllCoursesId()).thenReturn(Optional.of(List.of("IFT1025")));

        List<List<String>> combinaisons = List.of(List.of("ANDYCHLOE"));
        List<List<String>> resultat = service.comparerCombinaisonCours(combinaisons, null);

        assertNull(resultat);
    }

    /**
     * Test de comparerCombinaisonCours() avec plusieurs cours et vérification des conflits.
     */
    @Test
    @DisplayName("comparerCombinaisonCours() : plusieurs cours avec conflit")
    void testComparerCombinaisonCoursConflit() throws Exception {
        Cours c1 = new Cours();
        c1.setId("IFT1025");
        c1.setCredits(3);

        Cours.Schedule schedule1 = new Cours.Schedule();
        schedule1.setSemester("H2025");
        Cours.Section section1 = new Cours.Section();
        section1.setName("A");
        Cours.Volet volet1 = new Cours.Volet();
        Cours.Activity act1 = new Cours.Activity();
        act1.setDays(List.of("LUN"));
        act1.setStart_time("08:00");
        act1.setEnd_time("10:00");
        volet1.setActivities(List.of(act1));
        section1.setVolets(List.of(volet1));
        schedule1.setSections(List.of(section1));
        c1.setSchedules(List.of(schedule1));

        Cours c2 = new Cours();
        c2.setId("IFT2255");
        c2.setCredits(3);

        Cours.Schedule schedule2 = new Cours.Schedule();
        schedule2.setSemester("H2025");
        Cours.Section section2 = new Cours.Section();
        section2.setName("B");
        Cours.Volet volet2 = new Cours.Volet();
        Cours.Activity act2 = new Cours.Activity();
        act2.setDays(List.of("LUN"));
        act2.setStart_time("09:00");
        act2.setEnd_time("11:00");
        volet2.setActivities(List.of(act2));
        section2.setVolets(List.of(volet2));
        schedule2.setSections(List.of(section2));
        c2.setSchedules(List.of(schedule2));

        when(mockRepo.getAllCoursesId()).thenReturn(Optional.of(List.of("IFT1025", "IFT2255")));
        when(mockRepo.getCourseBy("id", "IFT1025", "true", null)).thenReturn(Optional.of(List.of(c1)));
        when(mockRepo.getCourseBy("id", "IFT2255", "true", null)).thenReturn(Optional.of(List.of(c2)));

        List<List<String>> combinaisons = List.of(List.of("IFT1025", "IFT2255"));
        List<List<String>> resultat = service.comparerCombinaisonCours(combinaisons, "H2025");

        assertNotNull(resultat);
        assertEquals(1, resultat.size());

        String conflits = resultat.get(0).get(resultat.get(0).size() - 1);
        assertTrue(conflits.contains("CONFLIT")); // Il doit détecter un conflit
    }

    // Tests pour la fonctionnalité "Vérifier son éligibilité à un cours"
        /**
         * Ce test vérifie que la méthode {@code checkEligibilityNew} retourne un message
         * d'erreur lorsque le cycle d’études n’est pas fourni.
         */
        @Test
        @DisplayName("checkEligibilityNew() retourne une erreur si le cycle est null")
        void testCheckEligibilityNewCycleNull() throws Exception {

                when(mockRepo.getAllCoursesId())
                        .thenReturn(Optional.of(List.of("IFT2255", "IFT1025")));

                String r = service.checkEligibilityNew(
                        "IFT2255",
                        List.of("IFT1025"),
                        null
                );

                assertEquals("Le cycle doit être fourni", r);
        }
        /**
         * Ce test vérifie que la méthode {@code checkEligibilityNew} retourne un message
         * d'erreur lorsque le cycle d’études fourni est hors des bornes autorisées.
        */

        @Test
        @DisplayName("checkEligibilityNew() retourne une erreur si le cycle est hors bornes")
        void testCheckEligibilityNewCycleInvalide() throws Exception {

                when(mockRepo.getAllCoursesId())
                        .thenReturn(Optional.of(List.of("IFT2255", "IFT1025")));

                String r = service.checkEligibilityNew(
                        "IFT2255",
                        List.of("IFT1025"),
                        5
                );

                assertEquals("Le cycle fourni est invalide", r);
        }

        /**
         * Ce test vérifie la règle métier selon laquelle un étudiant de premier cycle
         * ne peut pas s’inscrire à un cours de cycles supérieurs (niveau 6000+).
         */
        @Test
        @DisplayName("checkEligibilityNew() refuse un cours 6000+ pour un étudiant de 1er cycle")
        void testCheckEligibilityNewCycle1Cours6000() throws Exception {

        when(mockRepo.getAllCoursesId())
                .thenReturn(Optional.of(List.of("IFT6010", "IFT2255")));

        String r = service.checkEligibilityNew(
                "IFT6010",
                List.of("IFT2255"),
                1
        );

        assertTrue(
                r.contains("cycles supérieurs"),
                "Le message doit indiquer que le cours est réservé aux cycles supérieurs"
        );
        }

/**
 * Ce test vérifie que la méthode {@code checkEligibilityNew} rejette un identifiant
 * de cours invalide avant tout appel au repository.
 */
    @Test
    @DisplayName("checkEligibilityNew() retourne un message d'erreur si l'id du cours est invalide")
    void testCheckEligibilityNewIdCoursInvalide() throws Exception {
        when(mockRepo.getAllCoursesId())
                .thenReturn(Optional.of(List.of("IFT1025", "IFT2255")));

        String r = service.checkEligibilityNew("TJ5035", List.of("IFT1025"), 1);

        assertEquals("L'id du cours est invalide", r);
        verify(mockRepo, never()).getCourseEligibility(anyString(), anyList());
    }
/**
 * Vérifie que la méthode {@code checkEligibilityNew} retourne une erreur
 * lorsque la liste des cours complétés contient un cours invalide.
 */

    @Test
    @DisplayName("checkEligibilityNew() retourne un message si des cours complétés sont invalides")
    void testCheckEligibilityNewCoursCompletesInvalides() throws Exception {

        when(mockRepo.getAllCoursesId())
                .thenReturn(Optional.of(List.of("IFT2255")));

        String r = service.checkEligibilityNew("IFT2255", List.of("TJOFF3334"), 1);

        assertEquals("Il y a des cours complétés invalides", r);
        verify(mockRepo, never()).getCourseEligibility(anyString(), anyList());
    }
/**
 * Ce test vérifie que la méthode {@code checkEligibilityNew} retourne une erreur
 * lorsque la liste des cours complétés contient un cours invalide.
 */
    @Test
    @DisplayName("checkEligibilityNew() éligible")
    void testCheckEligibilityNewEligible() throws Exception {
        when(mockRepo.getAllCoursesId())
                .thenReturn(Optional.of(List.of("IFT1025", "IFT2255")));

        when(mockRepo.getCourseEligibility("IFT2255", List.of("IFT1025")))
                .thenReturn("""
                    {
                      "eligible": true,
                      "missing_prerequisites": []
                    }
                """);

        String r = service.checkEligibilityNew("IFT2255", List.of("IFT1025"), 1);

        assertEquals("Vous êtes éligible à ce cours!", r);
    }

    /**
 * Ce test vérifie le cas où l’étudiant n’est pas éligible au cours
 * en raison de prérequis manquants.
 */
    @Test
    @DisplayName("checkEligibilityNew() non éligible")
    void testCheckEligibilityNewNonEligible() throws Exception {
        when(mockRepo.getAllCoursesId())
                .thenReturn(Optional.of(List.of("IFT1025", "IFT2255", "IFT1000")));

        when(mockRepo.getCourseEligibility("IFT2255", List.of("IFT1000")))
                .thenReturn("""
                    {
                      "eligible": false,
                      "missing_prerequisites": ["IFT1025"]
                    }
                """);

        String r = service.checkEligibilityNew("IFT2255", List.of("IFT1000"), 1);

        assertEquals(
                "Vous n'êtes pas éligible à ce cours. Il vous manque les prerequis suivants : IFT1025",
                r
        );
    }
/**
 * Ce test vérifie que la méthode {@code checkEligibilityNew} gère correctement
 * une exception levée par le repository lors de la vérification
 * des prérequis.
 */
    @Test
    @DisplayName("checkEligibilityNew() gère l'exception du repository")
    void testCheckEligibilityNewExceptionRepository() throws Exception {
        when(mockRepo.getAllCoursesId())
                .thenReturn(Optional.of(List.of("IFT1025", "IFT2255")));

        when(mockRepo.getCourseEligibility(anyString(), anyList()))
                .thenThrow(new RuntimeException("Erreur Planifium"));

        String r = service.checkEligibilityNew("IFT2255", List.of("IFT1025"), 1);

        assertEquals("Une erreur est survenue lors de la vérification d'éligibilité.", r);
    }

    // Tests pour la fonctionnalité "Créer un ensemble de cours" + génération d'horaires avec détection de conflits horaires
    /**
 * Ce test vérifie que la méthode {@code genererEnsembleHoraire}
 * retourne une erreur lorsque la liste de cours est vide.
 */
@Test
@DisplayName("genererEnsembleHoraire() échoue si la liste de cours est vide")
void testGenererEnsembleHoraireListeVide() {

    HoraireException ex = assertThrows(
        HoraireException.class,
        () -> service.genererEnsembleHoraire(List.of(), "A25")
    );

    assertEquals(
        "La liste des cours est vide ou inexistante.",
        ex.getMessage()
    );

    verifyNoInteractions(mockRepo);
}

/**
 * Ce test vérifie que la méthode {@code genererEnsembleHoraire}
 * retourne une erreur lorsqu’on fournit plus de 6 cours.
 */
@Test
@DisplayName("genererEnsembleHoraire() échoue si plus de 6 cours sont fournis")
void testGenererEnsembleHoraireTropDeCours() {

    List<String> ids = List.of(
        "IFT1000","IFT1001","IFT1002",
        "IFT1003","IFT1004","IFT1005","IFT1006"
    );

    HoraireException ex = assertThrows(
        HoraireException.class,
        () -> service.genererEnsembleHoraire(ids, "H26")
    );

    assertTrue(ex.getMessage().contains("plus de 6 cours"));
    verifyNoInteractions(mockRepo);
}

/**
 * Ce test vérifie que la méthode {@code genererEnsembleHoraire}
 * retourne une erreur lorsqu’un cours valide ne peut pas être récupéré
 * depuis le repository.
 */
@Test
@DisplayName("genererEnsembleHoraire() échoue si un cours ne peut pas être récupéré")
void testGenererEnsembleHoraireCoursNonRecupere() throws Exception {

    when(mockRepo.getAllCoursesId())
            .thenReturn(Optional.of(List.of("IFT2255")));

    when(mockRepo.getCourseBy("id", "IFT2255", "true", null))
            .thenReturn(Optional.empty());

    HoraireException ex = assertThrows(
        HoraireException.class,
        () -> service.genererEnsembleHoraire(
                List.of("IFT2255"), "A25"
        )
    );

    assertEquals(
        "Le cours IFT2255 n’a pas pu être récupéré.",
        ex.getMessage()
    );
}

/**
 * Ce test vérifie que la méthode {@code genererEnsembleHoraire}
 * ignore correctement les horaires d’examen (intra et final).
 */
@Test
@DisplayName("genererEnsembleHoraire() ignore les horaires d'intra et de final")
void testGenererEnsembleHoraireIgnoreExamens() throws Exception {

    when(mockRepo.getAllCoursesId())
            .thenReturn(Optional.of(List.of("IFT2255")));

    Cours c = new Cours();
    c.setId("IFT2255");

    Cours.Schedule s = new Cours.Schedule();
    s.setSemester("A25");

    Cours.Section sec = new Cours.Section();
    sec.setName("A");

    Cours.Volet voletIntra = new Cours.Volet();
    voletIntra.setName("Intra");

    Cours.Volet voletFinal = new Cours.Volet();
    voletFinal.setName("Final");

    sec.setVolets(List.of(voletIntra, voletFinal));
    s.setSections(List.of(sec));
    c.setSchedules(List.of(s));

    when(mockRepo.getCourseBy("id", "IFT2255", "true", null))
            .thenReturn(Optional.of(List.of(c)));

    var res = service.genererEnsembleHoraire(
            List.of("IFT2255"), "A25"
    );

    assertTrue(res.get("IFT2255").isEmpty());
}

/**
 * Ce test vérifie que la méthode {@code genererEnsembleHoraire}
 * élimine les activités horaires en double.
 */
@Test
@DisplayName("genererEnsembleHoraire() élimine les doublons d'activités")
void testGenererEnsembleHoraireSansDoublons() throws Exception {

    when(mockRepo.getAllCoursesId())
            .thenReturn(Optional.of(List.of("IFT2255")));

    Cours c = new Cours();
    c.setId("IFT2255");

    Cours.Schedule s = new Cours.Schedule();
    s.setSemester("A25");

    Cours.Section sec = new Cours.Section();
    sec.setName("A");

    Cours.Volet volet = new Cours.Volet();
    volet.setName("TH");

    Cours.Activity act1 = new Cours.Activity();
    act1.setDays(List.of("LUN"));
    act1.setStart_time("08:30");
    act1.setEnd_time("10:00");

    Cours.Activity act2 = new Cours.Activity();
    act2.setDays(List.of("LUN"));
    act2.setStart_time("08:30");
    act2.setEnd_time("10:00");

    volet.setActivities(List.of(act1, act2));
    sec.setVolets(List.of(volet));
    s.setSections(List.of(sec));
    c.setSchedules(List.of(s));

    when(mockRepo.getCourseBy("id", "IFT2255", "true", null))
            .thenReturn(Optional.of(List.of(c)));

    var res = service.genererEnsembleHoraire(
            List.of("IFT2255"), "A25"
    );

    var blocs =
            res.get("IFT2255")
               .get("TH")
               .get("A");

    assertEquals(1, blocs.size());
}

/**
 * Ce test vérifie que la méthode {@code genererEnsembleHoraire}
 * fonctionne correctement lorsqu’un cours valide est fourni.
 */
@Test
@DisplayName("genererEnsembleHoraire() fonctionne correctement avec un cours valide")
void testGenererEnsembleHoraireValide() throws Exception {

    when(mockRepo.getAllCoursesId())
            .thenReturn(Optional.of(List.of("IFT2255")));

    Cours c = new Cours();
    c.setId("IFT2255");

    Cours.Schedule s = new Cours.Schedule();
    s.setSemester("A25");

    Cours.Section sec = new Cours.Section();
    sec.setName("A");

    Cours.Volet volet = new Cours.Volet();
    volet.setName("TH");

    Cours.Activity act = new Cours.Activity();
    act.setDays(List.of("LUN"));
    act.setStart_time("08:30");
    act.setEnd_time("10:00");

    volet.setActivities(List.of(act));
    sec.setVolets(List.of(volet));
    s.setSections(List.of(sec));
    c.setSchedules(List.of(s));

    when(mockRepo.getCourseBy("id", "IFT2255", "true", null))
            .thenReturn(Optional.of(List.of(c)));

    var res = service.genererEnsembleHoraire(
            List.of("IFT2255"), "A25"
    );

    assertNotNull(res);
    assertTrue(res.containsKey("IFT2255"));

    assertTrue(res.get("IFT2255").containsKey("TH"));
    assertTrue(res.get("IFT2255").get("TH").containsKey("A"));

    var blocs =
            res.get("IFT2255")
               .get("TH")
               .get("A");

    assertEquals(1, blocs.size());
    assertEquals("[LUN]", blocs.get(0).get(0));
    assertEquals("08:30-10:00", blocs.get(0).get(1));
}

/**
 * Ce test vérifie que la méthode {@code appliquerChoix}
 * retourne un horaire valide lorsqu’un choix cohérent est fourni.
 */
@Test
@DisplayName("appliquerChoix() fonctionne correctement avec des choix valides")
void testAppliquerChoixValide() {

    var horaires = Map.of(
        "IFT2255", Map.of(
            "TH", Map.of(
                "A", List.of(
                    List.of("[LUN]", "08:30-10:00")
                )
            ),
            "TP", Map.of(
                "A01", List.of(
                    List.of("[MER]", "10:00-11:30")
                )
            )
        )
    );

    var choix = Map.of(
        "IFT2255", Map.of(
            "TH", "A",
            "TP", "A01"
        )
    );

    var res = service.appliquerChoix(horaires, choix);

    assertNotNull(res);
    assertTrue(res.horaire.containsKey("IFT2255"));
    assertEquals(2, res.horaire.get("IFT2255").size());
    assertTrue(res.conflits.isEmpty());
}

// ==================== Tests HTTP / Planifium API ====================

    /**
     * Test de la méthode fetchSchedule lorsque le cours est disponible.
     * Vérifie que la méthode retourne un Optional contenant les données du cours
     * pour le semestre spécifié.
     *
     * @throws Exception si une erreur survient lors du test
     */
    @Test
    void testFetchSchedule_CourseAvailable() throws Exception {
        String courseID = "IFT1025";
        String semester = "A24";
        String mockResponse = """
            [
                {
                    "semester": "A24",
                    "sections": [
                        {
                            "name": "A",
                            "capacity": "100",
                            "number_inscription": "80",
                            "teachers": ["Prof. Smith"],
                            "volets": []
                        }
                    ]
                }
            ]
            """;


        HttpServer server = startServer((path, query) -> mockResponse);
        try {
            Method fetchMeth = CoursService.class.getDeclaredMethod("fetchSchedule", String.class, String.class);
            fetchMeth.setAccessible(true);
            Optional<JsonNode> result = (Optional<JsonNode>) fetchMeth.invoke(service, courseID, semester);

            assertTrue(result.isPresent());
            assertEquals("A24", result.get().get("semester").asText());
        } finally {
            server.stop(0);
            System.clearProperty("planifium.base");
        }
    }

    /**
     * Test de la méthode fetchSchedule lorsque le cours n'est pas disponible.
     * Vérifie que la méthode retourne un Optional vide quand aucun cours
     * ne correspond aux critères de recherche.
     *
     * @throws Exception si une erreur survient lors du test
     */
    @Test
    void testFetchSchedule_CourseNotAvailable() throws Exception {
        String courseID = "IFT9999";
        String semester = "A24";
        String mockResponse = "[]";

        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getInputStream())
            .thenReturn(new ByteArrayInputStream(mockResponse.getBytes()));

        HttpServer server = startServer((path, query) -> mockResponse);
        try {
            Method fetchMeth = CoursService.class.getDeclaredMethod("fetchSchedule", String.class, String.class);
            fetchMeth.setAccessible(true);
            Optional<JsonNode> result = (Optional<JsonNode>) fetchMeth.invoke(service, courseID, semester);

            assertTrue(result.isEmpty());
        } finally {
            server.stop(0);
            System.clearProperty("planifium.base");
        }
    }

    /**
     * Test de la méthode fetchSchedule lors d'une IOException.
     * Vérifie que la méthode gère correctement les erreurs réseau
     * et retourne un Optional vide.
     *
     * @throws Exception si une erreur survient lors du test
     */
    @Test
    void testFetchSchedule_IOException() throws Exception {
        String courseID = "IFT1025";
        String semester = "A24";

        HttpServer server = startServer((path, query) -> "");
        // Stop the server to simulate network error
        server.stop(0);
        try {
            Method fetchMeth = CoursService.class.getDeclaredMethod("fetchSchedule", String.class, String.class);
            fetchMeth.setAccessible(true);
            Optional<JsonNode> result = (Optional<JsonNode>) fetchMeth.invoke(service, courseID, semester);

            assertTrue(result.isEmpty());
        } finally {
            System.clearProperty("planifium.base");
        }
    }

    // ==================== Tests pour isCourseAvailable ====================

    /**
     * Test de la méthode isCourseAvailable lorsque le cours est disponible.
     * Vérifie que la méthode retourne true quand un cours existe pour
     * le semestre spécifié.
     *
     * @throws Exception si une erreur survient lors du test
     */
    @Test
    void testIsCourseAvailable_ReturnsTrue() throws Exception {
        String courseID = "IFT1025";
        String semester = "A24";
        String mockResponse = """
            [
                {
                    "semester": "A24",
                    "sections": []
                }
            ]
            """;

        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getInputStream())
            .thenReturn(new ByteArrayInputStream(mockResponse.getBytes()));

        HttpServer server = startServer((path, query) -> mockResponse);
        try {

            Method method = CoursService.class.getDeclaredMethod("isCourseAvailable", String.class, String.class);
            method.setAccessible(true);
            boolean res = (boolean) method.invoke(service, courseID, semester);

            assertTrue(res);
        } finally {
            server.stop(0);
            System.clearProperty("planifium.base");
        }
    }

    /**
     * Test de la méthode isCourseAvailable lorsque le cours n'est pas disponible.
     * Vérifie que la méthode retourne false quand aucun cours ne correspond
     * aux critères.
     *
     * @throws Exception si une erreur survient lors du test
     */
    @Test
    void testIsCourseAvailable_ReturnsFalse() throws Exception {
        String courseID = "IFT9999";
        String semester = "A24";
        String mockResponse = "[]";

        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getInputStream())
            .thenReturn(new ByteArrayInputStream(mockResponse.getBytes()));

        HttpServer server = startServer((path, query) -> mockResponse);
        try {

            Method method = CoursService.class.getDeclaredMethod("isCourseAvailable", String.class, String.class);
            method.setAccessible(true);
            boolean res = (boolean) method.invoke(service, courseID, semester);

            assertFalse(res);
        } finally {
            server.stop(0);
            System.clearProperty("planifium.base");
        }
    }

    // ==================== Tests pour getCourseSchedule ====================

    /**
     * Test de la méthode getCourseSchedule avec un cours disponible.
     * Vérifie que la méthode retourne une liste formatée contenant les informations
     * d'horaire du cours (sections, professeurs, places disponibles, etc.).
     *
     * @throws Exception si une erreur survient lors du test
     */
    @Test
    void testGetCourseSchedule_Success() throws Exception {
        String courseID = "IFT1025";
        String semester = "A24";
        String mockResponse = """
            [
                {
                    "semester": "A24",
                    "sections": [
                        {
                            "name": "A",
                            "capacity": "100",
                            "number_inscription": "80",
                            "teachers": ["Prof. Smith", "Prof. Jones"],
                            "volets": [
                                {
                                    "name": "Cours",
                                    "activities": [
                                        {
                                            "days": ["Lu", "Me"],
                                            "start_time": "09:00",
                                            "end_time": "10:30",
                                            "start_date": "2024-09-01",
                                            "end_date": "2024-12-15",
                                            "room": "101",
                                            "pavillon_name": "André-Aisenstadt",
                                            "campus": "MIL",
                                            "mode": "P"
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
            """;

        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getInputStream())
            .thenReturn(new ByteArrayInputStream(mockResponse.getBytes()));

        HttpServer server = startServer((path, query) -> mockResponse);
        try {
            List<String> result = service.getCourseSchedule(courseID, semester);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.get(0).contains("Section : A"));
            assertTrue(result.get(0).contains("Prof. Smith"));
            assertTrue(result.get(0).contains("20")); // 100 - 80 = 20 places restantes
        } finally {
            server.stop(0);
            System.clearProperty("planifium.base");
        }
    }

    /**
     * Test de la méthode getCourseSchedule lorsque le cours n'est pas disponible.
     * Vérifie que la méthode retourne une liste vide quand aucun horaire
     * n'est trouvé pour le cours.
     *
     * @throws Exception si une erreur survient lors du test
     */
    @Test
    void testGetCourseSchedule_CourseNotAvailable() throws Exception {
        String courseID = "IFT9999";
        String semester = "A24";
        String mockResponse = "[]";

        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getInputStream())
            .thenReturn(new ByteArrayInputStream(mockResponse.getBytes()));

        HttpServer server = startServer((path, query) -> mockResponse);
        try {
            List<String> result = service.getCourseSchedule(courseID, semester);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        } finally {
            server.stop(0);
            System.clearProperty("planifium.base");
        }
    }

    // ==================== Tests pour getCoursesForAProgram ====================

    /**
     * Test de la méthode getCoursesForAProgram avec un programme valide.
     * Vérifie que la méthode retourne correctement la liste de tous les cours
     * associés à un programme donné.
     *
     * @throws Exception si une erreur survient lors du test
     */
    @Test
    void testGetCoursesForAProgram_Success() throws Exception {
        String programID = "117510";
        String mockResponse = """
            [
                {
                    "id": "117510",
                    "courses": ["IFT1015", "IFT1025", "IFT2035"]
                }
            ]
            """;

        HttpServer server = startServer((path, query) -> mockResponse);
        try {
                List<String> result = service.getCoursesForAProgram(programID);

                assertNotNull(result);
                assertEquals(3, result.size());
                assertTrue(result.contains("IFT1015"));
                assertTrue(result.contains("IFT1025"));
                assertTrue(result.contains("IFT2035"));
            } finally {
                server.stop(0);
                System.clearProperty("planifium.base");
            }
    }

    /**
     * Test de la méthode getCoursesForAProgram lorsque le programme n'a pas de cours.
     * Vérifie que la méthode retourne une liste vide quand aucun cours n'est
     * associé au programme.
     *
     * @throws Exception si une erreur survient lors du test
     */
    @Test
    void testGetCoursesForAProgram_NoCourses() throws Exception {
        String programID = "INVALID";
        String mockResponse = """
            [
                {
                    "id": "INVALID"
                }
            ]
            """;

        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getInputStream())
            .thenReturn(new ByteArrayInputStream(mockResponse.getBytes()));

        HttpServer server = startServer((path, query) -> mockResponse);
        try {
            List<String> result = service.getCoursesForAProgram(programID);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        } finally {
            server.stop(0);
            System.clearProperty("planifium.base");
        }
    }

    /**
     * Test de la méthode getCoursesForAProgram lors d'une IOException.
     * Vérifie que la méthode gère correctement les erreurs réseau
     * et retourne une liste vide au lieu de lancer une exception.
     *
     * @throws Exception si une erreur survient lors du test
     */
    @Test
    void testGetCoursesForAProgram_IOException() throws Exception {
        String programID = "117510";

        HttpServer server = startServer((p,q) -> "");
        // Stop immediately to simulate network error (connection refused)
        int port = server.getAddress().getPort();
        server.stop(0);
        System.setProperty("planifium.base", "http://172.17.0.1:" + port);

        try {
            List<String> result = service.getCoursesForAProgram(programID);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        } finally {
            System.clearProperty("planifium.base");
        }
    }

    // ==================== Tests pour getCourseBySemester ====================

    /**
     * Test de la méthode getCourseBySemester avec des cours disponibles.
     * Vérifie que la méthode filtre correctement les cours d'un programme
     * pour ne retourner que ceux offerts durant le semestre spécifié.
     *
     * @throws Exception si une erreur survient lors du test
     */
    @Test
    void testGetCourseBySemester_Success() throws Exception {
        String programID = "117510";
        String semester = "A24";
        String programResponse = """
            [
                {
                    "id": "117510",
                    "courses": ["IFT1015", "IFT1025", "IFT2035"]
                }
            ]
            """;
        String scheduleResponse1 = """
            [
                {
                    "semester": "A24",
                    "sections": []
                }
            ]
            """;
        String scheduleResponse2 = "[]";

        HttpURLConnection mockConnection1 = mock(HttpURLConnection.class);
        HttpURLConnection mockConnection2 = mock(HttpURLConnection.class);
        HttpURLConnection mockConnection3 = mock(HttpURLConnection.class);
        HttpURLConnection mockConnection4 = mock(HttpURLConnection.class);

        when(mockConnection1.getInputStream())
            .thenReturn(new ByteArrayInputStream(programResponse.getBytes()));
        when(mockConnection2.getInputStream())
            .thenReturn(new ByteArrayInputStream(scheduleResponse1.getBytes()));
        when(mockConnection3.getInputStream())
            .thenReturn(new ByteArrayInputStream(scheduleResponse1.getBytes()));
        when(mockConnection4.getInputStream())
            .thenReturn(new ByteArrayInputStream(scheduleResponse2.getBytes()));

        HttpServer server = startServer((path, query) -> {
            if (path.contains("/programs")) return programResponse;
            if (path.contains("/schedules") && query != null && query.contains("IFT1015")) return scheduleResponse1;
            if (path.contains("/schedules") && query != null && query.contains("IFT1025")) return scheduleResponse1;
            if (path.contains("/schedules") && query != null && query.contains("IFT2035")) return scheduleResponse2;
            return "[]";
        });

        try {
            List<String> result = service.getCourseBySemester(semester, programID);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.contains("IFT1015"));
            assertTrue(result.contains("IFT1025"));
        } finally {
            server.stop(0);
            System.clearProperty("planifium.base");
        }
    }

    /**
     * Test de la méthode getCourseBySemester lorsqu' aucun cours n'est disponible.
     * Vérifie que la méthode retourne une liste vide quand aucun cours du programme
     * n'est offert durant le semestre spécifié.
     *
     * @throws Exception si une erreur survient lors du test
     */
    @Test
    void testGetCourseBySemester_NoCoursesAvailable() throws Exception {
        String programID = "117510";
        String semester = "E24";
        String programResponse = """
            [
                {
                    "id": "117510",
                    "courses": ["IFT1015"]
                }
            ]
            """;
        String scheduleResponse = "[]";

        HttpURLConnection mockConnection1 = mock(HttpURLConnection.class);
        HttpURLConnection mockConnection2 = mock(HttpURLConnection.class);

        when(mockConnection1.getInputStream())
            .thenReturn(new ByteArrayInputStream(programResponse.getBytes()));
        when(mockConnection2.getInputStream())
            .thenReturn(new ByteArrayInputStream(scheduleResponse.getBytes()));

        HttpServer server = startServer((path, query) -> {
            if (path.contains("/programs")) return programResponse;
            if (path.contains("/schedules")) return scheduleResponse;
            return "[]";
        });

        try {
            List<String> result = service.getCourseBySemester(semester, programID);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        } finally {
            server.stop(0);
            System.clearProperty("planifium.base");
        }
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