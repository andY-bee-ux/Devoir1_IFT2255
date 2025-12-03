package org.projet.model;

import java.util.List;

public class Profil {
    private String[] preferences;       //Preferences lies au profil
    private List<String> centreInterets;        //Tableaux contenant les centres d'interets de l'utilisateur.
    private String programme;    //Programme d'étude suivie par l'utilisateur.
    private String Cycle;    //Cycle d'étude associe au programme.
    private List<Cours> coursSuivies;    //Cours deja suivies par l'utilisateur.

    // Constructeur à faire.
    public Profil( ){}

    // Getters et Setters.
    public List<Cours> getCoursSuivies() {
        return coursSuivies;
    }

    public List<String> getCentreInterets() {
        return centreInterets;
    }

    public String getCycle() {
        return Cycle;
    }

    public String getProgramme() {
        return programme;
    }

    public String[] getPreferences() {
        return preferences;
    }

    public void setCentreInterets(List<String> centreInterets) {
        this.centreInterets = centreInterets;
    }

    public void setCoursSuivies(List<Cours> coursSuivies) {
        this.coursSuivies = coursSuivies;
    }

    public void setCycle(String cycle) {
        Cycle = cycle;
    }

    public void setPreferences(String[] preferences) {
        this.preferences = preferences;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }
}
