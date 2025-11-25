package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")

public class Reservation {

    /* ---------------- ATTRIBUTS ---------------- */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    private LocalDate dateReservation;

    @ManyToOne
    @JoinColumn(name = "circuit_id")
    private Circuit circuit;

    /* ---------------- CONSTRUCTEURS ---------------- */

    public Reservation() {}

    public Reservation(Long idReservation, String nom, String prenom, String email,
                       String telephone, LocalDate dateReservation, Circuit circuit) {

        this.idReservation = idReservation;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.dateReservation = dateReservation;
        this.circuit = circuit;
    }

    /* ---------------- GETTERS & SETTERS ---------------- */

    public Long getIdReservation() { return idReservation; }
    public void setIdReservation(Long idReservation) { this.idReservation = idReservation; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public LocalDate getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDate dateReservation) { this.dateReservation = dateReservation; }

    public Circuit getCircuit() { return circuit; }
    public void setCircuit(Circuit circuit) { this.circuit = circuit; }
}
