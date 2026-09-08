package com.beninexplo.backend.service;

import com.beninexplo.backend.config.PayPalProperties;
import com.beninexplo.backend.dto.CircuitPersonnaliseDTO;
import com.beninexplo.backend.dto.payment.CaptureCircuitPersonnalisePayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.CircuitPersonnalisePayPalCaptureResponseDTO;
import com.beninexplo.backend.dto.payment.CircuitPersonnalisePayPalOrderResponseDTO;
import com.beninexplo.backend.dto.payment.CreateCircuitPersonnalisePayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.HebergementPayPalConfigDTO;
import com.beninexplo.backend.entity.CircuitPersonnalise;
import com.beninexplo.backend.entity.PaiementCircuitPersonnalise;
import com.beninexplo.backend.service.payment.CircuitPersonnalisePaymentAdapter;
import com.beninexplo.backend.service.payment.PayPalPaymentOrchestrator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Façade fine autour de {@link PayPalPaymentOrchestrator} : toute la mécanique PayPal
 * (create/capture order, mapping de statuts) vit dans l'orchestrateur générique, ce service
 * ne fait que traduire les DTO publics du contrôleur circuit personnalisé vers/depuis
 * l'orchestrateur.
 */
@Service
@Transactional
public class CircuitPersonnalisePaymentService {

    private final PayPalPaymentOrchestrator<CircuitPersonnalise, PaiementCircuitPersonnalise> orchestrator;

    public CircuitPersonnalisePaymentService(CircuitPersonnalisePaymentAdapter adapter,
                                             PayPalApiClient payPalApiClient,
                                             PayPalProperties payPalProperties) {
        this.orchestrator = new PayPalPaymentOrchestrator<>(adapter, payPalApiClient, payPalProperties);
    }

    public HebergementPayPalConfigDTO getClientConfig() {
        return orchestrator.getClientConfig();
    }

    public CircuitPersonnalisePayPalOrderResponseDTO createOrder(CreateCircuitPersonnalisePayPalOrderRequestDTO request) {
        PayPalPaymentOrchestrator.OrderResult result = orchestrator.createOrder(
                request.getDemandeId(), request.getReturnUrl(), request.getCancelUrl());

        CircuitPersonnalisePayPalOrderResponseDTO dto = new CircuitPersonnalisePayPalOrderResponseDTO();
        dto.setDemandeId(result.entityId);
        dto.setOrderId(result.orderId);
        dto.setStatus(result.paypalStatus);
        dto.setStatutPaiement(result.paymentStatus);
        dto.setMontant(result.montant);
        dto.setDevise(result.devise);
        return dto;
    }

    public CircuitPersonnalisePayPalCaptureResponseDTO captureOrder(CaptureCircuitPersonnalisePayPalOrderRequestDTO request) {
        PayPalPaymentOrchestrator.CaptureResult result =
                orchestrator.captureOrder(request.getDemandeId(), request.getOrderId());

        CircuitPersonnalisePayPalCaptureResponseDTO dto = new CircuitPersonnalisePayPalCaptureResponseDTO();
        dto.setDemandeId(request.getDemandeId());
        dto.setOrderId(result.orderId);
        dto.setCaptureId(result.captureId);
        dto.setStatus(result.status);
        dto.setStatutPaiement(result.paymentStatus);
        dto.setDemande((CircuitPersonnaliseDTO) result.entityView);
        return dto;
    }
}
