package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DevisResponseDTO {

    private Long id;

    private String formule;
    private Integer dureeCircuit;

    private String dateDebutCircuit;
    private String dateFinCircuit;

    private Integer nbAdultes;
    private Integer nbEnfants;
    private Integer nbParticipants;

    private String statut;
    private String dateDemande;

    private Long utilisateurId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDateFinCircuit() {
		return dateFinCircuit;
	}

	public void setDateFinCircuit(String dateFinCircuit) {
		this.dateFinCircuit = dateFinCircuit;
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

	public Integer getNbParticipants() {
		return nbParticipants;
	}

	public void setNbParticipants(Integer nbParticipants) {
		this.nbParticipants = nbParticipants;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public String getDateDemande() {
		return dateDemande;
	}

	public void setDateDemande(String dateDemande) {
		this.dateDemande = dateDemande;
	}

	public Long getUtilisateurId() {
		return utilisateurId;
	}

	public void setUtilisateurId(Long utilisateurId) {
		this.utilisateurId = utilisateurId;
	}
    
    
    
}
