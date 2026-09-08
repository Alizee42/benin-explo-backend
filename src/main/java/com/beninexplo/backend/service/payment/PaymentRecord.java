package com.beninexplo.backend.service.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Contrat commun aux entités PaiementReservationCircuit / PaiementReservationHebergement /
 * PaiementCircuitPersonnalise, qui partagent exactement les mêmes champs PayPal mais
 * n'avaient jusqu'ici aucune interface commune (chacune est un @OneToOne vers une entité
 * métier différente).
 */
public interface PaymentRecord {

    Long getId();

    String getStatut();
    void setStatut(String statut);

    BigDecimal getMontant();
    void setMontant(BigDecimal montant);

    String getDevise();
    void setDevise(String devise);

    void setProvider(String provider);

    String getPaypalOrderId();
    void setPaypalOrderId(String paypalOrderId);

    String getPaypalCaptureId();
    void setPaypalCaptureId(String paypalCaptureId);

    void setPaypalPayerId(String paypalPayerId);

    void setPaypalRequestId(String paypalRequestId);

    LocalDateTime getDatePaiement();
    void setDatePaiement(LocalDateTime datePaiement);

    void setOrderPayload(String orderPayload);

    void setCapturePayload(String capturePayload);
}
