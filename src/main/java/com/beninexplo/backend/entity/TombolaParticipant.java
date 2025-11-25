package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TombolaParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParticipant;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    private LocalDateTime dateParticipation = LocalDateTime.now();

    private String statut = "valide";  
    private String codeUnique;         // généré automatiquement

    public TombolaParticipant() {
    }

    public TombolaParticipant(Long idParticipant, String nom, String prenom, String email, String telephone, LocalDateTime dateParticipation, String statut, String codeUnique) {
        this.idParticipant = idParticipant;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.dateParticipation = dateParticipation;
        this.statut = statut;
        this.codeUnique = codeUnique;
    }

	public Long getIdParticipant() {
		return idParticipant;
	}

	public void setIdParticipant(Long idParticipant) {
		this.idParticipant = idParticipant;
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

	public LocalDateTime getDateParticipation() {
		return dateParticipation;
	}

	public void setDateParticipation(LocalDateTime dateParticipation) {
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
