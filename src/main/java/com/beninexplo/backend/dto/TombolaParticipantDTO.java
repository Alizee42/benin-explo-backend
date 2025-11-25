package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TombolaParticipantDTO {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
       Ce que l'on renvoie côté client pour un participant.
    ---------------------------------------------------- */

    private Long id;
    private Long utilisateurId;
    private String email;
    private String nom;
    private String prenom;
    private String dateInscription;

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */

    public TombolaParticipantDTO() {}

    public TombolaParticipantDTO(Long id, Long utilisateurId, String email,
                                 String nom, String prenom, String dateInscription) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.dateInscription = dateInscription;
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getDateInscription() { return dateInscription; }
    public void setDateInscription(String dateInscription) { this.dateInscription = dateInscription; }
}
