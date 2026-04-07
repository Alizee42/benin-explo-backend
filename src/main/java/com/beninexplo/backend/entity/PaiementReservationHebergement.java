package com.beninexplo.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiements_reservation_hebergement")
public class PaiementReservationHebergement extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_hebergement_id", nullable = false, unique = true)
    private ReservationHebergement reservationHebergement;

    @Column(nullable = false, length = 30)
    private String provider = "PAYPAL";

    @Column(nullable = false, length = 30)
    private String statut = "A_PAYER";

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false, length = 3)
    private String devise = "EUR";

    @Column(length = 120)
    private String paypalOrderId;

    @Column(length = 120)
    private String paypalCaptureId;

    @Column(length = 120)
    private String paypalPayerId;

    @Column(length = 120)
    private String paypalRequestId;

    private LocalDateTime datePaiement;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String orderPayload;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String capturePayload;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReservationHebergement getReservationHebergement() {
        return reservationHebergement;
    }

    public void setReservationHebergement(ReservationHebergement reservationHebergement) {
        this.reservationHebergement = reservationHebergement;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public String getPaypalOrderId() {
        return paypalOrderId;
    }

    public void setPaypalOrderId(String paypalOrderId) {
        this.paypalOrderId = paypalOrderId;
    }

    public String getPaypalCaptureId() {
        return paypalCaptureId;
    }

    public void setPaypalCaptureId(String paypalCaptureId) {
        this.paypalCaptureId = paypalCaptureId;
    }

    public String getPaypalPayerId() {
        return paypalPayerId;
    }

    public void setPaypalPayerId(String paypalPayerId) {
        this.paypalPayerId = paypalPayerId;
    }

    public String getPaypalRequestId() {
        return paypalRequestId;
    }

    public void setPaypalRequestId(String paypalRequestId) {
        this.paypalRequestId = paypalRequestId;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getOrderPayload() {
        return orderPayload;
    }

    public void setOrderPayload(String orderPayload) {
        this.orderPayload = orderPayload;
    }

    public String getCapturePayload() {
        return capturePayload;
    }

    public void setCapturePayload(String capturePayload) {
        this.capturePayload = capturePayload;
    }
}
