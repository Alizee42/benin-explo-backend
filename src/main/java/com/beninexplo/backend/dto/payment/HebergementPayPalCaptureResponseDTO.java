package com.beninexplo.backend.dto.payment;

import com.beninexplo.backend.dto.ReservationHebergementDTO;

public class HebergementPayPalCaptureResponseDTO {

    private Long reservationId;
    private String orderId;
    private String captureId;
    private String status;
    private String statutPaiement;
    private ReservationHebergementDTO reservation;

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCaptureId() {
        return captureId;
    }

    public void setCaptureId(String captureId) {
        this.captureId = captureId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatutPaiement() {
        return statutPaiement;
    }

    public void setStatutPaiement(String statutPaiement) {
        this.statutPaiement = statutPaiement;
    }

    public ReservationHebergementDTO getReservation() {
        return reservation;
    }

    public void setReservation(ReservationHebergementDTO reservation) {
        this.reservation = reservation;
    }
}
