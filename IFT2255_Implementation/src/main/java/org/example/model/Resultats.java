package org.example.model;

public class Resultats {
    private int nombreInscrits;     //Nbrs d'inscrits dans le cours
    private int nombreEchecs;       // Nbrs d'etudiants ayant echoues.
    private float moyenne;      // Moyenne generale du cours.
    private String trimestre;       // Trimestre dont sont issue les resultats.

    // Constructeur à faire.
    public Resultats(){}

    // Getters et Setters.
    public float getMoyenne() {
        return moyenne;
    }

    public int getNombreEchecs() {
        return nombreEchecs;
    }

    public int getNombreInscrits() {
        return nombreInscrits;
    }

    public String getTrimestre() {
        return trimestre;
    }

    public void setMoyenne(float moyenne) {
        this.moyenne = moyenne;
    }

    public void setNombreEchecs(int nombreEchecs) {
        this.nombreEchecs = nombreEchecs;
    }

    public void setNombreInscrits(int nombreInscrits) {
        this.nombreInscrits = nombreInscrits;
    }

    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
    }
}
