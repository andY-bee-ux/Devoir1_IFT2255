---
title: Vue d'ensemble
---

<style>
    @media screen and (min-width: 76em) {
        .md-sidebar--primary {
            display: none !important;
        }
    }
</style>

# PickCourse

## Description du projet

Ce projet vise à développer un outil d’aide au choix de cours pour les étudiants de l’Université de Montréal. Il centralise les données de Planifium, les résultats académiques et les avis étudiants (via Discord) afin de permettre la recherche de cours, l’affichage des détails (prérequis, horaires, difficulté, charge, avis), la comparaison de cours, de différentes combinaisons de cours et la personnalisation selon le profil de l’étudiant.

## Repository Git

Le dépôt Git du projet est disponible ici : https://github.com/andY-bee-ux/Devoir1_IFT2255

## Documentation Javadoc

La documentation Javadoc du projet a été générée automatiquement à l’aide du plugin `maven-javadoc-plugin`.

Elle est accessible à partir du fichier d’entrée principal suivant :  
[javadoc/reports/apidocs/index.html](../javadoc/reports/apidocs/index.html)

> **Note :** La classe `ClientController.java` a été volontairement exclue de la génération de la Javadoc.  
> Cette exclusion est due à l’utilisation de composants JavaFX qui provoquaient des erreurs lors de l’exécution du plugin de génération de documentation dans l’environnement de build Maven.

## Équipe

- **`M1`:** Deazo Aude-Prunelle Ahui (20268927) #prupru1203
- **`M2`:** Mouhamed Ahmed Tidjani Diop (20290840) #_tidjani1
- **`M3`:** Andréa Noukoua Djodi (20279717) #andychloe 
- **`M4`:** Moussa Adama Sogoba (20201487) #moussas



## Échéancier du devoir 1

| Tâches                        | Terminé le   | Statut     | Responsable         |
|-------------------------------|--------------|------------|---------------------|
| Ouverture de projet           | 12 septembre | ✅ Terminé  | `M1` `M3`           |
| Description du domaine        | 19 septembre | ✅ Terminé  | `M1` `M2` `M3` `M4` |
| Identification des acteurs    | 19 septembre | ✅ Terminé  | `M1` `M2` `M3` `M4` |
| Glossaire                     | 07 octobre   | ✅ Terminé | `M1` `M2`           |
| Exigences Fonct et Non-Fonct  | 24 septembre | ✅ Terminé  | `M1` `M2` `M3` `M4` |
| Évaluation des risques        | 24 septembre | ✅ Terminé  | `M2` `M3`           |
| Cas d'utilisation             | 26 septembre | ✅ Terminé  | `M1` `M2` `M3` `M4` |
| Diagramme d'activités         | 29 septembre | ✅ Terminé  | `M1` `M2` `M3` `M4` |
| Modèle C4                     | 01 octobre             | ✅ Terminé  |                     |
| Prototype fonctionnel (bonus) | 08 octobre   | ✅ Terminé  |  `M3`                   |
| Rapport                       | 10 octobre             | ✅ Terminé |                     |

## Échéancier du devoir 2

| Tâches                                  | Terminé le | Statut     | Responsable |
|-----------------------------------------|------------|------------|-------------|
| Révision et intégration du feedback                               |  10 novembre          | ✅ Terminé | `M1` `M2` `M3` `M4` |
| Modèle C4 niveau 3                              | 19 novembre           | ✅ Terminé |    `M3`         |
| Justification des choix de design       | 20 novembre           | ✅ Terminé           |     `M3`        |
| Diagramme de classes    | 23 novembre           | ✅ Terminé           | `M1` `M2` `M3` `M4`            |
| Diagrammes de séquence  | 25 novembre           |   ✅ Terminé         |             | `M1` `M2` `M4`
| Implémentation des fonctionnalités       |   30 novembre         |✅ Terminé            | `M2` `M3` `M4`            |
| Écriture des tests unitaires*            |  01 décembre          |   ✅ Terminé         | `M1` `M2` `M3` `M4`            |
| Rapport      | 02 décembre           |  ✅ Terminé         |`M1` `M2` `M3` `M4`              | 
| Bonus : Développement du bot Discord    |  01 décembre          | ✅ Terminé           |  `M1`           |

\* Répartition — Écriture des tests unitaires

- **Tests du `Repository` (`CoursRepository`)**  
  Rédigés majoritairement par **`M2`**, avec des contributions de **`M1`** et **`M3`**.

- **Tests du `Service` (`CoursService`)**  
  Rédigés majoritairement par **`M2`**, avec des compléments de **`M1`** et **`M3`**.

- **Tests du `Controller` (`CoursController`)**  
  Rédigés principalement par **`M4`**.

- **Tests du modèle (`CoursTest`)**  
  Rédigés par **`M3`** .

## Échéancier du devoir 3
- Violet: Aude
- Bleu: Andréa
- Vert: Moussa
- Rouge: Tidjani

Lien vers la page Notion d'organisation:
https://www.notion.so/Devoir-3-2c28409ee3f08057980fe49c094ec5a7?source=copy_link
