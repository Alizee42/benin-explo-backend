package com.beninexplo.backend.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CaptureCircuitPersonnalisePayPalOrderRequestDTO {

    @NotNull(message = "La demande est obligatoire.")
    @Positive(message = "La demande doit etre un identifiant positif.")
    private Long demandeId;

    @NotBlank(message = "La commande PayPal est obligatoire.")
    @Size(max = 120, message = "L'identifiant de commande PayPal est invalide.")
    private String orderId;

    public Long getDemandeId() {
        return demandeId;
    }

    public void setDemandeId(Long demandeId) {
        this.demandeId = demandeId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
