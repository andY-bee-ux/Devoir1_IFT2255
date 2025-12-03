---
title: Conception - Modèle de données
---

# Modèle de données

## Entités principales

### User
Représente un étudiant de l'université de Montréal qui utilise l’application.
- Attributs (diagramme) : `id`, `informationsDeProfil : Profil`
- Rôle : c’est l’acteur principal du système, celui qui donne des avis sur les cours et pour lequel on peut consulter des résultats académiques.

### Cours
Représente un cours universitaire (ex. IFT2255).
- Attributs (diagramme) : `id`, `description`, `sigle`, `horaireSession`, `indicateurSession`, `nombreCredits`, etc.
- Rôle : support des avis et des résultats; chaque avis et chaque résultat est toujours lié à un cours précis.

### Avis
Représente l’avis déposé par un étudiant sur un cours.
- Attributs (diagramme) : `id`, `contenu`, `noteAttendue`, `noteObtenue`, `difficulte`, `qualite`, `chargeTravail`, etc.
- Rôle : stocke l’évaluation qualitative et quantitative d’un étudiant sur un cours (permet ensuite des statistiques, filtres, recommandations…).

### Profil
Représente le “profil académique” d’un étudiant.
- Attributs (diagramme) : `preferences : String[]`, `centreInterets : ArrayList<String>`, `programmeEtudes`, `cycle`, `coursPrefferes : Cours`, etc.
- Rôle : permet de personnaliser les recommandations de cours ou la présentation des informations en fonction des intérêts de l’étudiant.

### Resultats
Représente les résultats académiques agrégés d’un étudiant.
- Attributs (diagramme) : `nombreEchecs`, `nombreCredits`, `moyenne`, `trimestre`, etc.
- Rôle : donne une vision globale de la progression de l’étudiant et peut servir à adapter les conseils (ex. charge de travail à venir).

## Relations entre entités

- **User – Profil**
  - Multiplicité : `User 0..1 ── 1 Profil`
  - Interprétation : un utilisateur peut avoir au plus un profil détaillé; un profil appartient à exactement un utilisateur.

- **User – Avis**
  - Multiplicité (interprétée à partir du diagramme et du contexte) : `User 1 ── 0..* Avis`
  - Interprétation : un utilisateur peut déposer plusieurs avis, mais chaque avis est associé à un seul utilisateur auteur.

- **Cours – Avis**
  - Multiplicité : `Cours 1 ── 0..* Avis`
  - Interprétation : un cours peut recevoir plusieurs avis, mais chaque avis concerne exactement un cours.

- **User – Resultats**
  - Multiplicité (contexte) : `User 1 ── 0..* Resultats`
  - Interprétation : un utilisateur peut avoir plusieurs enregistrements de résultats (par trimestre ou par programme), mais chaque objet Resultats appartient à un seul étudiant.

- **Cours – Resultats**
  - Multiplicité (contexte) : `Cours 1 ── 0..* Resultats`
  - Interprétation : un cours peut apparaître dans plusieurs bulletins de résultats (plusieurs étudiants), mais chaque ligne de résultat est liée à un cours précis.

- **Profil – Cours**
  - Multiplicité : via les attributs comme `coursPrefere : Cours`
  - Interprétation : un profil peut référencer un ou plusieurs cours “importants” pour l’étudiant (préférés, déjà réussis, à risque, etc.).


## Contraintes métier 

- **Un Avis doit toujours être lié à un User et à un Cours**
  - Pas d’avis anonyme et pas d’avis “orphelin”.
  - Contrainte : `avis.user != null` et `avis.cours != null`.

- **Un User ne peut pas déposer plusieurs Avis “identiques” pour le même Cours**
  - Contrainte possible : au plus un avis par (user, cours) pour une session donnée.
  - Permet d’éviter le spam d’avis et simplifie les statistiques.

- **Les valeurs numériques d’un Avis sont bornées**
  - `difficulte`, `qualite`, `chargeTravail` ∈ [1, 5].
  - `noteAttendue`, `noteObtenue` ∈ [0, 100].
  - Ces contraintes garantissent la cohérence et facilitent les calculs.

- **Un Profil ne peut exister sans User**
  - En cas de suppression d’un utilisateur, son profil doit être supprimé ou rendu inutilisable.
  - Contrainte : relation de composition forte User–Profil.

- **Les Resultats doivent rester cohérents avec les Cours suivis**
  - `nombreCredits` doit correspondre à la somme des crédits des cours associés.
  - Un trimestre ne doit pas contenir de cours dupliqués pour un même étudiant.

## Évolution potentielle du modèle

Actuellement, l’horaire d’un cours est intégré directement dans l’entité Cours.
Une évolution serait d’introduire des entités dédiées aux sessions, sections et activités. Cela permettrait d’éviter la duplication d’informations entre cours répétés chaque session, de représenter explicitement la structure réelle de l’université, et de faciliter la gestion des informations sur les sessions de manière indépendante du reste (popularité, taux de réussite, etc.).
