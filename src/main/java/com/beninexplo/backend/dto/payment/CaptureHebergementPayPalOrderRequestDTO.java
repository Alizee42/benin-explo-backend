package com.beninexplo.backend.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CaptureHebergementPayPalOrderRequestDTO {

    @NotNull(message = "La reservation est obligatoire.")
    @Positive(message = "La reservation doit etre un identifiant positif.")
    private Long reservationId;

    @NotBlank(message = "L'identifiant de commande PayPal est obligatoire.")
    private String orderId;

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
}
