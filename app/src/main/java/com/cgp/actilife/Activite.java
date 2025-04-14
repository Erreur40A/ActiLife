package com.cgp.actilife;

import java.util.List;

public class Activite {
    private String nom;
    private String heureDebut;
    private String heureFin;
    private List<String> jours;

    public Activite(String nom, String heureDebut, String heureFin, List<String> jours) {
        this.nom = nom;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.jours= jours;
    }

    public List<String> getJours() {
        return jours;
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
