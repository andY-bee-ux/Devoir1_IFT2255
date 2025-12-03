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

Nous avons réalisé des tests unitaires avec JUnit, et en suivant les principes du TD: les tests unitaires ont été écrit avant l'implémentation des modules concernés, puis ont été refactor afin de les adapter.

Pour tester notre API, nous avons utilisé Postman.

Nous avons également utilisé Mockito afin de simuler les dépendances d'un module lors de son test.


## Critères d'évaluation
   Ces derniers seront mis à jour lors de la dernière phase.


## Résultats des tests

L'oracle des tests est donné ci-dessous:

| Module | Entité | Entrée | Sortie attendue |Type | Description |
| comparerCours | Controller | { "cours" : ["IFT1025", "IFT2255"], "criteres" : ["name", "credits"] } | Liste de cours comparés | Succès | Si les critères ainsi que les ids des cours sont valides, on s'attend à ce que la réponse ait un statut de succès (200), et à s'attend à avoir une liste. |

| comparerCours | Controller | { "cours" : ["IFT1025", "IFT2255"], "criteres" : ["nom", "credits"] } | message "Critère inconnu" | Échec | Si un critère est invalide, on s'attend à recevoir un message d'erreur. Le contrôleur gère l'erreur sans rejeter. |

| comparerCours | Controller | { "cours" : ["INVALID1", "INVALID2"], "criteres" : ["name", "credits"] } | Erreur 400 | Echec | Si les ids des cours sont invalides, on s'attend à avoir une erreur de requête 400. |

| comparerCours | Controller | { "cours" : ["IFT1025", "IFT2255"], "criteres" : ["id", "name", "description", "credits", "scheduledSemester", "schedules", "prerequisite_courses", "equivalent_courses", "concomitant_courses", "udemWebsite", "requirement_text", "available_terms", "available_periods"] } | Liste de cours comparés | Succès | Si les critères ainsi que les ids des cours sont valides, on s'attend à ce que la réponse ait un statut de succès (200), et à s'attend à avoir une liste. |

| rechercherCours | Controller | param : "id", valeur : "IFT1025", includeSchedule = "false", semester = null | Un ou plusieurs cours correspondant à l'ID "IFT1025" | Succès | Si les parametres sont valides et si l'identifiant est valide, on s'attend à ce que la réponse ait un statut de succès, et s'attend à avoir un cours |

| rechercherCours | Controller | param = "name", valeur = "Algorithmic", includeSchedule = "false", semester = null | Un ou plusieurs cours dont le nom contient "Algorithmic" | Succès | Recherche de cours par nom. S'assure que la recherche textuelle sur la nom fonctionne, ne provoque pas d'erreur côté contrôleur. et s'attend à avoir un cours |

| rechercherCours | Controller | param = "description", valeur = "fundamentals", includeSchedule = "false", semester = null | Un ou plusieurs cours dont la description contient "fundamentals" | Succès | Recherche de cours par description. S'assure que la recherche textuelle sur la description fonctionne, ne provoque pas d'erreur côté contrôleur. et s'attend à avoir un cours |

| rechercherCours | Controller | param = "invalid_param", valeur = "value", includeSchedule = "false", semester = null | Soit une liste vide, soit un message d'erreur "Paramètre invalide" | Echec | Vérifie la gestion d'un paramètre de recherche invalide. Le contrôleur doit répondre sans planter (au moins un statut est renvoyé pour signaler l'erreur ou l'absence de résultat). |

| rechercherCours | Controller | param = "id", valeur = "IFT1025", includeSchedule = "true", semester = "FALL" | Le cours "IFT1025" avec les horaires filtrés pour le semestre "FALL" | Echec | Vérifie que le paramètre includeSchedule et semester sont correctement pris en compte par le contrôleur. |

| checkEligibility | Controller | idCours = "IFT2255", listeCours = List.of("IFT1025", "IFT1030") | Un objet JSON contenant :"eligible": true si l'étudiant a suivi tous les prérequis (IFT1025, IFT1030 pour IFT2255), "message": "L'étudiant est éligible au cours" ou similaire | Succès | Vérifie que la méthode d'éligibilité retourne un JSON indiquant si l'étudiant est éligible au cours donné en fonction des prérequis fournis. |

