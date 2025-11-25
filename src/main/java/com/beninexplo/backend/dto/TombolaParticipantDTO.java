package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TombolaParticipantDTO {

    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    private String dateParticipation;
    private String statut;
    private String codeUnique;
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
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getDateParticipation() {
		return dateParticipation;
	}
	public void setDateParticipation(String dateParticipation) {
		this.dateParticipation = dateParticipation;
	}
	public String getStatut() {
		return statut;
	}
	public void setStatut(String statut) {
		this.statut = statut;
	}
	public String getCodeUnique() {
		return codeUnique;
	}
	public void setCodeUnique(String codeUnique) {
		this.codeUnique = codeUnique;
	}
    
    
}
