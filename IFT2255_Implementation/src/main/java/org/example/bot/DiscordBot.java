package org.example.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot {
    
    public static void main(String[] args) {
        //Token du bot Discord
        String token = "MTQ0MjI5MDczMjM3MDIzMTQwMw.GvzIN5.9_VkapkIdT8i6wu8PXphXlTH9sGTnlPsS6ZQt4";
        
        //Création du bot Discord
       JDA bot = JDABuilder.createDefault(token)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT) //Permet au bot de lire le contenu des messages
            .build();

            //Formulation de la commande "avis" avec ses options
            bot.upsertCommand("avis", "Donner un avis sur un cours ")
            .addOption(OptionType.STRING, "Cours", "Sigle (ex: IFT2255) Ou nom du cours (ex: Genie logiciel)", true )
            .addOption(OptionType.STRING, "Session", "Session durant laquelle le cours a ete suivi (ex: Hiver 2023)", true)
            .addOption(OptionType.STRING,"Professeur", "Nom du professeur ayant donne le cours", false)
            .addOption(OptionType.INTEGER,"Note","Note obtenue au cours", false)
            .addOption(OptionType.INTEGER,"Difficulte","Niveau de difficulte (1= Tres facile 5=Tres Difficile", false)
            .addOption(OptionType.INTEGER,"Qualite_Cours", "Appréciation globale (1=Mauvais, 5=Excellent)",false)
            .addOption(OptionType.INTEGER, "Charge", "Volume de Travail",false)
            .addOption(OptionType.STRING, "Commentaire", "Commentaire additionnel sur le cours",false).queue();

        System.out.println("Le bot est en train de se connecter ...");

    }


}
