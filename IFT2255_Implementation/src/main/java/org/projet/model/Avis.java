package org.projet.model;

public class Avis {
    private int id;     //Id associe a l'avis.
    private String contenu;     //Contenu de l'avis.
    private int idAuteur;       //Id de la personne ayant poster l'avis
    private int noteDifficulte;     //Notes attibue pour la difficulte du cours
    private Cours coursAssocie;     //Cours associe a l'avis.
    private int noteQualite;        //Note attibue pour la qualite du cours
    private boolean valide;         //Permet de verifier si un avis est conforme aux standart (suit le format...)
    private int noteChargeTravail;     //

    //Constructeur à faire.
    public Avis(){}

    //Getters et Setters.
    public void setNoteQualite(int noteQualite) {
        this.noteQualite = noteQualite;
    }

    public void setNoteDifficulte(int noteDifficulte) {
        this.noteDifficulte = noteDifficulte;
    }

    public void setIdAuteur(int idAuteur) {
        this.idAuteur = idAuteur;
    }

    public String getContenu() {
        return contenu;
    }

    public int getNoteDifficulte() {
        return noteDifficulte;
    }

    public int getNoteQualite() {
        return noteQualite;
    }

    public int getId() {
        return id;
    }

    public void setNoteChargeTravail(int noteChargeTravail) {
        this.noteChargeTravail = noteChargeTravail;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public int getNoteChargeTravail() {
        return noteChargeTravail;
    }

    public int getIdAuteur() {
        return idAuteur;
    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

    public Cours getCoursAssocie() {
        return coursAssocie;
    }

    public void setCoursAssocie(Cours coursAssocie) {
        this.coursAssocie = coursAssocie;
    }
}
