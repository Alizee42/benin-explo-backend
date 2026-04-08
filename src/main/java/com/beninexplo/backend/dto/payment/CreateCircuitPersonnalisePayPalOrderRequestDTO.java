package com.beninexplo.backend.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CreateCircuitPersonnalisePayPalOrderRequestDTO {

    @NotNull(message = "La demande est obligatoire.")
    @Positive(message = "La demande doit etre un identifiant positif.")
    private Long demandeId;

    @Size(max = 1000, message = "L'URL de retour est trop longue.")
    private String returnUrl;

    @Size(max = 1000, message = "L'URL d'annulation est trop longue.")
    private String cancelUrl;

    public Long getDemandeId() {
        return demandeId;
    }

    public void setDemandeId(Long demandeId) {
        this.demandeId = demandeId;
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