| checkEligibility | Controller | idCours = "INVALID_COURSE", listeCours = List.of("IFT1025") | Un objet JSON contenant :"eligible": false, "message": "Le cours INVALID_COURSE n'existe pas" ou similaire | Erreur | Teste le comportement lorsque l'identifiant du cours n'existe pas. Le contrôleur doit renvoyer une réponse JSON gérant le cas d'erreur sans lever d'exception non gérée. |

| checkEligibility | Controller | idCours = "IFT2255", listeCours = List.of() | Un objet JSON contenant :"eligible": false, "message": "L'étudiant ne possède pas tous les prérequis" ou similaire | Succes | Cas où la liste des prérequis est vide : la vérification doit retourner une réponse JSON correcte (généralement éligible ou non selon l'implémentation) sans erreur. |

| comparerCombinaisonCours | CoursController | listeCours = List.of(List.of("IFT1025", "IFT1015"), List.of("IFT2255", "IFT2035")), session = "FALL" | Un tableau ou objet contenant la comparaison des deux combinaisons | Erreur | Comparaison de plusieurs combinaisons de cours pour une session donnée. Vérifie que le contrôleur calcule et renvoie des résultats (statut et JSON) pour des combinaisons valides. |

| comparerCombinaisonCours | CoursController | listeCours = List.of(List.of("INVALID1", "INVALID2")), session = "FALL" | Les identifiants invalides (INVALID1, INVALID2) sont rejetés | Succès | Vérifie que la méthode retourne un statut 400 et un message d'erreur lorsque la combinaison contient des identifiants de cours invalides. |

| comparerCombinaisonCours | CoursController | listeCours = List.of(List.of("IFT1025", "IFT1030", "IFT2255")), List.of("IFT2255", "IFT2000")), session = "WINTER" | La comparaison pour une seule combinaison (IFT1025 + IFT1030 + IFT2255) | Succès | Cas avec une seule combinaison fournie : s'assure que le contrôleur peut traiter une liste contenant une seule combinaison sans erreur. |

| getAvis | CoursController | Cours.setId("IFT1025"), result = controller.getAvis(cours) | Une liste vide (aucun avis pour le cours IFT1025 de test) | Succès | Vérifie que la méthode getAvis retourne une liste (éventuellement vide) pour un cours donné. Ici on teste le cas où aucun avis n'est présent et on s'attend à une liste vide. |

| getAvis | CoursController | cours = new Cours(), result = controller.getAvis(cours) | Peut être vide ou contenir des avis | Succès | S'assure que getAvis ne retourne jamais null même si aucun avis n'existe pour le cours fourni. |

| comparerCombinaisonCours | CoursService | [[Cours IFT1025 = { id : ANDYCHLOE, credits : 3} ]] | doit retourner null | Succès | S'assure que comparerCombinaisonCours retourne null en cas d'id invalide |

| comparerCombinaisonCours | CoursService | [[Cours IFT1025 = { id : IFT1025, credits : 3, Semester : H2025, Section : A, Volet : Activity ( Lun 8h-10h ) } ], [Cours IFT2255 = { id : IFT2255, credits : 3, Semester : H2025, Section : B, Volet : Activity ( Lun 9h-11h ) } ]] | Un tableau tel que la partie du résultat associée au schedules contienne le mot clé CONFLIT indiquant la présence d'un conflit | Succès | S'assure que comparerCombinaisonCours détecte bien les conflits |

| comparerCombinaisonCours | CoursService | [[Cours IFT1025 = { id : IFT1025, credits : 3} ]] | doit uniquement contenir le cours IFT1025 | Succès | S'assure que comparerCombinaisonCours retourne bien un tableau avec les informations spécifiées |

| getCourseBy | CoursRepository | id, IFT1025 IFT1025 | doit retourner l'id 1025 | Invariance | S'assure que l'id associé au Cours retourné par cette méthode corresponde bien à l'id passé lors de la recherche |

| validateIdCours | Service | "IFT1025" ; repo = ["IFT1025","IFT2255"] | true | Succès | L’ID est valide : la méthode privée validateIdCours doit retourner true. |

| validateIdCours | Service | "IAMTIDJANI02" ; repo = ["IFT1025","IFT2255"] | false | Succès | L’ID n’existe pas : la méthode doit retourner false. |

