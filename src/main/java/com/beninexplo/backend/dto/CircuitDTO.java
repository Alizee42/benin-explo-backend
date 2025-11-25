package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.math.BigDecimal;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CircuitDTO {

    private Long id;
    private String nom;
    private String description;

    private String dureeIndicative;
    private BigDecimal prixIndicatif;
    private String formuleProposee;
    private String niveau;

    private boolean actif;

    private Long zoneId;
    private Long imagePrincipaleId;
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
	public String getDureeIndicative() {
		return dureeIndicative;
	}
	public void setDureeIndicative(String dureeIndicative) {
		this.dureeIndicative = dureeIndicative;
	}
	public BigDecimal getPrixIndicatif() {
		return prixIndicatif;
	}
	public void setPrixIndicatif(BigDecimal prixIndicatif) {
		this.prixIndicatif = prixIndicatif;
	}
	public String getFormuleProposee() {
		return formuleProposee;
	}
	public void setFormuleProposee(String formuleProposee) {
		this.formuleProposee = formuleProposee;
	}
	public String getNiveau() {
		return niveau;
	}
	public void setNiveau(String niveau) {
		this.niveau = niveau;
	}
	public boolean isActif() {
		return actif;
	}
	public void setActif(boolean actif) {
		this.actif = actif;
	}
	public Long getZoneId() {
		return zoneId;
	}
	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}
	public Long getImagePrincipaleId() {
		return imagePrincipaleId;
	}
	public void setImagePrincipaleId(Long imagePrincipaleId) {
		this.imagePrincipaleId = imagePrincipaleId;
	}
    
    
}
