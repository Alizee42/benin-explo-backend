package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "client_demandes")

public class ClientDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDemande;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    @Column(length = 10000)
    private String message;

    // Circuit choisi (facultatif)
    @ManyToOne
    @JoinColumn(name = "circuit_id")
    private Circuit circuit;

    public ClientDemande() {}

    public ClientDemande(Long idDemande, String nom, String prenom, String email,
                         String telephone, String message, Circuit circuit) {
        this.idDemande = idDemande;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.message = message;
        this.circuit = circuit;
    }
 // -----------------------------------------
    // GETTERS / SETTERS
    // -----------------------------------------
    public Long getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(Long idDemande) {
        this.idDemande = idDemande;
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

    public Circuit getCircuit() {
        return circuit;
    }

    public void setCircuit(Circuit circuit) {
        this.circuit = circuit;
    }
}
