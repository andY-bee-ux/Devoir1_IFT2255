## Commande `/avis`

La commande `/avis` permet à un étudiant de soumettre un avis détaillé sur un cours.

### Options de la commande

- **cours** (obligatoire)
  - Type : texte (string)
  - Exemple : `IFT2255`, `IFT2125`
  - Rôle : identifier le cours évalué.

- **session** (Optionnel)
  - Type : texte (string)
  - Exemple : `H2025`, `A2024`
  - Rôle : distinguer les différentes offres du cours.

- **professeur** (optionelle)
  - Type : texte (string)
  - Exemple : `Dupont`, `Martin`
  - Rôle : permettre d'associer l'avis à un enseignant.

- **note_obtenue** (obligatoire)
  - Type : nombre (int)
  - Exemple : `85`
  - Signification : note finale obtenue par l'étudiant dans le cours (sur 100, à définir dans la doc).
  - Utilisation : statistiques éventuelles (corrélation difficulté / résultats).

- **difficulte** (obligatoire)
  - Type : nombre entier (int)
  - Plage : 1 à 5
  - 1 = très facile, 5 = très difficile
  - Rôle : perception de la difficulté du cours.

- **qualite_cours** (obligatoire)
  - Type : nombre entier (int)
  - Plage : 1 à 5
  - 1 = très mauvais, 5 = excellent
  - Rôle : évaluation globale du cours (c’est cette valeur qui sera affichée comme "Note du cours").

- **charge** (obligatoire)
  - Type : nombre entier (int)
  - Plage : 1 à 5
  - 1 = très peu de travail, 5 = charge de travail très élevée
  - Rôle : indiquer la charge de travail perçue.

- **commentaire** (obligatoire)
  - Type : texte (string)
  - Exemple : `Le prof explique bien mais les travaux sont longs.`
  - Rôle : permettre un retour qualitatif.

### Règles de validation

- `difficulte`, `qualite_cours` et `charge` doivent être compris entre **1 et 5**.
- `note_obtenue` doit être comprise dans une plage définie (ex : 0 à 100).
- session doit être ecris de la maniére suivante : E2020, A2023
- La commande échoue si un champ obligatoire est manquant ou invalide.
- En cas de succès, le bot confirme l’enregistrement de l’avis avec un message du type :
  >  Avis pour le cours **IFT2255** soumis avec succès ! (Note du cours : 4/5)
