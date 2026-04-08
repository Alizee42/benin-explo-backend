package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ReservationResponseDTO {

    /* ---------------- ATTRIBUTS ---------------- */

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private LocalDate dateReservation;
    private Long circuitId;
    private String circuitNom;
    private String statut;
    private Integer nombrePersonnes;
    private Long utilisateurId;
    private String commentaires;
    private String referenceReservation;
    private BigDecimal prixTotal;
    private String statutPaiement;
    private BigDecimal montantPaye;
    private String devisePaiement;
    private String paypalOrderId;
    private String paypalCaptureId;
    private LocalDateTime datePaiement;

    /* ---------------- CONSTRUCTEURS ---------------- */

    public ReservationResponseDTO() {}

    public ReservationResponseDTO(Long id, String nom, String prenom, String email,
                                  String telephone, LocalDate dateReservation, Long circuitId) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.dateReservation = dateReservation;
        this.circuitId = circuitId;
    }

    /* ---------------- GETTERS & SETTERS ---------------- */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Long getCircuitId() { return circuitId; }
    public void setCircuitId(Long circuitId) { this.circuitId = circuitId; }

    public String getCircuitNom() { return circuitNom; }
    public void setCircuitNom(String circuitNom) { this.circuitNom = circuitNom; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Integer getNombrePersonnes() { return nombrePersonnes; }
    public void setNombrePersonnes(Integer nombrePersonnes) { this.nombrePersonnes = nombrePersonnes; }

    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public String getCommentaires() { return commentaires; }
    public void setCommentaires(String commentaires) { this.commentaires = commentaires; }

    public String getReferenceReservation() { return referenceReservation; }
    public void setReferenceReservation(String referenceReservation) { this.referenceReservation = referenceReservation; }

    public BigDecimal getPrixTotal() { return prixTotal; }
    public void setPrixTotal(BigDecimal prixTotal) { this.prixTotal = prixTotal; }

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
}
