---
title: Analyse des besoins - Flux principaux
---

# Flux d’interaction entre les acteurs et le système

## Flux 1 : Créer un compte
1. L'utilisateur clique sur le profil "Étudiant".
2. Le système affiche la page de connexion.
3. L'utilisateur clique sur "S'inscrire".
4. Le système affiche la page d'inscription.
5. L'utilisateur saisit son nom, ainsi que son mot de passe.
6. L'utilisateur saisit son adresse email.
7. Le système vérifie si l'adresse courriel est valide.
8. Le système affiche la page de personnalisation de profil.
9. L'utilisateur choisit ses préférences et donne des informations supplémentaires et clique sur "Terminer".
10. Le système indique que le compte a été créé avec succès et affiche la page d'accueil.

### Scénarios alternatifs
- **3.a L'utilisateur a déjà un compte**
  - Appel du cas *Se connecter*.
- **6.a L'adresse courriel est invalide**
  1. Le système indique à l'utilisateur qu'il doit s'agir d'une adresse de courriel de l'UdeM.
  2. Le système invite l'utilisateur à réessayer.
  3. Le scénario reprend à l'étape 6 (maximum de 3 essais pour éviter une boucle infinie).

---

## Flux 2 : Voir les avis d'un cours
1. L'utilisateur recherche un cours.
2. Le système vérifie la validité de la recherche.
3. Le système affiche la page associée au cours.
4. L'utilisateur clique sur la section "Avis".
5. Le système affiche les avis dans la mesure du possible (au moins 5).
6. L'utilisateur consulte les avis.

### Scénarios alternatifs
- **2.a L'entrée est invalide**
  1. Le système indique qu'aucun cours ne correspond à la recherche et fait une proposition de résultats.
  2. L'utilisateur sélectionne un cours de la liste.
  3. Le scénario reprend à l'étape 3.
  - **2.a.2.a Aucun des cours de la liste ne correspond au cours recherché**
    - Le scénario reprend à l'étape 1.
- **5.a Pas assez d'avis associés au cours (moins de 5)**
  1. Le système indique qu'il n'y a aucun avis pour le cours et propose à l'utilisateur de poster un avis.
  2. L'utilisateur poste un avis.
    - **5.a.2.a L'utilisateur ne poste pas d'avis**
      - Le scénario se termine.

---

## Flux 3 : Voir les informations officielles associées à un cours
1. L'utilisateur recherche un cours.
2. Le système vérifie la validité de la recherche.
3. Le système affiche la page associée au cours.
4. L'utilisateur clique sur la section "Informations".
5. Le système affiche les informations officielles associées au cours.
6. L'utilisateur consulte les informations officielles.

### Scénarios alternatifs
- **2.a L'entrée est invalide**
  1. Le système indique qu'aucun cours ne correspond à la recherche et fait une proposition de résultats.
  2. L'utilisateur sélectionne un cours de la liste.
  3. Le scénario reprend à l'étape 3.
  - **2.a.2.a Aucun des cours de la liste ne correspond au cours recherché**
    - Le scénario reprend à l'étape 1 (et si le user ne veut plus faire de recherche ?)
- **5.a Le système n'a pas pu récupérer les informations associées au cours**
  1. Le système indique que les informations ne sont pas disponibles et propose à l'utilisateur de consulter les avis plutôt.
  2. Appel du cas *consulter les avis*.
    - **5.a.1.a L'utilisateur ne veut pas consulter les avis**
      - Le scénario se termine.

---

## Flux 4 : Comparer cours
1. L'utilisateur sélectionne l'option "Comparer Cours".
2. L'utilisateur recherche les cours qu'il veut comparer et les ajoute.
3. Le système vérifie la validité de la recherche.
4. L'utilisateur sélectionne les critères de comparaison.
5. Le système vérifie la validité de la recherche.
6. Le système affiche les informations relatives à l'ensemble des cours sous forme de tableau, suivant les critères.

### Scénarios alternatifs
- **3.a L'entrée est invalide**
  1. Le système indique que les identifiants de cours sont invalides et invite l'utilisateur à réessayer.
  2. Le scénario reprend à l'étape 2.
- **5.a L'entrée est invalide**
  1. Le système indique que les critères ne sont pas valides et affiche la liste des critères autorisée.
  2. Le scénario reprend à l'étape 4.

---

## Flux 5 : Rechercher un cours
### Variante 1 : Par ID
1. L’utilisateur saisit l'id du cours qu'il veut rechercher.
2. Le système fait appel au service de Cours pour valider l'id.
3. Le système consulte le répertoire de Cours pour obtenir le cours.
4. Le système retourne le cours correspondant.
5. Le système affiche le résultat.

#### Scénarios alternatifs
- **1.a.1 L'utilisateur saisit le nom du cours**
  1. Le système fait appel au service de Cours pour valider le nom du cours.
  2. Le scénario reprend à l'étape 3.
- **1.a.2 L'utilisateur recherche le cours par mot clé**
  1. Le système consulte le répertoire de Cours pour obtenir la liste de cours contenant ce mot clé.
  2. Le système affiche la liste de cours.
- **2.a L'entrée de l'utilisateur est invalide**
  - Le système affiche un message d'erreur.
- **3.a L'API Planifium a une panne**
  1. Le système recherche le cours dans la base de donnée.
  2. Le scénario reprend à l'étape 5.
- **3.b Le cours n'existe pas dans Planifium**
  - Le système retourne un message signalant que le cours n'existe pas.
- **3.a.1 Le cours n'existe pas dans la base de donnée**
  - Le système retourne un message signalant que le cours n'existe pas.

### Variante 2 : Option "Rechercher Cours" dans le menu
1. L’utilisateur sélectionne l’option “Rechercher Cours” dans le menu.
2. L’utilisateur choisit le critère selon lequel il désire effectuer une recherche (nom ou sigle).
3. Le système fait appel à l'API Planifium pour obtenir le cours.
4. Le système retourne le/les cours correspondant aux critères de recherche.
5. Le système affiche le résultat.

#### Scénarios alternatifs
- **3.a L'API Planifium a une panne**
  1. Le système recherche le cours dans la base de donnée.
  2. Le scénario reprend à l'étape 5.
- **3.b Le cours n'existe pas dans Planifium**
  - Le système retourne un message signalant que le cours n'existe pas.
- **3.a.1 Le cours n'existe pas dans la base de donnée**
  - Le système retourne un message signalant que le cours n'existe pas.


## Diagrammes

![Diagramme pour le cas d'utilisation "Comparer Cours"](../images/Diagramme_Comparer_Cours.jpg) *Diagramme pour les cas d'utilisation "Comparer Cours"*

![Diagramme pour le cas d'utilisation "Consulter Avis et Cours"](../images/diagramme2_voirAvis_voirCours.jpg) *Diagramme pour les cas d'utilisation "Voir Avis, consulter cours"*

