package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "parametres_site")

public class ParametresSite {

    /* ---------------- ATTRIBUTS ---------------- */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParametres;

    private String emailContact;
    private String telephoneContact;
    private String adresseAgence;

    /* ---------------- CONSTRUCTEURS ---------------- */

    public ParametresSite() {}

    public ParametresSite(Long idParametres, String emailContact, String telephoneContact, String adresseAgence) {
        this.idParametres = idParametres;
        this.emailContact = emailContact;
        this.telephoneContact = telephoneContact;
        this.adresseAgence = adresseAgence;
    }

    /* ---------------- GETTERS & SETTERS ---------------- */

    public Long getIdParametres() { return idParametres; }
    public void setIdParametres(Long idParametres) { this.idParametres = idParametres; }

    public String getEmailContact() { return emailContact; }
    public void setEmailContact(String emailContact) { this.emailContact = emailContact; }

    public String getTelephoneContact() { return telephoneContact; }
    public void setTelephoneContact(String telephoneContact) { this.telephoneContact = telephoneContact; }

    public String getAdresseAgence() { return adresseAgence; }
    public void setAdresseAgence(String adresseAgence) { this.adresseAgence = adresseAgence; }
}
