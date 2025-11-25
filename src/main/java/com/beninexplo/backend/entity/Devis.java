package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "devis")  // ✅ TABLE CORRECTE
public class Devis {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
    ---------------------------------------------------- */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDevis;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    @Column(length = 20000)
    private String message;

    @ManyToOne
    @JoinColumn(name = "circuit_id")
    private Circuit circuit;

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */

    public Devis() {
        // Constructeur vide requis par JPA
    }

    public Devis(Long idDevis, String nom, String prenom, String email,
                 String telephone, String message, Circuit circuit) {
        this.idDevis = idDevis;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.message = message;
        this.circuit = circuit;
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */

    public Long getIdDevis() {
        return idDevis;
    }

    public void setIdDevis(Long idDevis) {
        this.idDevis = idDevis;
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
