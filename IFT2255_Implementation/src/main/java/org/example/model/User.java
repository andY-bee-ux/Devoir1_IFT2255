package org.example.model;

public class User {
    private int id;     //Id de l'utlisateur
    private Profil profil;      //Profil lie a l'utilisateur.

    // Constructeur à faire.
    public User(){}

    // Getters et Setters.
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Profil getProfil() {
        return profil;
    }

    public void setProfil(Profil profil) {
        this.profil = profil;
    }
}
