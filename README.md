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
  Ce dossier contiendra les differents diagrammes que nous aurons à realiser dans le cadre de notre projet. Le dossier contiendra a la fois les images
  et les fichiers **.vpp** (format des fichiers Visual Paradigm).

- **docs** : 
  Ce dossier contient tous les fichiers Markdown du site pour notre rapport construit avec [MkDocs](https://www.mkdocs.org/) et le thème [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/).

  - **Pour executer le rapport, veuillez suivre les instructions plus bas.**

- **.gitignore** :
  Spécifie quelles fichiers sont ignorer par git.

- **mkdocs.yml** :
  Ce fichier contient la configuration de MkDocs.

- **Pipfile** :
  Cet élément a été importé à l’aide du modèle MkDocs fourni pour ce devoir.

- **requirements** :
  Ce fichier contient les dépendances Python.

# Execution du rapport MkDocs

## Prérequis

Assurez-vous d’avoir les outils suivants installés :

- Python **3.11** ou plus récent
- `pip` (gestionnaire de paquets Python)
- `pipenv` ou équivalent (gestion d’environnement virtuel) 
  - Évite de polluer votre système et les conflits de version.
  - Installez-le avec `pip install pipenv`.

## Pour commencer

### Le répertoire existe déjà

1. Clonez ce dépôt (optionnel) pour obtenir le template localement 
```bash
git clone git@github.com:andY-bee-ux/Devoir1_IFT2255.git
```
2. Copiez les fichiers du template (en local) dans votre répertoire de projet.

> Note : Cette option est utile si vous souhaitez récupérer le contenu du template sans créer un nouveau dépôt (répertoire).

### Installation

> Vous avez maintenant le contenu du template sur votre poste. Il ne reste qu’à installer les dépendances pour commencer à l’utiliser.

1. Activez l'environnement virtuel avec 
```bash
pipenv shell
```
2. Installez les dépendances listées dans `requirements.txt` (à exécuter dans le répertoire du projet) :

```bash
pip install -r requirements.txt
```

### Utilisation

> Avant toute utilisation, assurez-vous que l’environnement virtuel est activé (`pipenv shell`).

### Développement local

Pour lancer un serveur de développement local et visualiser les modifications en temps réel, utilisez :

```bash
mkdocs serve
```

Le site sera accessible à l'adresse [http://127.0.0.1:8000](http://127.0.0.1:8000)

### Construction du site (optionnel)

> Cette étape n’est pas nécessaire pour la publication sur GitHub Pages

Pour construire le site :

```bash
mkdocs build
```

Les fichiers générés seront dans le dossier `site/`.

### Déploiement

Pour déployer automatiquement le site sur GitHub Pages (branche `gh-pages`)

```bash
mkdocs gh-deploy
```

> Cette commande pousse automatiquement le contenu du site sur la branche `gh-pages`. Si la branche n'existe pas, elle est crée automatiquement.

### Personnalisation

1. Modifiez `mkdocs.yml` pour changer la configuration du site
2. Ajoutez/modifiez les fichiers Markdown (`.md`) dans `docs/`
3. Personnalisez le thème en modifiant les paramètres dans `mkdocs.yml`

### Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

### Ressources utiles

- Documentation officielle MkDocs
- Thème Material for MkDocs