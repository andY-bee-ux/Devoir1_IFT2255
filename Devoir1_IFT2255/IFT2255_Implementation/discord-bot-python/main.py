import discord
from discord.ext import commands
from config import DISCORD_TOKEN, GUILD_ID

# Intents
intents = discord.Intents.default()

class MonBot(commands.Bot):
    def __init__(self):
        super().__init__(command_prefix="!", intents=intents)

    async def setup_hook(self):
        """
        Cette méthode est appelée au démarrage.
        C'est ici qu'on charge les extensions (les fichiers dans commands/).
        """
        # Charger l'extension 'avis_commands' qui est dans le dossier 'commands'
        await self.load_extension("commands.avis_commands")
        
        # Synchroniser les commandes slash avec le serveur
        # (Dans un vrai prod, on synchronise globalement, mais ici localement pour la rapidité)
        guild = discord.Object(id=GUILD_ID)
        self.tree.copy_global_to(guild=guild)
        synced = await self.tree.sync(guild=guild)
        
        print(f"✅ {len(synced)} commande(s) synchronisée(s) pour le serveur {GUILD_ID}.")

bot = MonBot()

@bot.event
async def on_ready():
    print(f"✅ Bot connecté en tant que {bot.user} (ID: {bot.user.id}) [cite: 7]")

if __name__ == "__main__":
    bot.run(DISCORD_TOKEN)