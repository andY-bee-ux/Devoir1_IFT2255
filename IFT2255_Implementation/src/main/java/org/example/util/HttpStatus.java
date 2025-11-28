package org.example.util;

import java.util.Map;

public class HttpStatus {

    // Ce code provient du repertoire GitHub : ift2255-template-javalin fournit pour ce projet.

    // REASONS contient un ensemble de cle-valeur, avec la clé étant le code retourné lors de l'appel d'une requête et
    // la valeur étant le message retourné lors de l'appel d'une requête.
    private static final Map<Integer, String> REASONS = Map.ofEntries(
            Map.entry(200, "OK"),
            Map.entry(201, "Created"),
            Map.entry(400, "Bad Request"),
            Map.entry(401, "Unauthorized"),
            Map.entry(403, "Forbidden"),
            Map.entry(404, "Not Found"),
            Map.entry(500, "Internal Server Error")
    );

    /**
     *  Cette methode retourne le message associé à la cle passe en parametre ou le message "UNKNOWN" si aucun message
     *  n'existe pour le code fournis.
     * @param code Code retourné lors de l'appel d'une requête.
     * @return Le message associee a chaque code ou le message "UNKNOWN" si le code n'est pas enregistrer.
     **/
    public static String reasonPhrase(int code) {
        return REASONS.getOrDefault(code, "Unknown");
    }
}
