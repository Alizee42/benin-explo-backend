package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "villes")
public class Ville {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVille;

    private String nom;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    // ---------------------------------------
    // CONSTRUCTEURS
    // ---------------------------------------
    public Ville() {}

    public Ville(Long idVille, String nom, Zone zone) {
        this.idVille = idVille;
        this.nom = nom;
        this.zone = zone;
    }

    // ---------------------------------------
    // GETTERS / SETTERS
    // ---------------------------------------
    public Long getIdVille() { return idVille; }
    public void setIdVille(Long idVille) { this.idVille = idVille; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Zone getZone() { return zone; }
    public void setZone(Zone zone) { this.zone = zone; }
}