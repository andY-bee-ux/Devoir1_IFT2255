---
title: Analyse des besoins - Exigences
---

# Exigences

## Exigences fonctionnelles

En nous basant sur la liste de souhait de notre client, ainsi que sur notre analyse du domaine du projet, les exigences fonctionnelles sont les suivantes:

- [ ] EF0 : L'utilisateur peut sélectionner un profil ( étudiant/visiteur)

En se basant sur l'hypothèse selon laquelle la plateforme est publique, le profil étudiant correspondra donc aux étudiants de l'UdeM et le profil visiteur correspond à toute personne

| Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
|L'utilisateur accède à la plateforme via un lien par exemple|L'utilisateur veut accéder à la plateforme| L'interface du système se met à jour dépendamment du profil( accès direct à la plateforme avec restriction si visiteur et page de connexion si étudiant)| Ce CU ne dépend pas vraiment des autres CU| L'utilisateur veut accéder à la plateforme.|

- [ ] EF1 : L’utilisateur peut créer un compte.

| Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
|L'utilisateur clique sur "créer un compte"| L'utilisateur doit être un étudiant| Un nouvel id de compte est ajouté dans notre base de données| Ce cas d'utilisation n'est accessible que si l'utilisateur a sélectionné le profil étudiant| Un étudiant souhaite se créer un compte|

    
- [ ] EF2  : L'utilisateur peut se connecter;

| Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
|L'utilisateur clique sur "Se connecter"| L'utilisateur a déjà un compte|L'horodatage de dernière connexion de cet utilisateur est mis à jour dans la base de données| Ce CU dépend du CU "creer compte" car c'est un préalable avant de pouvoir se connecter| Accéder à la plateforme|

- [ ] EF3 : L'utilisateur peut personnaliser son profil ;

| Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
|L'utilisateur a cliqué sur "continuer"| L'utilisateur a amorcé le processus de création de compte| Des paires (propriété, valeur) relatives aux informations personnelles fournies sont stockées dans la base de données| Ce CU est étroitement lié au CU "creer compte"| Un utilisateur souhaite avoir une expérience fluide et personnalisée.|

- [ ] EF4 : L'utilisateur peut modifier son profil;

| Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
|L'utilisateur a cliqué sur "modifier profil" dans les Paramètres|L'utilisateur doit déjà avoir un compte et être connecté sur ce dernier|Son profil est mis à jour| Ce CU est lié au CU "creer compte" et "se connecter"| L'utilisateur souhaite mettre à jour les données de son profil|

- [ ] EF6 : Le système envoie un courriel de confirmation;

| Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
|L'utilisateur a saisi une adresse e-mail et a cliqué sur "s'inscrire"| L'utilisateur doit avoir amorcé le processus de création de compte|Une requête vers l'API de email sending a été effectuée avec succès ( de son côté, l'utilisateur reçoit un mail)|Ce CU est lié au CU de "creer compte"|Se rassurer de la validité de l'adresse email|

- [ ] EF7 : L'utilisateur peut rechercher un cours par sigle ou par nom ou mot-clé ( par exemple s'il recherche "Programmation" alors tous les cours qui contiennent "Programmation" dans leurs noms doivent s'afficher ( IFT1015, IFT1025,...));

| Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
|L'utilisateur clique sur la barre de recherche| L'utilisateur doit être connecté sur son compte qu'il a au préalable créé| La requête à l'API Planifium a été réalisée avec succès et la page du cours s'affiche|Ce CU dépend du CU "creer compte", "se connecter" | L'utilisateur souhaite voir les informations relatives à un cours avant de s'y inscrire|

- [ ] EF8 : L'utilisateur peut consulter la liste des avis d'un cours;

| Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
|L'utilisateur clique sur "Avis"|L'utilisateur doit être connecté sur son compte qu'il a au préalable créé, et doit avoir recherché ( ou selectionné ) un cours | La requête au bot Discord a été réalisée avec succès et la page des avis du cours s'affiche |Ce CU dépend du CU "creer compte", "se connecter" et "rechercher cours"|L'utilisateur souhaite voir les avis des anciens étudiants pour prendre sa décision.|

- [ ] EF9 : L'utilisateur peut filtrer sa recherche ( selon ses préférences, centres d'intérêt et données personnelles genre cycle, programme);

| Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
|L'utilisateur a cliqué sur l'icône de filtre|L'utilisateur doit être connecté sur son compte qu'il a au préalable créé, et doit avoir ou non effectué une recherche |La requête à l'API Planifium a été réalisée avec succès et le résultat filtré s'affiche|Ce CU dépend du CU "creer compte", "se connecter" et peut-être "rechercher cours" si la recherche a été faite avant le filtrage | L'utilisateur souhaite avoir des résultats qui répondent à certains critères. |

- [ ] EF10 : L'utilisateur peut voir la description détaillée des cours ( pré-requis, co-requis, exigences liées au programme ou au cycle);

| Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
|L'utilisateur clique sur "Description"| L'utilisateur doit être connectée à son compte qu'il a créé ou non( utilisateur visiteur) et doit être sur la page d'un cours| La requête à Planifium a été effectuée avec succès et la description du cours s'affiche| Ce CU dépend du CU "se connecter"| L'utilisateur veut accéder à la description détailler des cours|

- [ ] EF11 : Le système doit être doté d'un outil de comparaison qui permet à l'utilisateur d'évaluer la charge de travail totale d'une combinaison de cours. Pour cela, le système doit calculer la charge de travail de chaque cours ( par exemple en établissant une corrélation entre les moyennes sur plusieurs années et la charge de travail selon les avis non-officiels), et pourra donc faire une moyenne pour avoir la charge de travail totale de la combinaison ( Facile + Moyen + rès Difficile = Moyen par exemple);

| Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
|L'utilisateur va dans la section "comparaison"| L'utilisateur est connecté à son compte qu'il a au préalable créé| Des requêtes à Planifium et à Discord via le bot ont été réalisées avec succès|Ce CU dépend des CU "creer compte" et "se connecter"| L'utilisateur veut choisir des cours dont la charge de travail totale est réalisable|

- [ ] EF12 : Le système doit proposer des cours à l'utilisateur basé sur son historique de recherche et son profil; 


| Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
|L'utilisateur se connecte à son compte |L'utilisateur a setup son profil lors de la création de son compte, s'est connecté et a idéalement effectué déjà une recherche |La requête Planifium  pour récupérer les cours répondant à certains critères a été effectuée avec succès|Ce CU dépend du CU "creer compte", "se connecter", "rechercher cours"|L'utilisateur aura une expérience plus fluide et personnalisée|

- [ ] EF13:  L'utilisateur peut poster des avis( et dans ce cas on aura donc deux types d'acteurs d'entrée dans notre diagramme : l'étudiant qui recherche un cours et l'étudiant qui a déjà fait ledit cours)

| Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
- [ ] EF14 : L'utilisateur peut ajouter des cours à ses favoris ;

| Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
|L'utilisateur clique sur l'icône "coeur"|L'utilisateur est connecté à son compte qu'il a au préalable créé, et a accédé à la page d'un cours|Le cours est ajouté dans les favoris de l'utilisateur|Ce CU dépend du CU "creer compte", "se connecter" et "rechercher cours"|L'utilisateur veut pouvoir retrouver un cours qu'il a trouvé intéressant|

- [ ] EF15 : L'utilisateur peut démarrer une discussion.

    Comme dans un forum, il devrait être possible sur la plateforme de démarrer une discussion au sujet d'un cours ( par exemple poser une question particulière et par chance, des personnes ayant déjà fait le cours passeront par là et répondront). Cette fonctionnalité peut également être intéressante dans le contexte d'un cours pour lequel on a pas pu récolter au moins 5 avis ( et dans ce cas, aucun avis ne s'affiche).

 Déclencheur | Préconditions | Postconditions | Dépendance | But|
|-----------------------|----------------------------------------|-------------------------------------------------------|-----------------------------------------------------------|-------------------------------------------|
|L'utilisateur clique sur "demarrer une discussion"|L'utilisateur est connecté à son comte qu'il a au préalable créé| Les données associées à l'ensemble des discussions sont mis à jour dans la base de données| Ce CU depend des CUs "creer compte", "se connecter"|L'utilisateur souhaite avoir des informations supplémentaires au sujet d'un cours|

- [ ] EF16: Le système peut proposer certaines discussions aux utilisateurs basés sur leur historique et leur profil.

On part du principe que chaque discussion a pour nom le nom d'un cours suivi d'un certain identifiant de discussion, et ainsi, de la même façon que le système propose des cours à l'utilisateur, il pourra lui proposer des discussions.


## Exigences non fonctionnelles

TODO: Contraintes de performance, sécurité, compatibilité, etc.

Exemple :

- [ ] ENF1 :  L'affichage des résultats de recherche doit se faire en moins de 1 seconde;
-  [ ] ENF2 : Les avis associés à un cours ne peuvent être affichés que si leur nombre excède 4. 
- [ ] ENF3 : La plateforme doit être compatible avec Microsoft Edge, Firefox et Chrome;
- [ ] ENF4 : La plateforme doit utiliser l'API Planifium; ( c'est une contrainte d'implémentation et non une fonctionnalité ( demo 2 page 10))
- [ ] ENF5 :  La plateforme doit communiquer avec un robot Discord qui se charge de la collecte de données sur un serveur Discord;
- [ ] ENF6 : Les données sensibles ( noms des personnes qui ont posté les avis, noms associés aux anciens étudiants ou toute autre information personnelle ) inclus dans des avis doivent être supprimées de sorte à ce que rien ne puisse être associé à qui que ce soit;

- [ ] ENF7 : Toutes les données doivent être centralisées au sein de la plateforme, c'est-à-dire qu'il ne faut pas de lien vers un serveur Discord par exemple;

- [ ] ENF8 : L'utilisateur doit pouvoir traduire la page;

- [ ] ENF9 :  L'interface doit être accessible à tous;

- [ ] ENF10 :  L'adresse courriel utilisée lors de l'inscription doit être une adresse courriel de l'UdeM ( *.*(.*)+@umontreal.ca );
- [ ] ENF11 : Pour la validation de courriel, le système doit utiliser une email sending API ( SendGrid par exemple);
- [ ] ENF12: L'utilisateur doit pouvoir accéder au Centre Étudiant;


## Priorisation

TODO: Identifier les exigences critiques.

## Types d'utilisateurs

> Identifier les différents profils qui interagiront avec le système.

| Type d’utilisateur | Description | Exemples de fonctionnalités accessibles |
|--------------------|-------------|------------------------------------------|
| Utilisateur invité | Accès limité, pas d’authentification | Consultation des ressources |
| Utilisateur authentifié | Compte personnel, fonctions principales | Réservation, historique |
| Administrateur | Droits étendus, gestion des ressources | Création/suppression de ressources, gestion des utilisateurs |

<!-- TODO: Détailler selon le périmètre du projet. -->

## Infrastructures

> Informations sur l’environnement d’exécution cible, les outils ou plateformes nécessaires.

- Le système sera hébergé sur un serveur Ubuntu 22.04.
- Base de données : PostgreSQL version 15.
- Serveur Web : Nginx + Gunicorn (pour une app Python, par exemple).
- Framework principal : [À spécifier selon le projet].

<!-- TODO: Compléter selon le stack technique prévu. -->
