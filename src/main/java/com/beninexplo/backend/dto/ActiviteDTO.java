package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ActiviteDTO {

    private Long id;
    private String nom;
    private String description;
    
    // Informations de localisation
    private Long villeId;
    private String villeNom;
    private Long zoneId;
    private String zoneNom;

    private Integer dureeInterne;
    private Integer poids;
    private String difficulte;

    private Long imagePrincipaleId;
    private String imagePrincipaleUrl;

    // ----------------------------------------------------
    // CONSTRUCTEURS
    // ----------------------------------------------------

    public ActiviteDTO() {
    }

    public ActiviteDTO(Long id,
                       String nom,
                       String description,
                       Long villeId,
                       String villeNom,
                       Long zoneId,
                       String zoneNom,
                       Integer dureeInterne,
                       Integer poids,
                       String difficulte,
                       Long imagePrincipaleId,
                       String imagePrincipaleUrl) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.villeId = villeId;
        this.villeNom = villeNom;
        this.zoneId = zoneId;
        this.zoneNom = zoneNom;
        this.dureeInterne = dureeInterne;
        this.poids = poids;
        this.difficulte = difficulte;
        this.imagePrincipaleId = imagePrincipaleId;
        this.imagePrincipaleUrl = imagePrincipaleUrl;
    }

    // ----------------------------------------------------
    // GETTERS / SETTERS
    // ----------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getVilleId() { 
        return villeId; 
    }
    
    public void setVilleId(Long villeId) { 
        this.villeId = villeId; 
    }
    
    public String getVilleNom() { 
        return villeNom; 
    }
    
    public void setVilleNom(String villeNom) { 
        this.villeNom = villeNom; 
    }
    
    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }
    
    public String getZoneNom() {
        return zoneNom;
    }

    public void setZoneNom(String zoneNom) {
        this.zoneNom = zoneNom;
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

    public Long getImagePrincipaleId() {
        return imagePrincipaleId;
    }

    public void setImagePrincipaleId(Long imagePrincipaleId) {
        this.imagePrincipaleId = imagePrincipaleId;
    }

    public String getImagePrincipaleUrl() { return imagePrincipaleUrl; }
    public void setImagePrincipaleUrl(String imagePrincipaleUrl) { this.imagePrincipaleUrl = imagePrincipaleUrl; }
}
