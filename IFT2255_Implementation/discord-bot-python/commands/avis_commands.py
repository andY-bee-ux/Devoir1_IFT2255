import discord
from discord import app_commands
from discord.ext import commands
from api_client import AvisClient
from config import GUILD_ID  

class AvisCommands(commands.Cog):
    def __init__(self, bot):
        self.bot = bot
        self.avis_client = AvisClient()

    @app_commands.command(
        name="avis",
        description="Donner un avis sur un cours"
    )
    @app_commands.describe(
        cours="Nom ou sigle du cours (ex: IFT2255)",
        difficulte="Difficulté perçue du cours (1 = facile, 5 = très difficile)",
        qualite_cours="Qualité générale du cours (1 = mauvais, 5 = excellent)",
        charge="Charge de travail (1 = faible, 5 = très élevée)",
        commentaire="Commentaire libre sur le cours",
        note_obtenue="Note obtenue (optionnel)",
        session="Session (optionnelle)",
        professeur="Nom du professeur (optionnel)",
    )
    async def avis(
        self,
        interaction: discord.Interaction,
        cours: str,
        difficulte: app_commands.Range[int, 1, 5],
        qualite_cours: app_commands.Range[int, 1, 5],
        charge:  app_commands.Range[int, 1, 5],
        commentaire: str,
        note_obtenue: app_commands.Range[int, 0, 100] = None,
        session: str | None = None,
        professeur: str | None = None,
    ):
        """
        Gère la commande /avis.
        """
        
        # Validation du format de la session (si fournie)
        if session:
            session = session.strip().upper()
            if len(session) != 5 or session[0] not in ['H', 'A', 'E'] or not session[1:].isdigit():
                await interaction.response.send_message(
                    "La session doit être au format XYYYY (ex: H2023 , E2025, A2020).",
                    ephemeral=True
                )
                return

        # Construction de l'avis en JSON
        avis_json = {
            "cours": cours,
            "difficulte": difficulte,
            "qualite_cours": qualite_cours,
            "charge": charge,
            "commentaire": commentaire,
            "note_obtenue": note_obtenue,
            "session": session,
            "professeur": professeur,
        }

        # Envoi de l'avis à l'API via le client
        success, message = self.avis_client.envoyer_avis(avis_json)
        
        if not success:
            await interaction.response.send_message(message, ephemeral=True)
            return

        # Message de confirmation à l'utilisateur
        resume = (
            f"✅ Merci pour ton avis sur **{cours}**"
            f"{f' ({session})' if session else ''}"
            f"{f' avec **{professeur}**' if professeur else ''}.\n"
            f"- Difficulté : {difficulte}/5\n"
            f"- Qualité du cours : {qualite_cours}/5\n"
            f"- Charge de travail : {charge}/5\n"
        )

        if note_obtenue is not None:
            resume += f"- Note obtenue : {note_obtenue}\n"

        if commentaire:
            resume += f"- Commentaire : {commentaire}\n"

        await interaction.response.send_message(resume, ephemeral=True)

async def setup(bot):
    # On enregistre le Cog sur le serveur spécifique (GUILD_ID)
    await bot.add_cog(AvisCommands(bot), guild=discord.Object(id=GUILD_ID))
