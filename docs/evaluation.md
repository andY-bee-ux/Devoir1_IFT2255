---
title: Évaluation et tests
---

<style>
    @media screen and (min-width: 76em) {
        .md-sidebar--primary {
            display: none !important;
        }
    }
</style>

# Tests et évaluation

## Plan de test

- - Types de tests réalisés :
  - Tests unitaires
  - Tests d’intégration
  - Tests manuels
- Outils utilisés : Pytest, Postman, Selenium, etc.

## Critères d'évaluation

- Présenter les critères utilisés pour évaluer le système.

L'oracle des tests est donné ci-dessous:

| Module | Entité | Entrée | Sortie attendue | Type | Description |
|----|-----|---------------------|-------------| ------ | ---- |
| comparerCours | Controller | { "cours" : ["IFT1025", "IFT2255"], "criteres" : ["name", "credits"]} | Liste de cours comparés| Succès| Si les critères ainsi que les ids des cours sont valides, on s'attend à ce que la réponse ait un statut de succès (200), et à s'attend à avoir une liste.|

| comparerCours | Controller | { "cours" : ["IFT1025", "IFT2255"], "criteres" : ["nom", "credits"]} | message "Critère inconnu"| Échec| Si un critère est invalide, on s'attend à recevoir un message d'erreur.|

| comparerCours | Controller | { "cours" : ["INVALID1", "INVALID2"], "criteres" : ["name", "credits"]} | Erreur 400| Echec| Si les ids des cours sont invalides, on s'attend à avoir une erreur de requête 400.|

| comparerCours | Controller | { "cours" : ["IFT1025", "IFT2255"], "criteres" : ["id", "name", "description", "credits", "scheduledSemester",
                "schedules", "prerequisite_courses", "equivalent_courses",
                "concomitant_courses", "udemWebsite", "requirement_text",
                "available_terms", "available_periods"]} | Liste de cours comparés| Succès| Si les critères ainsi que les ids des cours sont valides, on s'attend à ce que la réponse ait un statut de succès (200), et à s'attend à avoir une liste.|
| getCourseBy | Repository | param : "name", value : "programmation" | liste de cours(optionnel) non vide | Succes | Test d'intégration : Vérifier que la recherche par nom communique correctement avec l'API externe et retourne une liste. |
| getCourseEligibility | courseId : "IFT2255", completed : ["IFT1025"] | Chainr Json valide contenant eligible | Succes | Test d'intégration : Vérifier que la requête POST vers l'API Planifium fonctionne et retourne un JSON structuré. |
| checkEligibity | Service | id: "IFT2255", Réponse Repo simulée : HTML invalide | Message : "Une erreur est survenue..." | Echec | Test Unitaire : Vérifier que le service capture l'exception et ne plante pas (crash) si l'API externe renvoie des données corrompues. |

- Résumé qualitatif :
  - Comportement attendu obtenu
  - Bonne robustesse générale

- Résumé quantitatif :
  - 85 % de couverture de code
  - Temps de réponse moyen : 1.2s

## Évaluation du système

Cette section sera mise à jour lors de la dernière phase du projet.
