package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories_activites")
public class CategorieActivite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategorie;

    private String nom;

    @Column(length = 5000)
    private String description;

    // ------------------------------------------------
    // CONSTRUCTEURS
    // ------------------------------------------------
    public CategorieActivite() {}

    public CategorieActivite(Long idCategorie, String nom, String description) {
        this.idCategorie = idCategorie;
        this.nom = nom;
        this.description = description;
    }

    // ------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------

    public Long getIdCategorie() { return idCategorie; }
    public void setIdCategorie(Long idCategorie) { this.idCategorie = idCategorie; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
