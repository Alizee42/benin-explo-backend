package com.beninexplo.backend.service;

import com.beninexplo.backend.config.PayPalProperties;
import com.beninexplo.backend.dto.ReservationHebergementDTO;
import com.beninexplo.backend.dto.payment.CaptureHebergementPayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.CreateHebergementPayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.HebergementPayPalCaptureResponseDTO;
import com.beninexplo.backend.dto.payment.HebergementPayPalConfigDTO;
import com.beninexplo.backend.dto.payment.HebergementPayPalOrderResponseDTO;
import com.beninexplo.backend.entity.PaiementReservationHebergement;
import com.beninexplo.backend.entity.ReservationHebergement;
import com.beninexplo.backend.service.payment.PayPalPaymentOrchestrator;
import com.beninexplo.backend.service.payment.ReservationHebergementPaymentAdapter;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Façade fine autour de {@link PayPalPaymentOrchestrator} : toute la mécanique PayPal
 * (create/capture order, mapping de statuts) vit dans l'orchestrateur générique, ce service
 * ne fait que traduire les DTO publics du contrôleur hébergement vers/depuis l'orchestrateur.
 */
@Service
@Transactional
public class ReservationHebergementPaymentService {

    private final PayPalPaymentOrchestrator<ReservationHebergement, PaiementReservationHebergement> orchestrator;

    public ReservationHebergementPaymentService(ReservationHebergementPaymentAdapter adapter,
                                                PayPalApiClient payPalApiClient,
                                                PayPalProperties payPalProperties) {
        this.orchestrator = new PayPalPaymentOrchestrator<>(adapter, payPalApiClient, payPalProperties);
    }

    public HebergementPayPalConfigDTO getClientConfig() {
        return orchestrator.getClientConfig();
    }

    public HebergementPayPalOrderResponseDTO createOrder(CreateHebergementPayPalOrderRequestDTO request) {
        PayPalPaymentOrchestrator.OrderResult result = orchestrator.createOrder(
                request.getReservationId(), request.getReturnUrl(), request.getCancelUrl());

        HebergementPayPalOrderResponseDTO dto = new HebergementPayPalOrderResponseDTO();
        dto.setReservationId(result.entityId);
        dto.setOrderId(result.orderId);
        dto.setStatus(result.paypalStatus);
        dto.setStatutPaiement(result.paymentStatus);
        dto.setMontant(result.montant);
        dto.setDevise(result.devise);
        return dto;
    }

    public HebergementPayPalCaptureResponseDTO captureOrder(CaptureHebergementPayPalOrderRequestDTO request) {
        PayPalPaymentOrchestrator.CaptureResult result =
                orchestrator.captureOrder(request.getReservationId(), request.getOrderId());

        HebergementPayPalCaptureResponseDTO dto = new HebergementPayPalCaptureResponseDTO();
        dto.setReservationId(request.getReservationId());
        dto.setOrderId(result.orderId);
        dto.setCaptureId(result.captureId);
        dto.setStatus(result.status);
        dto.setStatutPaiement(result.paymentStatus);
        dto.setReservation((ReservationHebergementDTO) result.entityView);
        return dto;
    }
}
