# Projet pour le cours IFT2255

# Description du projet 

Ce projet vise à concevoir une **plateforme web intelligente**, accessible via une **API REST**, 
destinée aux étudiants de l’Université de Montréal et plus particulièrement du **DIRO** 
afin de les aider à faire des **choix de cours éclairés**.

La plateforme combinera :

- des **données officielles** (résultats académiques, informations provenant de l’API Planifium),
- et des **données collectées auprès des étudiants** (forums, Discord, etc).

L’objectif est d’offrir aux étudiants une **vue centralisée**, **fiable et personnalisée** des cours, leur permettant de :

- comparer plusieurs cours selon la charge de travail, la difficulté perçue et la compatibilité avec leur profil ;
- consulter des **tableaux de bord interactifs** par cours ;
- **personnaliser l’affichage** selon leur profil académique et leurs préférences ;
- **interroger les données** de manière dynamique et intuitive.

# Structure du projet

Le répertoire est organisé comme suit :

- **.history** :
  Ce dossier fait partie des éléments importés à partir du modèle MkDocs fourni pour ce devoir.

- **.idea** :
  Ce dossier fait partie des éléments importés à partir du modèle MkDocs fourni pour ce devoir.

- **diagrammes** :
  Ce dossier contiendra les differents diagrammes que nous aurons à realiser dans le cadre de notre projet. Le dossier contiendra les images de nos differents diagrammes.

