package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity

@Table(name = "tombola_participants")
public class TombolaParticipant {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
    ---------------------------------------------------- */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lien direct avec l'utilisateur du site
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // Informations stockées pour la tombola (sécurité / historique)
    @Column(nullable = false)
    private String email;

    private String nom;
    private String prenom;

    private LocalDateTime dateInscription;

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */

    public TombolaParticipant() {}

    public TombolaParticipant(Long id, Utilisateur utilisateur, String email,
                              String nom, String prenom, LocalDateTime dateInscription) {
        this.id = id;
        this.utilisateur = utilisateur;
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

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }
}
