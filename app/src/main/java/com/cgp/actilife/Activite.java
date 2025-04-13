package com.cgp.actilife;

public class Activite {
    private String nom;
    private String heureDebut;
    private String heureFin;

    public Activite(String nom, String heureDebut, String heureFin) {
        this.nom = nom;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    public String getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(String heureFin) {
        this.heureFin = heureFin;
    }
}
