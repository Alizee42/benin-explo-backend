package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservations_hebergement")
public class ReservationHebergement {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
    ---------------------------------------------------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    @ManyToOne
    @JoinColumn(name = "id_hebergement")
    private Hebergement hebergement;

    private String nomClient;
    private String prenomClient;
    private String emailClient;
    private String telephoneClient;

    private LocalDate dateArrivee;
    private LocalDate dateDepart;
    private int nombreNuits;
    private int nombrePersonnes;

    private double prixTotal;
    private String statut; // CONFIRME, ANNULE, EN_ATTENTE

    @Column(length = 1000)
    private String commentaires;

    private LocalDate dateCreation;

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */
    public ReservationHebergement() {}

    public ReservationHebergement(Hebergement hebergement, String nomClient, String prenomClient,
                                String emailClient, String telephoneClient, LocalDate dateArrivee,
                                LocalDate dateDepart, int nombrePersonnes, String commentaires) {
        this.hebergement = hebergement;
        this.nomClient = nomClient;
        this.prenomClient = prenomClient;
        this.emailClient = emailClient;
        this.telephoneClient = telephoneClient;
        this.dateArrivee = dateArrivee;
        this.dateDepart = dateDepart;
        this.nombrePersonnes = nombrePersonnes;
        this.commentaires = commentaires;
        this.dateCreation = LocalDate.now();
        this.statut = "EN_ATTENTE";

        // Calcul automatique du nombre de nuits et du prix total
        this.nombreNuits = (int) java.time.temporal.ChronoUnit.DAYS.between(dateArrivee, dateDepart);
        this.prixTotal = this.nombreNuits * hebergement.getPrixParNuit();
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */
    public Long getIdReservation() { return idReservation; }
    public void setIdReservation(Long idReservation) { this.idReservation = idReservation; }

    public Hebergement getHebergement() { return hebergement; }
    public void setHebergement(Hebergement hebergement) { this.hebergement = hebergement; }

    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }

    public String getPrenomClient() { return prenomClient; }
    public void setPrenomClient(String prenomClient) { this.prenomClient = prenomClient; }

    public String getEmailClient() { return emailClient; }
    public void setEmailClient(String emailClient) { this.emailClient = emailClient; }

    public String getTelephoneClient() { return telephoneClient; }
    public void setTelephoneClient(String telephoneClient) { this.telephoneClient = telephoneClient; }

    public LocalDate getDateArrivee() { return dateArrivee; }
    public void setDateArrivee(LocalDate dateArrivee) { this.dateArrivee = dateArrivee; }

    public LocalDate getDateDepart() { return dateDepart; }
    public void setDateDepart(LocalDate dateDepart) { this.dateDepart = dateDepart; }

    public int getNombreNuits() { return nombreNuits; }
    public void setNombreNuits(int nombreNuits) { this.nombreNuits = nombreNuits; }

    public int getNombrePersonnes() { return nombrePersonnes; }
    public void setNombrePersonnes(int nombrePersonnes) { this.nombrePersonnes = nombrePersonnes; }

    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getCommentaires() { return commentaires; }
    public void setCommentaires(String commentaires) { this.commentaires = commentaires; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
}