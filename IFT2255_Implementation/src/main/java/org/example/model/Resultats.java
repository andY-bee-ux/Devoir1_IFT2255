package org.example.model;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.*;

/**
 * La classe Resultats gère l'extraction et l'affichage des données académiques 
 * à partir d'un fichier CSV d'historique.
 */

public class Resultats {
    private String sigleCours;
    private String nom;
    private String moyenne;
    private double score;
    private int participants;
    private int trimestre;


    

    /**
     * Constructeur : Charge les données du cours depuis le CSV dès l'instanciation.
     * @param sigleCours Le code du cours (ex: "IFT1015") à rechercher.
     */
    public Resultats( String sigleCours) {
        this.sigleCours = sigleCours.trim().toUpperCase();
        Map<String, List<String>> resultats = transformCSVToList();

        
     
        // Vérification si le CSV a été chargé correctement
        if (resultats.isEmpty()) {
            System.err.println("Aucun résultat n'a été chargé depuis le fichier CSV.");
        }


        
        // Vérification de la présence du cours dans les données chargées
        if(resultats.containsKey(this.sigleCours)) {

        List<String> reList = resultats.get(this.sigleCours);    
        
        
        this.nom = reList.get(0);
        this.moyenne = reList.get(1);
        this.score = Double.parseDouble(reList.get(2));
        this.participants = Integer.parseInt(reList.get(3));
        this.trimestre = Integer.parseInt(reList.get(4));
    }else{
        // Valeurs par défaut si le cours n'est pas trouvé dans le CSV
        this.nom = "";
        this.moyenne = "";
        this.score = 0.0;
        this.participants = 0;
        this.trimestre = 0;
    }
}




    /**
     * Lit le fichier CSV depuis les ressources et transforme chaque ligne en une entrée dans une Map.
     * @return Une Map où la clé est le sigle et la valeur est la liste des informations du cours.
     */
    public Map<String, List<String>> transformCSVToList() {
        Map<String, List<String>> ResultatsMap = new HashMap<>();
        // charge le fichier CSV depuis les ressources
        InputStream is = getClass().getClassLoader().getResourceAsStream("historique_cours_prog_117510.csv");
        if (is == null) {
            System.err.println("Erreur : Le fichier CSV 'historique_cours_prog_117510.csv' n'a pas été trouvé dans les ressources.");
            return ResultatsMap;}  //retourne une map vide
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            br.readLine();  // Ignore la ligne d'en-tête (Sigle, Nom, etc.)

            while ((line = br.readLine()) != null) {
                String delimiter = line.contains(";") ? ";" : ",";
                String[] row = line.split(delimiter);
                
                if (row.length >= 5) {
                    // Nettoyage des guillemets et espaces
                    String key = row[0].replace("\"", "").trim().toUpperCase();
                    
                    
                    // Extraction des autres valeurs
                    List<String> values = new ArrayList<>();
                    for (int i = 1; i < row.length; i++) {
                        values.add(row[i].replace("\"", "").trim());
                    }
                    
                    ResultatsMap.put(key, values);
            
   
              
              
          }
            
        }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier CSV : " + e.getMessage());
            e.printStackTrace();
        }
      return ResultatsMap;
    }
  
    public String getNom() {
        return nom;
    }   
    public String getMoyenne() {
        return moyenne;
    }   
    public double getScore() {
        return score;
    }   
    public int getParticipants() {
        return participants;
    }   
    public int getTrimestre() {
        return trimestre;
    } 
    
    /** @return Indique si le cours a été trouvé lors du chargement. */
    public boolean isCoursPresent() {
        return this.nom != null && !this.nom.isEmpty();
    
    }   

    /** @return Une chaîne formatée affichant tous les détails du cours. */
    public String voirResultats() {
        if (!isCoursPresent()) {
            return "Désolé ! Nous n'avons trouvé aucun résultat pour le cours " + this.sigleCours + 
               ". Vérifiez que le sigle est correct.";
        }
        return "Résultats pour le cours " + this.sigleCours + " - " + this.nom + " :\n" +
               "Moyenne : " + this.moyenne + "\n" +
               "Score : " + this.score + "\n" +
               "Participants : " + this.participants + "\n" +
               "Trimestre : " + this.trimestre;
    }


}
