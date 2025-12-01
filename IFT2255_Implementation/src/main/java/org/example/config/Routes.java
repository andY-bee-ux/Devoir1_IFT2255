package org.example.config;

import io.javalin.Javalin;
import org.example.controller.CoursController;
import org.example.controller.UserController;
import org.example.model.Cours;
import org.example.service.CoursService;
import org.example.service.UserService;
import org.example.util.HttpClientApi;

public class Routes {
    // Ce code provient du repertoire GitHub : ift2255-template-javalin fournit pour ce projet.

    /**
     * Cette methode permet d'initialiser les registres pour nos entites, ses registres contiennent les methodes GET,
     * POST, PUT et DELETE.
     * @param app Il s'agit d'une instanciation du serveur Javalin.
     **/
    public static void registre(Javalin app){
        registreUserRoutes(app);
        resgistreCoursRoutes(app);
        registreAvisRoutes(app);
        registreResultatRoutes(app);
    }

    /**
     *  Cette methode contient les methodes  GET, POST, PUT et DELETE pour l'entité USER.
     * @param app Il s'agit d'une instanciation du serveur Javalin.
     **/
    private static void registreUserRoutes(Javalin app){}

    /**
     *  Cette methode contient les methodes  GET, POST, PUT et DELETE pour l'entité COURS.
     * @param app Il s'agit d'une instanciation du serveur Javalin.
     **/
    private static void resgistreCoursRoutes(Javalin app){
        CoursService coursService = new CoursService(new HttpClientApi());
        CoursController coursController = new CoursController(coursService);

        app.get("/courses", coursController :: getAllCourses);
        app.get("/courses/{id}", coursController :: getCourseById);
    }

    /**
     *  Cette methode contient les methodes  GET, POST, PUT et DELETE pour l'entité AVIS.
     * @param app Il s'agit d'une instanciation du serveur Javalin.
     **/
    private static void registreAvisRoutes(Javalin app){}

    /**
     *  Cette methode contient les methodes  GET, POST, PUT et DELETE pour l'entité RESULTATS.
     * @param app Il s'agit d'une instanciation du serveur Javalin.
     **/
    private static void registreResultatRoutes(Javalin app){}
}