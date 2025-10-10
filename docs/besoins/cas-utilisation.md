---
title: Analyse des besoins - Cas d'utilisation
---

# Cas d'utilisation

## Vue d’ensemble

TODO: Introduction aux cas d’utilisation du système.

## Liste des cas d’utilisation

| ID | Nom | Acteurs principaux | Description |
|----|-----|---------------------|-------------|
| CU01 | Sélection du profil | Utilisateur | L'utilisateur sélectionne un profil (étudiant ou visiteur) |
| CU02 | Création de compte | Étudiant | L'utilisateur (étudiant) crée un compte sur la plateforme |
| CU03 | Connexion au compte | Utilisateur | L'utilisateur se connecte à son compte existant |
| CU04 | Personnalisation du profil | Utilisateur | L'utilisateur personnalise son profil lors de la création de compte |
| CU05 | Modification du profil | Utilisateur | L'utilisateur modifie les informations de son profil |
| CU06 | Envoi de courriel de confirmation | Système | Le système envoie un courriel de confirmation à l’utilisateur |
| CU07 | Recherche de cours | Utilisateur | L'utilisateur recherche un cours par sigle, nom ou mot-clé |
| CU08 | Consultation des avis | Utilisateur | L'utilisateur consulte la liste des avis d’un cours |
| CU9 | Filtrage de recherche | Utilisateur | L'utilisateur filtre les résultats selon ses préférences |
| CU10 | Consultation de la description d’un cours | Utilisateur | L'utilisateur consulte les détails d’un cours |
| CU11 | Comparaison de cours | Utilisateur | Le système permet de comparer la charge de travail de plusieurs cours |
| CU12 | Recommandation de cours | Système | Le système propose des cours selon l’historique et le profil de l’utilisateur |
| CU13 | Publication d’un avis | Étudiant | L'utilisateur poste un avis sur un cours qu’il a suivi |
| CU14 | Ajout aux favoris | Utilisateur | L'utilisateur ajoute un cours à ses favoris |
| CU15 | Démarrage d’une discussion | Utilisateur | L'utilisateur démarre une discussion à propos d’un cours |
| CU16 | Recommandation de discussions | Système | Le système propose des discussions basées sur le profil et l’historique de l’utilisateur |



## Détail

<<<<<<< HEAD
### CU01 - Sélectionner profil
=======
### CU01 - Sélection du profil
>>>>>>> 74212818d40af88e827963aa993306b819786451

**Acteurs** : Utilisateur (principal)  
**Préconditions** :  
**PostConditions** :  
**Déclencheur** : L'utilisateur accède à la plateforme via un lien par exemple  
**Dépendances** : Ce CU ne dépend pas vraiment des autres CU  
**But** : L'utilisateur veut accéder à la plateforme.

En se basant sur l'hypothèse selon laquelle la plateforme est publique, le profil étudiant correspondra donc aux étudiants de l'UdeM et le profil visiteur correspond à toute personne.

---

### CU02 - Création de compte

**Acteurs** : Étudiant (principal)  
**Préconditions** : L'utilisateur doit être un étudiant  
**PostConditions** : Un nouvel id de compte est ajouté dans notre base de données  
**Déclencheur** : L'utilisateur clique sur "créer un compte"  
**Dépendances** : Ce cas d'utilisation n'est accessible que si l'utilisateur a sélectionné le profil étudiant  
**But** : Un étudiant souhaite se créer un compte

---

### CU03 - Connexion au compte

**Acteurs** : Utilisateur (principal)  
**Préconditions** : L'utilisateur a déjà un compte  
**PostConditions** : L'horodatage de dernière connexion de cet utilisateur est mis à jour dans la base de données  
**Déclencheur** : L'utilisateur clique sur "Se connecter"  
**Dépendances** : Ce CU dépend du CU "créer compte" car c'est un préalable avant de pouvoir se connecter  
**But** : Accéder à la plateforme

---

