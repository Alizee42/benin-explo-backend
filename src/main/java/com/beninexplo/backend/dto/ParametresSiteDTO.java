package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ParametresSiteDTO {

    /* ---------------- ATTRIBUTS ---------------- */

    private Long id;
    private String emailContact;
    private String telephoneContact;
    private String adresseAgence;

    /* ---------------- CONSTRUCTEURS ---------------- */

    public ParametresSiteDTO() {}

    public ParametresSiteDTO(Long id, String emailContact, String telephoneContact, String adresseAgence) {
        this.id = id;
        this.emailContact = emailContact;
        this.telephoneContact = telephoneContact;
        this.adresseAgence = adresseAgence;
    }

    /* ---------------- GETTERS & SETTERS ---------------- */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmailContact() { return emailContact; }
    public void setEmailContact(String emailContact) { this.emailContact = emailContact; }

    public String getTelephoneContact() { return telephoneContact; }
    public void setTelephoneContact(String telephoneContact) { this.telephoneContact = telephoneContact; }

    public String getAdresseAgence() { return adresseAgence; }
    public void setAdresseAgence(String adresseAgence) { this.adresseAgence = adresseAgence; }
}
