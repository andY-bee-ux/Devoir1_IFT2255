package org.example.util;

import java.util.Map;

public class ResponseUtil {
    // Ce code provient du repertoire GitHub : ift2255-template-javalin fournit pour ce projet.
    public static Map<String, String> formatError(String errorMessage) {
        return Map.of("error", errorMessage);
    }
}
