package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicules")
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String marque;
    private String modele;
    private String matricule;
    private int annee;
    private boolean disponible;

    /* ----------------------
       CONSTRUCTEURS
    ----------------------- */
    public Vehicule() {}

    public Vehicule(Long id, String marque, String modele, String matricule, int annee, boolean disponible) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.matricule = matricule;
        this.annee = annee;
        this.disponible = disponible;
    }

    /* ----------------------
       GETTERS & SETTERS
    ----------------------- */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
