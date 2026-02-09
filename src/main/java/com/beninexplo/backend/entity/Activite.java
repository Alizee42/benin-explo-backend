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

    // Relation avec Ville (source unique de vérité pour la localisation)
    @ManyToOne(optional = false)
    @JoinColumn(name = "ville_id", nullable = false)
    private Ville ville;

    // Durée interne de l'activité (en minutes, heures, etc. selon ton choix)
    private Integer dureeInterne;

    // Poids / importance pour le tri ou la mise en avant
    private Integer poids;

    // Niveau de difficulté (facile, moyen, difficile, etc.)
    private String difficulte;

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
                    Ville ville,
                    Integer dureeInterne,
                    Integer poids,
                    String difficulte,
                    Media imagePrincipale) {
        this.idActivite = idActivite;
        this.nom = nom;
        this.description = description;
        this.ville = ville;
        this.dureeInterne = dureeInterne;
        this.poids = poids;
        this.difficulte = difficulte;
        this.imagePrincipale = imagePrincipale;
    }
    
    // ----------------------------------------------------
    // MÉTHODES UTILITAIRES
    // ----------------------------------------------------
    
    /**
     * Récupère la zone de l'activité via sa ville
     */
    public Zone getZone() {
        return ville != null ? ville.getZone() : null;
    }
    
    /**
     * Récupère le nom de la ville
     */
    public String getVilleNom() {
        return ville != null ? ville.getNom() : null;
    }
    
    /**
     * Validation avant insertion/mise à jour
     */
    @PrePersist
    @PreUpdate
    private void validate() {
        if (ville == null) {
            throw new IllegalStateException("Une activité doit être associée à une ville");
        }
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

    public Ville getVille() {
        return ville;
    }

    public void setVille(Ville ville) {
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

    public Media getImagePrincipale() {
        return imagePrincipale;
    }

    public void setImagePrincipale(Media imagePrincipale) {
        this.imagePrincipale = imagePrincipale;
    }
}

