package org.projet.service;

import org.projet.model.Cours;
import org.projet.repository.CoursRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import java.util.function.BiFunction;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        service.setCoursRepository(mockRepo);
        service.cacheCoursIds.clear();
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
        System.setProperty("planifium.base", "http://localhost:" + port);
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
        System.setProperty("planifium.base", "http://localhost:" + port);

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
}