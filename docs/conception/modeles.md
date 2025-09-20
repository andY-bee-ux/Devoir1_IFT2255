---
title: Conception - Modèle de données
---

# Modèle de données

## Entités principales

- Utilisateur
- Réservation
- Ressource
- Notification

> Décrire brièvement chaque entité.

## Relations entre entités

- 1 utilisateur peut avoir plusieurs réservations
- Une ressource peut être associée à 0 ou plusieurs réservations

## Contraintes métier

- Une réservation ne peut pas chevaucher une autre
- Un utilisateur doit valider son courriel avant de réserver

## Évolution potentielle du modèle

- Ajouter des statuts de réservation
- Support multi-utilisateur par ressource
