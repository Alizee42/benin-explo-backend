package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "activites")
public class Activite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idActivite;

    private String nom;

    @Column(length = 5000)
    private String description;

    private String ville;

    @ManyToOne
    @JoinColumn(name = "ville_id")
    private Ville villeEntity;

    // Durée interne de l'activité (en minutes, heures, etc. selon ton choix)
    private Integer dureeInterne;


    // Poids / importance pour le tri ou la mise en avant
    private Integer poids;

    // Niveau de difficulté (facile, moyen, difficile, etc.)
    private String difficulte;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @ManyToOne
    @JoinColumn(name = "image_principale_id")
    private Media imagePrincipale;

    // ----------------------------------------------------
    // CONSTRUCTEURS
    // ----------------------------------------------------

    public Activite() {
    }

    public Activite(Long idActivite,
                    String nom,
                    String description,
                    String ville,
                    Integer dureeInterne,
                    Integer poids,
                    String difficulte,
                    Zone zone,
                    Media imagePrincipale) {
        this.idActivite = idActivite;
        this.nom = nom;
        this.description = description;
        this.ville = ville;
        this.dureeInterne = dureeInterne;
        this.poids = poids;
        this.difficulte = difficulte;
        this.zone = zone;
        this.imagePrincipale = imagePrincipale;
    }

    // ----------------------------------------------------
    // GETTERS / SETTERS
    // ----------------------------------------------------

    public Long getIdActivite() {
        return idActivite;
    }

    public void setIdActivite(Long idActivite) {
        this.idActivite = idActivite;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public Integer getDureeInterne() {
        return dureeInterne;
    }

    public void setDureeInterne(Integer dureeInterne) {
        this.dureeInterne = dureeInterne;
    }
    public Integer getPoids() {
        return poids;
    }

    public void setPoids(Integer poids) {
        this.poids = poids;
    }

    public String getDifficulte() {
        return difficulte;
    }

    public void setDifficulte(String difficulte) {
        this.difficulte = difficulte;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Ville getVilleEntity() {
        return villeEntity;
    }

    public void setVilleEntity(Ville villeEntity) {
        this.villeEntity = villeEntity;
    }

    public Media getImagePrincipale() {
        return imagePrincipale;
    }

    public void setImagePrincipale(Media imagePrincipale) {
        this.imagePrincipale = imagePrincipale;
    }
}

