package com.beninexplo.backend.dto.payment;

import com.beninexplo.backend.dto.ReservationResponseDTO;

public class CircuitPayPalCaptureResponseDTO {

    private Long reservationId;
    private String orderId;
    private String captureId;
    private String status;
    private String statutPaiement;
    private ReservationResponseDTO reservation;

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

    public ReservationResponseDTO getReservation() {
        return reservation;
    }

    public void setReservation(ReservationResponseDTO reservation) {
        this.reservation = reservation;
    }
}
