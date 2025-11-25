package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LoginRequestDTO {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
       Données envoyées lors du login
    ---------------------------------------------------- */

    private String email;
    private String motDePasse;

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */

    public LoginRequestDTO() {}

    public LoginRequestDTO(String email, String motDePasse) {
        this.email = email;
        this.motDePasse = motDePasse;
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}