### CU04 - Personnalisation du profil

**Acteurs** : Utilisateur (principal)  
**Préconditions** : L'utilisateur a amorcé le processus de création de compte  
**PostConditions** : Des paires (propriété, valeur) relatives aux informations personnelles fournies sont stockées dans la base de données  
**Déclencheur** : L'utilisateur a cliqué sur "continuer"  
**Dépendances** : Ce CU est étroitement lié au CU "créer compte"  
**But** : Un utilisateur souhaite avoir une expérience fluide et personnalisée.

---

### CU05 - Modification du profil

**Acteurs** : Utilisateur (principal)  
**Préconditions** : L'utilisateur doit déjà avoir un compte et être connecté sur ce dernier  
**PostConditions** : Son profil est mis à jour  
**Déclencheur** : L'utilisateur a cliqué sur "modifier profil" dans les Paramètres  
**Dépendances** : Ce CU est lié au CU "créer compte" et "se connecter"  
**But** : L'utilisateur souhaite mettre à jour les données de son profil

---

### CU06 - Envoi de courriel de confirmation

**Acteurs** : Système (principal)  
**Préconditions** : L'utilisateur doit avoir amorcé le processus de création de compte  
**PostConditions** : Une requête vers l'API de email sending a été effectuée avec succès (de son côté, l'utilisateur reçoit un mail)  
**Déclencheur** : L'utilisateur a saisi une adresse e-mail et a cliqué sur "s'inscrire"  
**Dépendances** : Ce CU est lié au CU "créer compte"  
**But** : Se rassurer de la validité de l'adresse email

---

### CU07 - Recherche de cours

**Acteurs** : Utilisateur (principal)  
**Préconditions** : L'utilisateur doit être connecté sur son compte qu'il a au préalable créé  
**PostConditions** : La requête à l'API Planifium a été réalisée avec succès et la page du cours s'affiche  
**Déclencheur** : L'utilisateur clique sur la barre de recherche  
**Dépendances** : Ce CU dépend du CU "créer compte", "se connecter"  
**But** : L'utilisateur souhaite voir les informations relatives à un cours avant de s'y inscrire

---

### CU08 - Consultation des avis

**Acteurs** : Utilisateur (principal)  
**Préconditions** : L'utilisateur doit être connecté sur son compte qu'il a au préalable créé, et doit avoir recherché (ou sélectionné) un cours  
**PostConditions** : La requête au bot Discord a été réalisée avec succès et la page des avis du cours s'affiche  
**Déclencheur** : L'utilisateur clique sur "Avis"  
**Dépendances** : Ce CU dépend du CU "créer compte", "se connecter" et "rechercher cours"  
**But** : L'utilisateur souhaite voir les avis des anciens étudiants pour prendre sa décision.

---

### CU09 - Filtrage de recherche

**Acteurs** : Utilisateur (principal)  
**Préconditions** : L'utilisateur doit être connecté sur son compte qu'il a au préalable créé, et doit avoir ou non effectué une recherche  
**PostConditions** : La requête à l'API Planifium a été réalisée avec succès et le résultat filtré s'affiche  
**Déclencheur** : L'utilisateur a cliqué sur l'icône de filtre  
**Dépendances** : Ce CU dépend du CU "créer compte", "se connecter" et peut-être "rechercher cours" si la recherche a été faite avant le filtrage  
**But** : L'utilisateur souhaite avoir des résultats qui répondent à certains critères.

---

### CU10 - Consultation de la description d’un cours

**Acteurs** : Utilisateur (principal)  
**Préconditions** : L'utilisateur doit être connecté à son compte qu'il a créé ou non (utilisateur visiteur) et doit être sur la page d'un cours  
**PostConditions** : La requête à Planifium a été effectuée avec succès et la description du cours s'affiche  
**Déclencheur** : L'utilisateur clique sur "Description"  
**Dépendances** : Ce CU dépend du CU "se connecter"  
**But** : L'utilisateur veut accéder à la description détaillée des cours