| checkEligibility | Service | id = "TJ5035" ; complétés = ["IFT1025"] ; repo = ["IFT1025","IFT2255"] | "L'id du cours est invalide" | Échec | L’ID demandé n'existe pas : la méthode doit retourner ce message et ne pas appeler le repository. |

| checkEligibility | Service | id = "IFT2255" ; complétés = ["TJOFF3334"] ; repo = ["IFT2255"] | "Il y a des cours complétés invalides" | Échec | Un cours complété n'est pas valide : la méthode doit stopper et retourner l’erreur. |

| checkEligibility | Service | id = "IFT2255" ; complétés = ["IFT1025"] ; JSON repo = { eligible: true } | "Vous êtes éligible à ce cours!" | Succès | Le cours est éligible : la méthode retourne le message positif. |

| checkEligibility | Service | id = "IFT2255" ; complétés = ["IFT1000"] ; JSON repo = { eligible: false, missing_prerequisites: ["IFT1025"] } | "Vous n'êtes pas éligible à ce cours. Il vous manque les prerequis suivants : IFT1025" | Succès | Le cours n’est pas éligible : la méthode doit afficher les prérequis manquants. |

| checkEligibility | Service | id = "IFT2255" ; complétés = ["IFT1025"] ; repo lance une exception "Erreur Planifium" | "Une erreur est survenue lors de la vérification d'éligibilité." | Échec | Le service doit capturer les exceptions et retourner un message propre. |

| checkEligibility | Service | id = "IFT2255" ; complétés = [] ; repo retourne "<html>…</html>" (JSON corrompu) | "Une erreur est survenue lors de la vérification d'éligibilité." | Échec | Réponse JSON invalide : la méthode doit retourner un message générique sans planter. |

| comparerCombinaisonCours | Service | combinaisons = [["IFT1025","DHGFH56"]] ; session = "A24" ; repo connaît seulement "IFT1025" | null | Échec | Une combinaison contient un ID invalide : la méthode doit retourner null. |

| comparerCombinaisonCours | Service | combinaisons = [["IFT1025","IFT2255"]] ; session = "A25" ; repo retourne les 2 cours valides avec 3 crédits chacun | Liste contenant 1 ligne : ["Combinaison …", "IFT1025, IFT2255", "6"] | Succès | Les deux cours existent : la méthode produit la ligne indiquant les cours et le total des crédits. |

| getAllCoursesId | Repository | — (appel direct) | Optional présent + liste non vide | Succès | La méthode doit retourner un Optional contenant une liste d’IDs de cours existants. |

| getAllCoursesId | Repository | — (appel direct) | La liste contient "IFT2255" | Succès | Vérifie qu’un cours connu (IFT2255) est bien présent dans la base. |

| getAllCoursesId | Repository | — (appel direct) | Aucun doublon dans la liste | Succès | Confirme que la source de données ne renvoie pas d’IDs dupliqués. |

| getCourseBy(id) | Repository | champ = "id" ; valeur = "IFT1025" | Optional présent contenant exactement 1 cours | Succès | La recherche par ID doit retourner un unique objet Cours correspondant. |

| getCourseBy(id) | Repository | champ = "id" ; valeur = "IFT1025" | Le cours trouvé possède le nom "Programmation 2" | Succès | Vérifie la cohérence des données du cours IFT1025. |

| getCourseBy(id) | Repository | champ = "id" ; valeur = "TIDJANI45" | Optional.empty() | Échec | Un ID inexistant doit retourner un Optional vide. |

| getCourseBy(name) | Repository | champ = "name" ; mot-clé = "Programmation" ; ignoreCase = "false" | Optional présent + liste non vide + premier élément contenant "Programmation" | Succès | La recherche par mot-clé doit retourner au moins un cours dont le nom contient le terme. |

| getCourseEligibility | Repository | id = "IFT2255" ; complétés = ["IFT1025"] (POST vers API Planifium) | Chaîne JSON non nulle contenant le champ "eligible" | Succès | Teste la communication réelle avec l'API Planifium et la validité du JSON retourné. |

<!--
- Résumé qualitatif :
  - Comportement attendu obtenu
  - Bonne robustesse générale

- Résumé quantitatif :
  - 85 % de couverture de code
  - Temps de réponse moyen : 1.2s

## Évaluation du système

Cette section sera mise à jour lors de la dernière phase du projet. -->
