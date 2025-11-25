package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class HebergementDTO {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
    ---------------------------------------------------- */
    private Long id;
    private String nom;
    private String type;
    private String localisation;
    private String description;
    private double prixParNuit;

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */
    public HebergementDTO() {}

    public HebergementDTO(Long id, String nom, String type, String localisation,
                          String description, double prixParNuit) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.localisation = localisation;
        this.description = description;
        this.prixParNuit = prixParNuit;
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
