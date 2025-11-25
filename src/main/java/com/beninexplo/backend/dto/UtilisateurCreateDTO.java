package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UtilisateurCreateDTO {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
       Données envoyées pour créer un utilisateur USER
    ---------------------------------------------------- */

    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String motDePasse;

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */

    public UtilisateurCreateDTO() {}

    public UtilisateurCreateDTO(String nom, String prenom, String email,
                                String telephone, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}
