package com.beninexplo.backend.dto.payment;

import com.beninexplo.backend.dto.CircuitPersonnaliseDTO;

public class CircuitPersonnalisePayPalCaptureResponseDTO {

    private Long demandeId;
    private String orderId;
    private String captureId;
    private String status;
    private String statutPaiement;
    private CircuitPersonnaliseDTO demande;

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

    public CircuitPersonnaliseDTO getDemande() {
        return demande;
    }

    public void setDemande(CircuitPersonnaliseDTO demande) {
        this.demande = demande;
    }
}