---

### CU11 - Comparaison de cours

**Acteurs** : Utilisateur (principal)  
**Préconditions** : L'utilisateur est connecté à son compte qu'il a au préalable créé  
**PostConditions** : Des requêtes à Planifium et à Discord via le bot ont été réalisées avec succès  
**Déclencheur** : L'utilisateur va dans la section "comparaison"  
**Dépendances** : Ce CU dépend des CU "créer compte" et "se connecter"  
**But** : L'utilisateur veut choisir des cours dont la charge de travail totale est réalisable

---

### CU12 - Recommandation de cours

**Acteurs** : Système (principal)  
**Préconditions** : L'utilisateur a setup son profil lors de la création de son compte, s'est connecté et a idéalement effectué déjà une recherche  
**PostConditions** : La requête Planifium pour récupérer les cours répondant à certains critères a été effectuée avec succès  
**Déclencheur** : L'utilisateur se connecte à son compte  
**Dépendances** : Ce CU dépend du CU "créer compte", "se connecter", "rechercher cours"  
**But** : L'utilisateur aura une expérience plus fluide et personnalisée

---

### CU13 - Publication d’un avis

**Acteurs** : Étudiant (principal)  
**Préconditions** : L'utilisateur a déjà suivi le cours concerné  
**PostConditions** : L’avis est ajouté à la base de données et visible pour les autres utilisateurs  
**Déclencheur** : L'utilisateur clique sur "Publier un avis"  
**Dépendances** : Ce CU dépend du CU "se connecter" et "rechercher cours"  
**But** : L'utilisateur souhaite partager son expérience sur un cours

---

### CU14 - Ajout aux favoris

**Acteurs** : Utilisateur (principal)  
**Préconditions** : L'utilisateur est connecté à son compte qu'il a au préalable créé, et a accédé à la page d'un cours  
**PostConditions** : Le cours est ajouté dans les favoris de l'utilisateur  
**Déclencheur** : L'utilisateur clique sur l'icône "coeur"  
**Dépendances** : Ce CU dépend du CU "créer compte", "se connecter" et "rechercher cours"  
**But** : L'utilisateur veut pouvoir retrouver un cours qu'il a trouvé intéressant

---

### CU15 - Démarrage d’une discussion

**Acteurs** : Utilisateur (principal)  
**Préconditions** : L'utilisateur est connecté à son compte qu'il a au préalable créé  
**PostConditions** : Les données associées à l'ensemble des discussions sont mises à jour dans la base de données  
**Déclencheur** : L'utilisateur clique sur "démarrer une discussion"  
**Dépendances** : Ce CU dépend des CUs "créer compte", "se connecter"  
**But** : L'utilisateur souhaite avoir des informations supplémentaires au sujet d'un cours  

Comme dans un forum, il devrait être possible sur la plateforme de démarrer une discussion au sujet d'un cours (par exemple poser une question particulière et par chance, des personnes ayant déjà fait le cours passeront par là et répondront). Cette fonctionnalité peut également être intéressante dans le contexte d'un cours pour lequel on n’a pas pu récolter au moins 5 avis (et dans ce cas, aucun avis ne s'affiche).

---

### CU16 - Recommandation de discussions

**Acteurs** : Système (principal)  
**Préconditions** : L'utilisateur a un profil et un historique de navigation/discussion  
**PostConditions** : Des discussions pertinentes sont proposées à l'utilisateur  
**Déclencheur** : L'utilisateur accède à la section des discussions  
**Dépendances** : Ce CU dépend du CU "démarrer une discussion" et "se connecter"  
**But** : Le système propose des discussions adaptées aux intérêts et à l’historique de l’utilisateur  

On part du principe que chaque discussion a pour nom le nom d'un cours suivi d'un certain identifiant de discussion, et ainsi, de la même façon que le système propose des cours à l'utilisateur, il pourra lui proposer des discussions.
