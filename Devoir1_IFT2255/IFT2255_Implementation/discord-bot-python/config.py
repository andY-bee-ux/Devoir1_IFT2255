import os
from dotenv import load_dotenv

# Charge les variables du fichier .env
load_dotenv()

# Token du bot Discord
DISCORD_TOKEN = os.getenv("DISCORD_TOKEN")

# URL de base de l'API Java (backend du projet)
API_BASE_URL = os.getenv("API_BASE_URL", "http://172.17.0.1:7000")

GUILD_ID = int(os.getenv("GUILD_ID", 0))

# Petit check pour éviter d'oublier le token
if not DISCORD_TOKEN:
    raise ValueError("Le token Discord (DISCORD_TOKEN) est manquant dans le fichier .env")
