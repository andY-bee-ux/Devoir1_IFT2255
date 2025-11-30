package org.example;

import io.javalin.Javalin;

import org.example.config.Routes;

public class Main {

    public static void main(String[] args) {
        // Crée une instance de Javalin avec une configuration personnalisée
        // Ici, on définit le type de contenu par défaut des réponses HTTP en JSON
        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
        });
        int port = 7070; // Vous pouvez choisir n'importe quel port disponible.

        // Enregistre toutes les routes de l'application
        Routes.registre(app);
        
        app.start(port);        // post car on veut pouvoir inclure du body à la requête.
        System.out.println("Serveur démarré sur le port " + port);
    }
}
