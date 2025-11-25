package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "hebergements")
public class Hebergement {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
    ---------------------------------------------------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHebergement;

    private String nom;
    private String type; // hôtel, maison d'hôte, auberge, lodge...
    private String localisation;

    @Column(length = 5000)
    private String description;

    private double prixParNuit;

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */
    public Hebergement() {}

    public Hebergement(Long idHebergement, String nom, String type, String localisation,
                       String description, double prixParNuit) {
        this.idHebergement = idHebergement;
        this.nom = nom;
        this.type = type;
        this.localisation = localisation;
        this.description = description;
        this.prixParNuit = prixParNuit;
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */
    public Long getIdHebergement() { return idHebergement; }
    public void setIdHebergement(Long idHebergement) { this.idHebergement = idHebergement; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrixParNuit() { return prixParNuit; }
    public void setPrixParNuit(double prixParNuit) { this.prixParNuit = prixParNuit; }
}
