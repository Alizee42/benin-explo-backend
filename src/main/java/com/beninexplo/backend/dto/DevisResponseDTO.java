package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DevisResponseDTO {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
    ---------------------------------------------------- */

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String message;
    private Long circuitId;

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */

    public DevisResponseDTO() {
    }

    public DevisResponseDTO(Long id, String nom, String prenom, String email,
                            String telephone, String message, Long circuitId) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.message = message;
        this.circuitId = circuitId;
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCircuitId() {
        return circuitId;
    }

    public void setCircuitId(Long circuitId) {
        this.circuitId = circuitId;
    }
}
