package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
public class ClientDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClientDemande;

    @ManyToOne
    @JoinColumn(name = "devis_id")
    private Devis devis;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    @Column(length = 5000)
    private String message;

    public ClientDemande() {
    }

    public ClientDemande(Long idClientDemande, Devis devis, String nom, String prenom, String email, String telephone, String message) {
        this.idClientDemande = idClientDemande;
        this.devis = devis;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.message = message;
    }

	public Long getIdClientDemande() {
		return idClientDemande;
	}

	public void setIdClientDemande(Long idClientDemande) {
		this.idClientDemande = idClientDemande;
	}

	public Devis getDevis() {
		return devis;
	}

	public void setDevis(Devis devis) {
		this.devis = devis;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
}
