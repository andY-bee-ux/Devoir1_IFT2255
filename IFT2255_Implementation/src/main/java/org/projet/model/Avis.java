package org.projet.model;

public class Avis {
    //private int id;     //Id associe a l'avis (necessaire?)
    private String commentaire;
    //private int idAuteur;
    private int noteDifficulte;
    private String sigleCours;
    private int noteQualite;
    private boolean valide;
    private int noteChargeTravail;
    private String nomProfesseur;

    public Avis(){}

    public Avis( String sigleCours, String nomProfesseur, int noteQualite, int noteDifficulte, String commentaire, boolean valide) {
        this.commentaire = commentaire;
        this.noteDifficulte = noteDifficulte;
        this.sigleCours = sigleCours;
        this.noteQualite = noteQualite;
        this.valide = valide;
        this.noteChargeTravail = noteChargeTravail;
        this.nomProfesseur = nomProfesseur;
    }

    public String getSigleCours() {
        return sigleCours;
    }

    public void setSigleCours(String sigleCours) {
        this.sigleCours = sigleCours;
    }

    public String getNomProfesseur() {
        return nomProfesseur;
    }

    public void setNomProfesseur(String nomProfesseur) {
        this.nomProfesseur = nomProfesseur;
    }

    //Getters et Setters.
    public void setNoteQualite(int noteQualite) {
        this.noteQualite = noteQualite;
    }

    public void setNoteDifficulte(int noteDifficulte) {
        this.noteDifficulte = noteDifficulte;
    }

//    public void setIdAuteur(int idAuteur) {
//        this.idAuteur = idAuteur;
//    }

    public String getCommentaire() {
        return commentaire;
    }

    public int getNoteDifficulte() {
        return noteDifficulte;
    }

    public int getNoteQualite() {
        return noteQualite;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setNoteChargeTravail(int noteChargeTravail) {
//        this.noteChargeTravail = noteChargeTravail;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int getNoteChargeTravail() {
        return noteChargeTravail;
    }

//    public int getIdAuteur() {
//        return idAuteur;
//    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

//    public Cours getCoursAssocie() {
//        return coursAssocie;
//    }
//
//    public void setCoursAssocie(Cours coursAssocie) {
//        this.coursAssocie = coursAssocie;
//    }
}
