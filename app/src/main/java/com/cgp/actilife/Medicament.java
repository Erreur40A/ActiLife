package com.cgp.actilife;

import java.io.Serializable;

public class Medicament implements Serializable {
    private String nom;
    private String heure;

    public Medicament(String nom, String heure) {
        this.nom = nom;
        this.heure = heure;
    }

    public String getNom() {
        return nom;
    }

    public String getHeure() {
        return heure;
    }
}
