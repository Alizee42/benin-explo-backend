package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LoginResponseDTO {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
       Réponse envoyée après un login réussi
    ---------------------------------------------------- */

    private String token;          // JWT
    private Long id;               // ID utilisateur
    private String nom;            // Nom
    private String prenom;         // Prénom
    private String email;          // Email
    private String role;           // ADMIN / USER / PARTICIPANT

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, Long id, String nom, String prenom,
                            String email, String role) {

        this.token = token;
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