- **docs** : 
  Ce dossier contient tous les fichiers Markdown du site pour notre rapport construit avec [MkDocs](https://www.mkdocs.org/) et le thème [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/).

  - **Pour executer le rapport, veuillez suivre les instructions plus bas dans la section **EXECUTION DU RAPPORT MKDOCS** .**

- **IFT2255_Implementation** :
  Ce dossier contient notre implementation pour le projet via une API-REST avec Javalin. La structure est inspiré du template fournis par l'enseignant et les auxiliaires.
  La structure est organisée suivant une architecture MVC (Model–View–Controller). On aura la structure suivante :

  - **src** :
    - **main** :
      - **java** :
        - **Model (`model/`)** : Représentation des entités du domaine (ex. User, Course).
        - **Controller (`controller/`)** : Gestion des requêtes HTTP et appels au service.
        - **Service (`service/`)** : Logique métier central.
        - **Util (`util/`)** : Fonctions utilitaires réutilisables (validation, réponses, etc.).
        - **Config (`config/`)** : Configuration du serveur et définition des routes.
        - **`Main.java`** : Point d’entrée du serveur (initialise Javalin et enregistre les routes).
      - **resources** :
        Ce dossier contient les fichiers JSON utilisés pour stocker de manière permanente nos données.
    - **test** :
      - **java** :
        - **Model** : Contient les tests pour les classes du fichier **`main/java/org/example/model`**.
        - **Controller** : Contient les tests pour les classes du fichier **`main/java/org/example/controller`**.
        - **Service** : Contient les tests pour les classes du fichier **`main/java/org/example/service`**.
        - **Util** : Contient les tests pour les classes du fichier **`main/java/org/example/util`**.
        - **Config** : Contient les tests pour les classes du fichier **`main/java/org/example/config`**.
    - **pom.xml** : 
      Fichier contenant les dependances Maven.

- **.gitignore** :
  Spécifie quelles fichiers sont ignorer par git.

- **mkdocs.yml** :
  Ce fichier contient la configuration de MkDocs.

- **Pipfile** :
  Cet élément a été importé à l’aide du modèle MkDocs fourni pour ce devoir.

- **requirements** :
  Ce fichier contient les dépendances Python.


Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

### Ressources utiles

- Documentation officielle MkDocs
- Thème Material for MkDocs

# Documentation pour l'API REST développée avec Javalin
## Routes
#### POST /cours/comparer
Compare plusieurs cours selon des critères.

**Format pour le body de la requête:**
{
  "cours": ["idCours1", "idCours2",...],
  "criteres": ["critere1", "critere2",...]
}

**Exemple de Body JSON attendu :**
{
  "cours": ["IFT1025", "IFT2255"],
  "criteres": ["name", "credits"]
}

**Exemple de réponse JSON**: ( status 200)
[
  ["IFT1025","Introduction à l'informatique","3"],
  ["IFT2255","Structures de données","3"]
]

**Exemple de réponse JSON ( status 400)**:
La comparaison n'a pas pu être effectuée. Vérifiez le format des critères de comparaison et celui des ids de Cours.

**Liste de critères**:
Lors de l’appel à `/cours/comparer`, les **critères suivants** sont à considérer :

| Critère | Description |
|----------|------------|
| `id` | Identifiant du cours |
| `name` | Nom complet du cours |
| `description` | Description détaillée |
| `credits` | Nombre de crédits |
| `scheduledSemester` | Trimestre(s) où le cours est offert |
| `schedules` | Horaires du cours |
| `prerequisite_courses` | Cours prérequis |
| `equivalent_courses` | Cours équivalents |
| `concomitant_courses` | Cours concomitants |
| `udemWebsite` | Lien vers le site officiel UdeM |
| `requirement_text` | Exigences spécifiques |
| `available_terms` | Termes disponibles |
| `available_periods` | Périodes disponibles |

#### POST /cours/comparer/combinaison
Compare des combinaisons de cours.

**Format pour le body de la requête:**
{
  "listeCours": [["idCours1", "idCours2",...],["idCours1","idCours2",...],...]
  "session" : "A24"
}

**Exemple de Body JSON attendu :**
{
    "listeCours": [["IFT1015", "IFT1025"], ["IFT1227"]],
    "session": "A24" 
}

ou encore

{
    "listeCours": [["IFT1015", "IFT1025"], ["IFT1227"]],
    "session": "" 
}

**Exemple de réponse JSON**: ( status 200)
[
    [
        "Combinaison 1",
        "Cours=[IFT1015, IFT1025]",
        "Crédits=6",
        "Prérequis=[IFT1015, IFT1016]",
        "Concomitants=[]",
        "Périodes communes=[daytime]",
        "Sessions communes=[winter, autumn, summer]",
        "Horaires=listeHoraires,
        "Conflits=listeConflits"
    ],
    [
        "Combinaison 2",
        "Cours=[IFT1227]",
        "Crédits=3",
        "Prérequis=[IFT1065, IFT1215]",
        "Concomitants=[]",
        "Périodes communes=[daytime]",
        "Sessions communes=[winter, autumn]",
        "Horaires=[IFT1227 [A] [Ma] 10:30-12:29, IFT1227 [A] [Ma] 10:30-12:29, IFT1227 [A] [Je] 15:30-16:29, IFT1227 [A] [Je] 15:30-16:29, IFT1227 [A] [Ma] 10:30-12:29, IFT1227 [A] [Ma] 10:30-12:29, IFT1227 [A] [Ma] 09:30-12:29, IFT1227 [A101] [Je] 16:30-18:29, IFT1227 [A101] [Je] 16:30-18:29]",
        "Conflits=[]"
    ]
]

**Exemple de réponse JSON ( status 400)**:
Requête invalide

#### POST /cours/comparer
Compare plusieurs cours selon des critères.

**Format pour le body de la requête:**
{
  "cours": ["idCours1", "idCours2",...],
  "criteres": ["critere1", "critere2",...]
}

**Exemple de Body JSON attendu :**
{
  "cours": ["IFT1025", "IFT2255"],
  "criteres": ["name", "credits"]
}

**Exemple de réponse JSON**: ( status 200)
[
  ["IFT1025","Introduction à l'informatique","3"],
  ["IFT2255","Structures de données","3"]
]

**Exemple de réponse JSON ( status 400)**:
La comparaison n'a pas pu être effectuée. Vérifiez le format des critères de comparaison et celui des ids de Cours.
#### POST /cours/rechercher

**Format pour le body de la requête:**
{
    "param" : Param ,
    "valeur" : "IFT1025",
    "includeSchedule": "false",
    "semester":  String
}

Les valeurs possibles de Param sont : id, name et description.

**Exemple de Body JSON attendu :**
{
    "param" : "id",
    "valeur" : "IFT1025",
    "includeSchedule": "false",
    "semester":  null
}
ou encore

{
    "param" : "id",
    "valeur" : "IFT1025",
    "includeSchedule": "true",
    "semester":  "A24"
}

**Exemple de réponse JSON**: ( status 200)
[
    {
        "id": "IFT1025",
        "description": "Concepts avancés : classes, objets, héritage, interfaces, réutilisation, événements. Introduction aux structures de données et algorithmes : listes, arbres binaires, fichiers, recherche et tri. Notions d'analyse numérique : précision.",
        "name": "Programmation 2",
        "scheduledSemester": null,
        "prerequisite_courses": [
            "IFT1015",
            "IFT1016"
        ],
        "equivalent_courses": [],
        "concomitant_courses": [],
        "udemWebsite": null,
        "credits": 3.0,
        "requirement_text": "prerequisite_courses :  IFT1015 ou IFT1016",
        "available_terms": {
            "autumn": true,
            "winter": true,
            "summer": true
        },
        "available_periods": {
            "daytime": true,
            "evening": false
        },
        "schedules": []
    }
]
**Exemple de réponse JSON ( status 400)**:
Cours pas trouvé. Veuillez reessayer. Pour rappel, les paramètres possibles sont id, name et description.
