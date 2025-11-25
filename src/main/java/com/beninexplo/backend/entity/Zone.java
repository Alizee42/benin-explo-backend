package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "zones")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idZone;

    private String nom;
    private String description;

    /* ----------------------
       CONSTRUCTEURS
    ----------------------- */
    public Zone() {}

    public Zone(Long idZone, String nom, String description) {
        this.idZone = idZone;
        this.nom = nom;
        this.description = description;
    }

    /* ----------------------
       GETTERS & SETTERS
    ----------------------- */

    public Long getIdZone() { return idZone; }
    public void setIdZone(Long idZone) { this.idZone = idZone; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
