#  Bot Discord – Avis Étudiants

Ce dossier contient le **bot Discord** permettant aux étudiants de soumettre des avis sur leurs cours via la commande `/avis`.  
Les avis sont ensuite envoyés automatiquement vers le **backend Java (API)** du projet.

---

##  Structure du dossier

```
discord-bot-python/
├── main.py                # Point d'entrée du bot
├── config.py              # Gestion des variables d'environnement (.env)
├── api_client.py          # Envoi des avis vers l'API Java
├── commands/
│   └── avis_commands.py   # Commande slash /avis
├── venv/                  # Environnement virtuel Python (local)
└── .env                   # Variables sensibles (token, API, guilde)
```

---

##  Prérequis

- **Python 3.10+** installé
- **pip** installé
- **API Java** démarrée sur `http://172.17.0.1:7000`
- **Bot Discord** créé et configuré sur le [Discord Developer Portal](https://discord.com/developers/applications)

---
## Installation

1. **Cloner le dépôt :**
   ```bash
   git clone <URL_DU_DEPOT>
   cd Devoir1_IFT2255/IFT2255_Implementation/discord-bot-python
   ```

2. **Créer un environnement virtuel :**
   ```bash
   python -m venv venv
   ```

3. **Activer l’environnement virtuel :**
   - Sous **PowerShell (Windows)** :
     ```bash
     venv\Scripts\Activate.ps1
     ```
   - Sous **cmd** :
     ```bash
     venv\Scripts\activate.bat
     ```

4. **Installer les dépendances :**
   ```bash
   pip install discord.py python-dotenv requests
   ```

---

## Configuration du fichier `.env`

Crée un fichier `.env` à la racine du dossier avec les informations suivantes :

```env
DISCORD_TOKEN=TON_TOKEN_DISCORD_ICI
API_BASE_URL=http://172.17.0.1:7000
GUILD_ID=1442296329740226774
```

### Explication
- `DISCORD_TOKEN` : ton token de bot (à récupérer sur Discord Developer Portal).
- `API_BASE_URL` : URL de ton backend Java.
- `GUILD_ID` : ID de ton serveur Discord (clic droit → **Copier l’identifiant**).

---

##  Lancer le projet

### 1️-Démarrer le backend Java (API)

Ouvrir un terminal dans le dossier qui contient `pom.xml`, puis :
```bash
mvn spring-boot:run
```

Si tout va bien :
```
Tomcat started on port(s): 7000 (http)
```

l'API tourne sur `http://172.17.0.1:7000`

---

### 2- Lancer le bot Discord

Dans un autre terminal :
```bash
cd discord-bot-python
venv\Scripts\Activate.ps1
python main.py
```

Il y aura :
```
✅ Bot connecté en tant que Avis-Bot#XXXX (ID: ...)
✅ 1 commande(s) slash synchronisée(s)
```

---

##  Utilisation dans Discord

1. Ouvrir ton **serveur Discord** (celui du `GUILD_ID`).
2. Taper `/avis` → Discord ouvrira un **formulaire** avec les champs :

| Champ | Type | Obligatoire | Description |
|-------|------|--------------|-------------|
| cours | texte | ✅ | Nom/sigle du cours (ex: IFT2255) |
| difficulte | entier | ✅ | Difficulté perçue (1 à 5) |
| qualite_cours | entier | ✅ | Qualité du cours (1 à 5) |
| charge | entier | ✅ | Charge de travail (1 à 5) |
| commentaire | texte | ✅ | Ton avis personnel |
| note_obtenue | entier | ❌ | Note entre 0 et 100 |
| session | texte | ❌ | Format `H2025`, `A2024`, `E2023` |
| professeur | texte | ❌ | Nom du professeur |

---

##  Exemple de réponse du bot

Une fois le formulaire soumis :
```
✅ Merci pour ton avis sur IFT2255 (H2025) avec Dupont.
- Difficulté : 3/5
- Qualité du cours : 4/5
- Charge de travail : 3/5
- Note obtenue : 85
- Commentaire : super cours !
```

>  Ce message est **éphémère** (visible uniquement par l’étudiant).

---
## Côté API (backend Java)

Quand un avis est envoyé, tu verras dans la console :
```bash
Avis reçu: {"cours": "IFT2255", "difficulte": 3, "qualite_cours": 4, "charge": 3, "commentaire": "super cours", "note_obtenue": 85, "session": "H2025", "professeur": "Dupont"}
```

---

##  Dépannage

| Message du bot | Cause probable | Solution |
|----------------|----------------|-----------|
| `Erreur serveur : 404.` | Mauvaise route API (`/api/avis` au lieu de `/avis`) | Corriger `API_BASE_URL` dans `.env` |
| `Impossible de contacter l'API.` | L’API Java n’est pas lancée | Lancer `mvn spring-boot:run` |
| `Le token Discord est manquant` | Fichier `.env` mal configuré | Vérifie la clé `DISCORD_TOKEN` |
| `La session doit être au format XYYYY` | Format invalide | Exemples valides : `H2024`, `A2025`, `E2023` |

---


