package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicule;

    private String modele;
    private String type; // 4x4, minibus, moto...

    private BigDecimal prixParJour;

    private boolean actif = true;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    public Vehicule() {
    }

    public Vehicule(Long idVehicule, String modele, String type, BigDecimal prixParJour, boolean actif, Zone zone) {
        this.idVehicule = idVehicule;
        this.modele = modele;
        this.type = type;
        this.prixParJour = prixParJour;
        this.actif = actif;
        this.zone = zone;
    }

	public Long getIdVehicule() {
		return idVehicule;
	}

	public void setIdVehicule(Long idVehicule) {
		this.idVehicule = idVehicule;
	}

	public String getModele() {
		return modele;
	}

	public void setModele(String modele) {
		this.modele = modele;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getPrixParJour() {
		return prixParJour;
	}

	public void setPrixParJour(BigDecimal prixParJour) {
		this.prixParJour = prixParJour;
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
