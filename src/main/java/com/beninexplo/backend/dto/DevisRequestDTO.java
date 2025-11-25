package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DevisRequestDTO {

    private Long utilisateurId; // optionnel : null si visiteur non connecté

    private String formule;

    private Integer dureeCircuit;

    private String dateDebutCircuit; // format yyyy-MM-dd

    private Integer nbAdultes;
    private Integer nbEnfants;
	public Long getUtilisateurId() {
		return utilisateurId;
	}
	public void setUtilisateurId(Long utilisateurId) {
		this.utilisateurId = utilisateurId;
	}
	public String getFormule() {
		return formule;
	}
	public void setFormule(String formule) {
		this.formule = formule;
	}
	public Integer getDureeCircuit() {
		return dureeCircuit;
	}
	public void setDureeCircuit(Integer dureeCircuit) {
		this.dureeCircuit = dureeCircuit;
	}
	public String getDateDebutCircuit() {
		return dateDebutCircuit;
	}
	public void setDateDebutCircuit(String dateDebutCircuit) {
		this.dateDebutCircuit = dateDebutCircuit;
	}
	public Integer getNbAdultes() {
		return nbAdultes;
	}
	public void setNbAdultes(Integer nbAdultes) {
		this.nbAdultes = nbAdultes;
	}
	public Integer getNbEnfants() {
		return nbEnfants;
	}
	public void setNbEnfants(Integer nbEnfants) {
		this.nbEnfants = nbEnfants;
	}
    
    
    
}
