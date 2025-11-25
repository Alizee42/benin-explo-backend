package com.beninexplo.backend.dto;

public class ParametresSiteDTO {

    private Long id;

    private String nomAgence;
    private String messageAccueil;

    private String emailContact;
    private String telephoneContact;
    private String adresseAgence;

    private String couleurPrimaire;
    private String couleurSecondaire;

    private String urlFacebook;
    private String urlInstagram;

    public ParametresSiteDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
