package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ReservationHebergementDTO {

    private Long id;

    @NotNull(message = "L'hebergement est obligatoire.")
    @Positive(message = "L'hebergement doit etre un identifiant positif.")
    private Long hebergementId;

    private String hebergementNom;
    private String referenceReservation;

    @NotBlank(message = "Le nom du client est obligatoire.")
    @Size(max = 100, message = "Le nom du client ne doit pas depasser 100 caracteres.")
    private String nomClient;

    @NotBlank(message = "Le prenom du client est obligatoire.")
    @Size(max = 100, message = "Le prenom du client ne doit pas depasser 100 caracteres.")
    private String prenomClient;

    @NotBlank(message = "L'email du client est obligatoire.")
    @Email(message = "L'email du client doit etre valide.")
    @Size(max = 150, message = "L'email du client ne doit pas depasser 150 caracteres.")
    private String emailClient;

    @NotBlank(message = "Le telephone du client est obligatoire.")
    @Pattern(regexp = "^[0-9+()\\-\\s]{8,20}$", message = "Le telephone du client doit contenir entre 8 et 20 caracteres valides.")
    private String telephoneClient;

    @NotNull(message = "La date d'arrivee est obligatoire.")
    private LocalDate dateArrivee;

    @NotNull(message = "La date de depart est obligatoire.")
    private LocalDate dateDepart;

    private int nombreNuits;

    @Positive(message = "Le nombre de personnes doit etre superieur a zero.")
    private int nombrePersonnes;

    private BigDecimal prixTotal;
    private String statut;
    private Long utilisateurId;
    private String statutPaiement;
    private BigDecimal montantPaye;
    private String devisePaiement;
    private String paypalOrderId;
    private String paypalCaptureId;
    private LocalDateTime datePaiement;

    @Size(max = 1000, message = "Les commentaires ne doivent pas depasser 1000 caracteres.")
    private String commentaires;

    private LocalDate dateCreation;

    public ReservationHebergementDTO() {
    }

    public ReservationHebergementDTO(Long id, Long hebergementId, String hebergementNom,
                                     String nomClient, String prenomClient, String emailClient,
                                     String telephoneClient, LocalDate dateArrivee, LocalDate dateDepart,
                                     int nombreNuits, int nombrePersonnes, BigDecimal prixTotal,
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

    @AssertTrue(message = "La date de depart doit etre strictement apres la date d'arrivee.")
    public boolean isStayPeriodValid() {
        return dateArrivee == null || dateDepart == null || dateDepart.isAfter(dateArrivee);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHebergementId() { return hebergementId; }
    public void setHebergementId(Long hebergementId) { this.hebergementId = hebergementId; }

    public String getHebergementNom() { return hebergementNom; }
    public void setHebergementNom(String hebergementNom) { this.hebergementNom = hebergementNom; }

    public String getReferenceReservation() { return referenceReservation; }
    public void setReferenceReservation(String referenceReservation) { this.referenceReservation = referenceReservation; }

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

    public BigDecimal getPrixTotal() { return prixTotal; }
    public void setPrixTotal(BigDecimal prixTotal) { this.prixTotal = prixTotal; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public String getStatutPaiement() { return statutPaiement; }
    public void setStatutPaiement(String statutPaiement) { this.statutPaiement = statutPaiement; }

    public BigDecimal getMontantPaye() { return montantPaye; }
    public void setMontantPaye(BigDecimal montantPaye) { this.montantPaye = montantPaye; }

    public String getDevisePaiement() { return devisePaiement; }
    public void setDevisePaiement(String devisePaiement) { this.devisePaiement = devisePaiement; }

    public String getPaypalOrderId() { return paypalOrderId; }
    public void setPaypalOrderId(String paypalOrderId) { this.paypalOrderId = paypalOrderId; }

    public String getPaypalCaptureId() { return paypalCaptureId; }
    public void setPaypalCaptureId(String paypalCaptureId) { this.paypalCaptureId = paypalCaptureId; }

    public LocalDateTime getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }

    public String getCommentaires() { return commentaires; }
    public void setCommentaires(String commentaires) { this.commentaires = commentaires; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
}
