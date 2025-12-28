package org.projet.controller;

import io.javalin.http.Context;
import org.projet.service.AvisService;
import org.projet.model.Avis;

import java.util.List;

public class AvisController {

    private final AvisService avisService = AvisService.getInstance(); // singleton

    /**
     * Cette méthode permet de traiter la soumission d'avis grâce au bot Discord.
     * @param ctx contient la requête + notre réponse
     */
    public void soumettreAvis(Context ctx) {
        try {
            RequeteAvis req = ctx.bodyAsClass(RequeteAvis.class);
       // La méthode enregistrerAvis stocke ledit avis sur notre plateforme.
            avisService.enregistrerAvis(
                    req.sigleCours.toUpperCase(),
                    req.professeur,
                    req.noteDifficulte,
                    req.noteCharge,
                    req.commentaire
            );
        // Si tout se passe bien, on affiche un message de succès.
            ctx.status(200).result("Avis enregistré avec succès");
        // Le IllegalArgumentException se produit lorsque l'une des entrées est incorrecte.
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("L'entrée est incorrecte. Veuillez reessayer.");
         // Tout autre exception vient du côté Serveur.
        } catch (Exception e) {
            ctx.status(500).result("Erreur serveur : " + e.getMessage());
        }
    }

    /**
    * Cette méthode permet de gérer la requête relative à la récupération de tous les avis
    * enregistrés sur la plateforme.
    */
    
    public void getAllAvis(Context ctx){
        try{
            List<Avis> avis = avisService.getAllAvis();
            // Il est possible qu'il n'y ait pas d'avis pour ce cours.
            if(avis == null || avis.isEmpty()) {
                ctx.status(400).result("Erreur : avis inexistant");
            }

            ctx.status(200).json(avis);

        }
        // Le IllegalArgumentException se produit lorsque le sigle de cours est incorrect est incorrecte.
        catch(IllegalArgumentException e) {
            ctx.status(400).result("Erreur : sigle de cours invalide");
        }
        // Toute autre exception vient du côté serveur.
        catch(Exception e) {
            ctx.status(500).result("Erreur : " + e.getMessage());
        }


    }

    /**
     * Cette méthode permet de gérer les requêtes utilisateurs relatives à la récupération d'avis pour un cours.
     * @param ctx  la requête + notre réponse.
     */

    public void getAvisParCours(Context ctx) {
        // la route contient le sigle donc on le récupère
        String sigle = ctx.pathParam("sigle").toUpperCase();
         // vérification minimale.
        if (sigle == null || sigle.isBlank()) {
            ctx.status(400).result("Sigle du cours manquant");
            return;
        }

        // On parcourt notre local "database" pour trouver les avis relatifs au cours en utilisant avisService.
        try{
            List<Avis> avis = avisService.getAvisParCours(sigle);
            // Il est possible qu'il n'y ait pas d'avis pour ce cours.
            if(avis == null || avis.isEmpty()) {
                ctx.status(400).result("Erreur : avis inexistant");
            }

            ctx.status(200).json(avis);

        }
        // Le IllegalArgumentException se produit lorsque le sigle de cours est incorrect est incorrecte.
        catch(IllegalArgumentException e) {
            ctx.status(400).result("Erreur : sigle de cours invalide");
        }
        // Toute autre exception vient du côté serveur.
        catch(Exception e) {
            ctx.status(500).result("Erreur : " + e.getMessage());
        }

    }

    /**
    * Cette classe interne permet de parser le corps de la requête pour les avis.
    */
    public static class RequeteAvis {
        public String sigleCours;
        public String professeur;
        public int noteDifficulte;
        public int noteCharge;
        public String commentaire;
    }
}
