package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.time.LocalDate;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ReservationHebergementDTO {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
    ---------------------------------------------------- */
    private Long id;
    private Long hebergementId;
    private String hebergementNom;
    private String nomClient;
    private String prenomClient;
    private String emailClient;
    private String telephoneClient;
    private LocalDate dateArrivee;
    private LocalDate dateDepart;
    private int nombreNuits;
    private int nombrePersonnes;
    private double prixTotal;
    private String statut;
    private String commentaires;
    private LocalDate dateCreation;

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */
    public ReservationHebergementDTO() {}

    public ReservationHebergementDTO(Long id, Long hebergementId, String hebergementNom,
                                   String nomClient, String prenomClient, String emailClient,
                                   String telephoneClient, LocalDate dateArrivee, LocalDate dateDepart,
                                   int nombreNuits, int nombrePersonnes, double prixTotal,
                                   String statut, String commentaires, LocalDate dateCreation) {
        this.id = id;
        this.hebergementId = hebergementId;
        this.hebergementNom = hebergementNom;
        this.nomClient = nomClient;
        this.prenomClient = prenomClient;
        this.emailClient = emailClient;
        this.telephoneClient = telephoneClient;
        this.dateArrivee = dateArrivee;
        this.dateDepart = dateDepart;
        this.nombreNuits = nombreNuits;
        this.nombrePersonnes = nombrePersonnes;
        this.prixTotal = prixTotal;
        this.statut = statut;
        this.commentaires = commentaires;
        this.dateCreation = dateCreation;
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHebergementId() { return hebergementId; }
    public void setHebergementId(Long hebergementId) { this.hebergementId = hebergementId; }

    public String getHebergementNom() { return hebergementNom; }
    public void setHebergementNom(String hebergementNom) { this.hebergementNom = hebergementNom; }

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