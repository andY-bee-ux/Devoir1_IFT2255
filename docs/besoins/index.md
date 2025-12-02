---
title: Analyse des besoins - Présentation générale
---

# Présentation du projet

## Description du domaine

### Fonctionnement

Actuellement, à l'UdeM, la plupart des étudiants s'inspirent des *descriptions brèves* de cours données sur le **site officiel de l'université** , comme le montre la capture suivante:

![Capture d'écran du site officiel de l'UdeM sur la page descriptive d'un cours](../images/image_siteofficiel_cours.png) *Source: [Site officiel de l'Udem](https://admission.umontreal.ca/cours-et-horaires/cours/ift-2105/)*.


Ce dernier offre une interface sur laquelle on peut rechercher des cours, et obtenir des informations comme le nombre de crédits et l'horaire en fonction des sessions. Ces courtes descriptions sont également accessibles via le [**Centre Étudiant**](glossaire.md), qui constitue de nos jours le moyen de choix de cours par défaut à l'UdeM.

 Certains étudiants, animés par un désir de certitude un peu plus poussé en ce qui concerne leurs choix, ont sans surprise besoin d'approfondir leurs recherches, et optent donc pour **des ressources non-officielles** mais pas des moindres, comme **les avis d'anciens étudiants**. Ces derniers sont généralement accessibles via des forums comme [**Reddit**](glossaire.md), ou bien un réseau social largement utilisé par la communauté étudiante nommé [**Discord**] (glossaire.md). Grâce à ces outils, les étudiants peuvent avoir accès aux opinions des anciens, que ce soit en ce qui concerne le cours, mais aussi la charge de travail, le niveau de difficulté et le rythme de cours perçu. Ces témoignages peuvent également être accessibles via du **bouche-à-oreille**, lorsqu'un étudiant qu'on connaît personnellement a déjà fait le cours. 

En réalité, les étudiants conçoivent généralement que la charge de travail d'un cours est inhérente au profil du professeur, et leur décision se base alors très souvent sur les avis collectés sur des sites comme *ratemyprofessor*, ou même sur l'expérience professionnelle dudit professeur, exposée sur **Linkedin**(glossaire.md).

 Certains peuvent également solliciter l'aide de [**TGDEs**](glossaire.md) ou de [**conseillers**](glossaire.md), qui peuvent aussi bien être des responsables que des camarades qui ont déjà fait le cours ( bouche-à-oreille). Tous ces éléments gravitent dans notre domaine, et assurent le fonctionnement de ce dernier.

### Acteurs

Le domaine tel que décrit ci-dessus renferme de nombreux acteurs:

- **Les étudiants de l'université de Montréal**

    Cette catégorie concerne aussi bien:- **Les anciens étudiants** : Il s'agit d'étudiants qui ont déjà eu des cours et qui souhaitent en choisir de nouveaux pour la prochaine [session](glossaire.md); - **Les nouveaux étudiants** : Ces derniers désignent les personnes souhaitant s'inscrire pour la première fois à des cours. Ils peuvent provenir de cégeps, de lycées, ou bien d'autres universités. - **Les étudiants expérimentés** : Cette catégorie désignera dans notre contexte, les étudiants ayant déjà eu le cours en question. Ces derniers interviennent en témoignant de leurs expériences, que ce soit virtuellement ( Discord, reddit...) ou physiquement ( bouche-à-oreille).


- **Les responsables** : Il s'agit ici de TGDEs et de conseillers qui guident les étudiants dans leur prise de décisions. Les conseillers pourraient aussi bien être des conseillers d'orientation que les profs chargés de cours qui peuvent aider l'étudiant à comprendre s'il satisfait aux exigences du cours.

- **Les chargés de cours** : Les professeurs, ainsi que les démonstrateurs représentent également des acteurs de notre domaine car ils sont justement responsables des cours en question.

### Dépendances

Dans notre domaine, il existe des dépendances entre les étudiants qui cherchent à s'inscrire à un cours, et toutes les autres catégories d'acteurs ( responsables, chargés de cours, étudiants qui donnent des avis).

## Hypothèses et contraintes

Les hypothèses considérées lors de notre travail sont les suivantes:

- H1: La plateforme est publique. On distinguera donc deux profils principaux : étudiant ( étudiant de l'UdeM et dans ce cas il faudra créer un compte avec l'adresse email de l'université) et visiteur( ces derniers auront accès à la description de cours, mais n'auront pas accès aux informations issues de Planifium ou du bot Discord. Ils ne pourront pas non plus lancer de discussion, poster un avis...)

- H2 :  Une des contraintes de l'énoncé est que les avis des étudiants et les données officielles provenant de Planifium doivent être centralisés dans une *même interface*. Nous considérons ici que "même interface" signifie qu'on peut se déplacer entre les sections "avis" et "moyennes" par exemple, sans ouvrir un nouvel onglet par exemple si la plateforme est une plateforme web. On les séparera en deux sections différentes; ( les informations ne seront pas sur la même page, mais le déplacement entre les deux sections n'ouvre pas un nouvel onglet)
- H3 : Le système ne pourra être utilisé que par les étudiants du DIRO car les informations de Planifium concerne uniquement les cours du DIRO ( pour l'instant). Dans notre prototype interactif, les filtres ont donc été adapté à cet effet.

- H4 : Dans le système, on aura pour chaque cours un estimateur de la **charge de travail totale** ( les valeurs possibles sont : Faible, Moyenne, Élevée). Cette estimation est calculée basée sur les moyennes générales du cours.

## Lien du prototype

Nous avons réalisé un prototype Figma qui se trouve au lien suivant:

https://www.figma.com/proto/LWJZnKTemAaash9IN1G4OS/Prototype_2255?node-id=0-1&t=Qa7Bc3sqZaHIZ5Gs-1
