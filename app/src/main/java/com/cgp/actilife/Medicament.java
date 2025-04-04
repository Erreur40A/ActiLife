package com.cgp.actilife;

import java.io.Serializable;

public class Medicament implements Serializable {
    private String nom;
    private String type;
    private String heure;

    public Medicament(String nom, String type, String heure) {
        this.nom = nom;
        this.type = type;
        this.heure = heure;
    }

    public String getNom() {
        return nom;
    }

    public String getType() {
        return type;
    }

    public String getHeure() {
        return heure;
    }
}
