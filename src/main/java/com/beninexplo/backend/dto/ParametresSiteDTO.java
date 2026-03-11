package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ParametresSiteDTO {

    /* ---------------- ATTRIBUTS ---------------- */

    private Long id;

    @Email(message = "L'email de contact doit etre valide.")
    @Size(max = 150, message = "L'email de contact ne doit pas depasser 150 caracteres.")
    private String emailContact;

    @Pattern(regexp = "^$|^[0-9+()\\-\\s]{8,20}$", message = "Le telephone de contact doit contenir entre 8 et 20 caracteres valides.")
    private String telephoneContact;

    @Size(max = 255, message = "L'adresse de l'agence ne doit pas depasser 255 caracteres.")
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
