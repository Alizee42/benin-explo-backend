package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
public class Activite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idActivite;

    private String nom;

    @Column(length = 5000)
    private String description;

    private String ville;

    private Integer dureeInterne;          // durée indicative interne
    private Integer distanceDepuisCotonou; // km
    private Integer poids;                 // 1 proche / 2 moyen / 3 loin
    private String difficulte;

    private boolean actif = true;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private CategorieActivite categorie;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @ManyToOne
    @JoinColumn(name = "image_principale_id")
    private Media imagePrincipale;

    public Activite() {
    }

    public Activite(Long idActivite, String nom, String description, String ville, Integer dureeInterne, Integer distanceDepuisCotonou, Integer poids, String difficulte, boolean actif, CategorieActivite categorie, Zone zone, Media imagePrincipale) {
        this.idActivite = idActivite;
        this.nom = nom;
        this.description = description;
        this.ville = ville;
        this.dureeInterne = dureeInterne;
        this.distanceDepuisCotonou = distanceDepuisCotonou;
        this.poids = poids;
        this.difficulte = difficulte;
        this.actif = actif;
        this.categorie = categorie;
        this.zone = zone;
        this.imagePrincipale = imagePrincipale;
    }

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

	public Integer getDistanceDepuisCotonou() {
		return distanceDepuisCotonou;
	}

	public void setDistanceDepuisCotonou(Integer distanceDepuisCotonou) {
		this.distanceDepuisCotonou = distanceDepuisCotonou;
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

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

	public CategorieActivite getCategorie() {
		return categorie;
	}

	public void setCategorie(CategorieActivite categorie) {
		this.categorie = categorie;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public Media getImagePrincipale() {
		return imagePrincipale;
	}

	public void setImagePrincipale(Media imagePrincipale) {
		this.imagePrincipale = imagePrincipale;
	}
    
    
}
