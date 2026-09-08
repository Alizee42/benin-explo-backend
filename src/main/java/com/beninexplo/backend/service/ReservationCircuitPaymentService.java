package com.beninexplo.backend.service;

import com.beninexplo.backend.config.PayPalProperties;
import com.beninexplo.backend.dto.ReservationResponseDTO;
import com.beninexplo.backend.dto.payment.CaptureCircuitPayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.CircuitPayPalCaptureResponseDTO;
import com.beninexplo.backend.dto.payment.CircuitPayPalOrderResponseDTO;
import com.beninexplo.backend.dto.payment.CreateCircuitPayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.HebergementPayPalConfigDTO;
import com.beninexplo.backend.entity.PaiementReservationCircuit;
import com.beninexplo.backend.entity.Reservation;
import com.beninexplo.backend.service.payment.PayPalPaymentOrchestrator;
import com.beninexplo.backend.service.payment.ReservationCircuitPaymentAdapter;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Façade fine autour de {@link PayPalPaymentOrchestrator} : toute la mécanique PayPal
 * (create/capture order, mapping de statuts) vit dans l'orchestrateur générique, ce service
 * ne fait que traduire les DTO publics du contrôleur circuit vers/depuis l'orchestrateur.
 */
@Service
@Transactional
public class ReservationCircuitPaymentService {

    private final PayPalPaymentOrchestrator<Reservation, PaiementReservationCircuit> orchestrator;

    public ReservationCircuitPaymentService(ReservationCircuitPaymentAdapter adapter,
                                            PayPalApiClient payPalApiClient,
                                            PayPalProperties payPalProperties) {
        this.orchestrator = new PayPalPaymentOrchestrator<>(adapter, payPalApiClient, payPalProperties);
    }

    public HebergementPayPalConfigDTO getClientConfig() {
        return orchestrator.getClientConfig();
    }

    public CircuitPayPalOrderResponseDTO createOrder(CreateCircuitPayPalOrderRequestDTO request) {
        PayPalPaymentOrchestrator.OrderResult result = orchestrator.createOrder(
                request.getReservationId(), request.getReturnUrl(), request.getCancelUrl());

        CircuitPayPalOrderResponseDTO dto = new CircuitPayPalOrderResponseDTO();
        dto.setReservationId(result.entityId);
        dto.setOrderId(result.orderId);
        dto.setStatus(result.paypalStatus);
        dto.setStatutPaiement(result.paymentStatus);
        dto.setMontant(result.montant);
        dto.setDevise(result.devise);
        return dto;
    }

    public CircuitPayPalCaptureResponseDTO captureOrder(CaptureCircuitPayPalOrderRequestDTO request) {
        PayPalPaymentOrchestrator.CaptureResult result =
                orchestrator.captureOrder(request.getReservationId(), request.getOrderId());

        CircuitPayPalCaptureResponseDTO dto = new CircuitPayPalCaptureResponseDTO();
        dto.setReservationId(request.getReservationId());
        dto.setOrderId(result.orderId);
        dto.setCaptureId(result.captureId);
        dto.setStatus(result.status);
        dto.setStatutPaiement(result.paymentStatus);
        dto.setReservation((ReservationResponseDTO) result.entityView);
        return dto;
    }
}
