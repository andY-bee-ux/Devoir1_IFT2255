package org.example.model;

import java.util.List;

public class Cours {
    private int id;     //Id du cours
    private String description;     //Description du cours
    private String scheduledSemester;   //Trimestres où le cours est offert
    private boolean includeSchedule;   //
    private List<Cours> prerequis;      //Cours prerequis.
    private String udemWebsite;         //Site Web attache au cours.
    private int credits;                //Nbrs de credits

    //Constructeur à faire.
    public Cours() {}

    //Getters et Setters.
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isIncludeSchedule() {
        return includeSchedule;
    }

    public List<Cours> getPrerequis() {
        return prerequis;
    }

    public int getCredits() {
        return credits;
    }

    public String getDescription() {
        return description;
    }

    public String getScheduledSemester() {
        return scheduledSemester;
    }

    public String getUdemWebsite() {
        return udemWebsite;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIncludeSchedule(boolean includeSchedule) {
        this.includeSchedule = includeSchedule;
    }

    public void setPrerequis(List<Cours> prerequis) {
        this.prerequis = prerequis;
    }

    public void setScheduledSemester(String scheduledSemester) {
        this.scheduledSemester = scheduledSemester;
    }

    public void setUdemWebsite(String udemWebsite) {
        this.udemWebsite = udemWebsite;
    }
}
