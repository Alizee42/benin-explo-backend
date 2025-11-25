package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Hebergement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHebergement;

    private String nom;
    private String type; // hôtel, lodge, auberge…
    
    @Column(length = 2000)
    private String description;

    private BigDecimal prixParNuit;

    private boolean actif = true;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    public Hebergement() {
    }

    public Hebergement(Long idHebergement, String nom, String type, String description, BigDecimal prixParNuit, boolean actif, Zone zone) {
        this.idHebergement = idHebergement;
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.prixParNuit = prixParNuit;
        this.actif = actif;
        this.zone = zone;
    }

	public Long getIdHebergement() {
		return idHebergement;
	}

	public void setIdHebergement(Long idHebergement) {
		this.idHebergement = idHebergement;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrixParNuit() {
		return prixParNuit;
	}

	public void setPrixParNuit(BigDecimal prixParNuit) {
		this.prixParNuit = prixParNuit;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}
    
}
