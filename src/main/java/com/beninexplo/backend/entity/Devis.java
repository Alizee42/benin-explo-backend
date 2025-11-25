package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Devis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDevis;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur; // optionnel (client non connecté)

    private String formule; // circuit / circuit+transport / tout compris

    private Integer dureeCircuit;       // en jours
    private LocalDate dateDebutCircuit; 
    private LocalDate dateFinCircuit;   // optionnel (calculé côté backend)

    private Integer nbAdultes;
    private Integer nbEnfants;
    private Integer nbParticipants;      // helpers

    private LocalDateTime dateDemande = LocalDateTime.now();
    private String statut = "en_attente";

    @Column(length = 2000)
    private String commentaireInterne;

    public Devis() {
    }

    public Devis(Long idDevis, Utilisateur utilisateur, String formule, Integer dureeCircuit, LocalDate dateDebutCircuit, LocalDate dateFinCircuit, Integer nbAdultes, Integer nbEnfants, Integer nbParticipants, LocalDateTime dateDemande, String statut, String commentaireInterne) {
        this.idDevis = idDevis;
        this.utilisateur = utilisateur;
        this.formule = formule;
        this.dureeCircuit = dureeCircuit;
        this.dateDebutCircuit = dateDebutCircuit;
        this.dateFinCircuit = dateFinCircuit;
        this.nbAdultes = nbAdultes;
        this.nbEnfants = nbEnfants;
        this.nbParticipants = nbParticipants;
        this.dateDemande = dateDemande;
        this.statut = statut;
        this.commentaireInterne = commentaireInterne;
    }

	public Long getIdDevis() {
		return idDevis;
	}

	public void setIdDevis(Long idDevis) {
		this.idDevis = idDevis;
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
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

	public LocalDate getDateDebutCircuit() {
		return dateDebutCircuit;
	}

	public void setDateDebutCircuit(LocalDate dateDebutCircuit) {
		this.dateDebutCircuit = dateDebutCircuit;
	}

	public LocalDate getDateFinCircuit() {
		return dateFinCircuit;
	}

	public void setDateFinCircuit(LocalDate dateFinCircuit) {
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

	public LocalDateTime getDateDemande() {
		return dateDemande;
	}

	public void setDateDemande(LocalDateTime dateDemande) {
		this.dateDemande = dateDemande;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public String getCommentaireInterne() {
		return commentaireInterne;
	}

	public void setCommentaireInterne(String commentaireInterne) {
		this.commentaireInterne = commentaireInterne;
	}

 
}
