package com.beninexplo.backend.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CreateCircuitPayPalOrderRequestDTO {

    @NotNull(message = "La reservation est obligatoire.")
    @Positive(message = "La reservation doit etre un identifiant positif.")
    private Long reservationId;

    @Size(max = 1000, message = "L'URL de retour est trop longue.")
    private String returnUrl;

    @Size(max = 1000, message = "L'URL d'annulation est trop longue.")
    private String cancelUrl;

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }
}
