package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
public class ParametresSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParametres;

    private String nomAgence;

    @Column(length = 5000)
    private String messageAccueil;

    private String emailContact;
    private String telephoneContact;
    private String adresseAgence;

    private String couleurPrimaire;     // ex : "#009900"
    private String couleurSecondaire;   // ex : "#FFCC00"

    private String urlFacebook;
    private String urlInstagram;

    public ParametresSite() {
    }

    public ParametresSite(Long idParametres, String nomAgence, String messageAccueil, String emailContact, String telephoneContact, String adresseAgence, String couleurPrimaire, String couleurSecondaire, String urlFacebook, String urlInstagram) {
        this.idParametres = idParametres;
        this.nomAgence = nomAgence;
        this.messageAccueil = messageAccueil;
        this.emailContact = emailContact;
        this.telephoneContact = telephoneContact;
        this.adresseAgence = adresseAgence;
        this.couleurPrimaire = couleurPrimaire;
        this.couleurSecondaire = couleurSecondaire;
        this.urlFacebook = urlFacebook;
        this.urlInstagram = urlInstagram;
    }

	public Long getIdParametres() {
		return idParametres;
	}

	public void setIdParametres(Long idParametres) {
		this.idParametres = idParametres;
	}

	public String getNomAgence() {
		return nomAgence;
	}

	public void setNomAgence(String nomAgence) {
		this.nomAgence = nomAgence;
	}

	public String getMessageAccueil() {
		return messageAccueil;
	}

	public void setMessageAccueil(String messageAccueil) {
		this.messageAccueil = messageAccueil;
	}

	public String getEmailContact() {
		return emailContact;
	}

	public void setEmailContact(String emailContact) {
		this.emailContact = emailContact;
	}

	public String getTelephoneContact() {
		return telephoneContact;
	}

	public void setTelephoneContact(String telephoneContact) {
		this.telephoneContact = telephoneContact;
	}

	public String getAdresseAgence() {
		return adresseAgence;
	}

	public void setAdresseAgence(String adresseAgence) {
		this.adresseAgence = adresseAgence;
	}

	public String getCouleurPrimaire() {
		return couleurPrimaire;
	}

	public void setCouleurPrimaire(String couleurPrimaire) {
		this.couleurPrimaire = couleurPrimaire;
	}

	public String getCouleurSecondaire() {
		return couleurSecondaire;
	}

	public void setCouleurSecondaire(String couleurSecondaire) {
		this.couleurSecondaire = couleurSecondaire;
	}

	public String getUrlFacebook() {
		return urlFacebook;
	}

	public void setUrlFacebook(String urlFacebook) {
		this.urlFacebook = urlFacebook;
	}

	public String getUrlInstagram() {
		return urlInstagram;
	}

	public void setUrlInstagram(String urlInstagram) {
		this.urlInstagram = urlInstagram;
	}
    
}
