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
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.CircuitPersonnaliseRepository;
import com.beninexplo.backend.repository.PaiementCircuitPersonnaliseRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class CircuitPersonnalisePaymentService {

    private static final Logger log = LoggerFactory.getLogger(CircuitPersonnalisePaymentService.class);

    private final PaiementCircuitPersonnaliseRepository paymentRepository;
    private final CircuitPersonnaliseRepository circuitPersonnaliseRepository;
    private final CircuitPersonnaliseService circuitPersonnaliseService;
    private final AuthenticatedUserService authenticatedUserService;
    private final PayPalApiClient payPalApiClient;
    private final PayPalProperties payPalProperties;

    public CircuitPersonnalisePaymentService(PaiementCircuitPersonnaliseRepository paymentRepository,
                                             CircuitPersonnaliseRepository circuitPersonnaliseRepository,
                                             CircuitPersonnaliseService circuitPersonnaliseService,
                                             AuthenticatedUserService authenticatedUserService,
                                             PayPalApiClient payPalApiClient,
                                             PayPalProperties payPalProperties) {
        this.paymentRepository = paymentRepository;
        this.circuitPersonnaliseRepository = circuitPersonnaliseRepository;
        this.circuitPersonnaliseService = circuitPersonnaliseService;
        this.authenticatedUserService = authenticatedUserService;
        this.payPalApiClient = payPalApiClient;
        this.payPalProperties = payPalProperties;
    }

    public HebergementPayPalConfigDTO getClientConfig() {
        HebergementPayPalConfigDTO dto = new HebergementPayPalConfigDTO();
        dto.setEnabled(payPalProperties.isReady());
        dto.setSandbox(payPalProperties.isSandbox());
        dto.setClientId(payPalProperties.isReady() ? payPalProperties.getClientId() : "");
        dto.setCurrency(payPalProperties.getCurrency());
        dto.setBrandName(payPalProperties.getBrandName());
        return dto;
    }

    public CircuitPersonnalisePayPalOrderResponseDTO createOrder(CreateCircuitPersonnalisePayPalOrderRequestDTO request) {
        CircuitPersonnalise demande = getOwnedDemande(request.getDemandeId());
        validateDemandeIsPayable(demande);
        BigDecimal amount = getPayableAmount(demande);

        PaiementCircuitPersonnalise payment = getOrCreatePayment(demande, amount);
        if ("PAYE".equals(normalizePaymentStatus(payment.getStatut()))) {
            throw new BadRequestException("Ce devis a deja ete regle.");
        }

        String requestId = UUID.randomUUID().toString();
        log.info("Starting PayPal create-order for custom circuit: demandeId={}, userId={}, paymentId={}, amount={}, currency={}, locale={}, reference={}, prixFinal={}, returnUrl={}, cancelUrl={}",
                demande.getId(),
                demande.getUtilisateur() != null ? demande.getUtilisateur().getId() : null,
                payment.getId(),
                amount,
                payPalProperties.getCurrency(),
                payPalProperties.getLocale(),
                demande.getReferenceReservation(),
                demande.getPrixFinal(),
                shortenUrl(request.getReturnUrl()),
                shortenUrl(request.getCancelUrl()));

        JsonNode response = payPalApiClient.createOrder(
                amount,
                buildDescription(demande),
                "circuit-personnalise-" + demande.getId(),
                "demande-circuit-personnalise-" + demande.getId(),
                request.getReturnUrl(),
                request.getCancelUrl(),
                requestId
        );

        String orderId = response.path("id").asText("");
        if (!StringUtils.hasText(orderId)) {
            throw new BadRequestException("PayPal n'a pas retourne de commande exploitable.");
        }

        payment.setProvider("PAYPAL");
        payment.setStatut(mapOrderStatusToPaymentStatus(response.path("status").asText("")));
        payment.setMontant(amount);
        payment.setDevise(payPalProperties.getCurrency());
        payment.setPaypalOrderId(orderId);
        payment.setPaypalRequestId(requestId);
        payment.setOrderPayload(response.toString());
        paymentRepository.save(payment);

        CircuitPersonnalisePayPalOrderResponseDTO dto = new CircuitPersonnalisePayPalOrderResponseDTO();
        dto.setDemandeId(demande.getId());
        dto.setOrderId(orderId);
        dto.setStatus(response.path("status").asText(""));
        dto.setStatutPaiement(payment.getStatut());
        dto.setMontant(payment.getMontant());
        dto.setDevise(payment.getDevise());
        return dto;
    }

    public CircuitPersonnalisePayPalCaptureResponseDTO captureOrder(CaptureCircuitPersonnalisePayPalOrderRequestDTO request) {
        CircuitPersonnalise demande = getOwnedDemande(request.getDemandeId());
        BigDecimal amount = getPayableAmount(demande);
        PaiementCircuitPersonnalise payment = getOrCreatePayment(demande, amount);

        if ("PAYE".equals(normalizePaymentStatus(payment.getStatut()))
                && request.getOrderId().equals(payment.getPaypalOrderId())) {
            return buildCaptureResponse(payment, circuitPersonnaliseService.getMineById(demande.getId()), "COMPLETED");
        }

        if (StringUtils.hasText(payment.getPaypalOrderId())
                && !request.getOrderId().equals(payment.getPaypalOrderId())) {
            throw new BadRequestException("La commande PayPal ne correspond pas a ce devis.");
        }

        String requestId = UUID.randomUUID().toString();
        JsonNode response = payPalApiClient.captureOrder(request.getOrderId(), requestId);
        JsonNode captureNode = response.path("purchase_units").path(0).path("payments").path("captures").path(0);

        String orderStatus = response.path("status").asText("");
        String captureStatus = captureNode.path("status").asText(orderStatus);
        String paymentStatus = mapCaptureStatusToPaymentStatus(orderStatus, captureStatus);

        payment.setProvider("PAYPAL");
        payment.setStatut(paymentStatus);
        payment.setMontant(amount);
        payment.setDevise(payPalProperties.getCurrency());
        payment.setPaypalOrderId(request.getOrderId());
        payment.setPaypalCaptureId(blankToNull(captureNode.path("id").asText("")));
        payment.setPaypalPayerId(blankToNull(response.path("payer").path("payer_id").asText("")));
        payment.setPaypalRequestId(requestId);
        payment.setCapturePayload(response.toString());
        if ("PAYE".equals(paymentStatus)) {
            payment.setDatePaiement(LocalDateTime.now());
        }
        paymentRepository.save(payment);

        return buildCaptureResponse(payment, circuitPersonnaliseService.getMineById(demande.getId()), orderStatus);
    }

    private CircuitPersonnalisePayPalCaptureResponseDTO buildCaptureResponse(PaiementCircuitPersonnalise payment,
                                                                             CircuitPersonnaliseDTO demande,
                                                                             String status) {
        CircuitPersonnalisePayPalCaptureResponseDTO dto = new CircuitPersonnalisePayPalCaptureResponseDTO();
        dto.setDemandeId(demande.getId());
        dto.setOrderId(payment.getPaypalOrderId());
        dto.setCaptureId(payment.getPaypalCaptureId());
        dto.setStatus(status);
        dto.setStatutPaiement(payment.getStatut());
        dto.setDemande(demande);
        return dto;
    }

    private PaiementCircuitPersonnalise getOrCreatePayment(CircuitPersonnalise demande, BigDecimal amount) {
        return paymentRepository.findByCircuitPersonnaliseId(demande.getId())
                .map(existing -> {
                    existing.setMontant(amount);
                    if (!StringUtils.hasText(existing.getDevise())) {
                        existing.setDevise(payPalProperties.getCurrency());
                    }
                    if (!StringUtils.hasText(existing.getStatut())) {
                        existing.setStatut("A_PAYER");
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    PaiementCircuitPersonnalise payment = new PaiementCircuitPersonnalise();
                    payment.setCircuitPersonnalise(demande);
                    payment.setProvider("PAYPAL");
                    payment.setStatut("A_PAYER");
                    payment.setMontant(amount);
                    payment.setDevise(payPalProperties.getCurrency());
                    return paymentRepository.save(payment);
                });
    }

    private CircuitPersonnalise getOwnedDemande(Long demandeId) {
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        CircuitPersonnalise demande = circuitPersonnaliseRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Devis personnalise introuvable pour ce compte."));

        boolean ownedByUserId = demande.getUtilisateur() != null
                && demande.getUtilisateur().getId() != null
                && demande.getUtilisateur().getId().equals(currentUser.getId());
        boolean ownedByEmail = StringUtils.hasText(demande.getEmailClient())
                && demande.getEmailClient().trim().equalsIgnoreCase(currentUser.getEmail());
        if (!ownedByUserId && !ownedByEmail) {
            throw new ResourceNotFoundException("Devis personnalise introuvable pour ce compte.");
        }
        return demande;
    }

    private void validateDemandeIsPayable(CircuitPersonnalise demande) {
        String status = demande.getStatut() != null ? demande.getStatut().name() : "EN_ATTENTE";
        if ("REFUSE".equalsIgnoreCase(status)) {
            throw new BadRequestException("Un devis refuse ne peut pas etre paye.");
        }
        if (!"ACCEPTE".equalsIgnoreCase(status)) {
            throw new BadRequestException("Le paiement sera disponible une fois le devis valide.");
        }
        if (getPayableAmount(demande).compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Ce devis ne contient pas de montant payable.");
        }
    }

    private BigDecimal getPayableAmount(CircuitPersonnalise demande) {
        if (demande.getPrixFinal() == null || demande.getPrixFinal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Le devis final n'est pas encore pret pour le paiement.");
        }
        return demande.getPrixFinal();
    }

    private String buildDescription(CircuitPersonnalise demande) {
        String reference = StringUtils.hasText(demande.getReferenceReservation())
                ? demande.getReferenceReservation()
                : "CPS-" + String.format("%06d", demande.getId());
        return "Paiement circuit personnalise " + reference
                + " - " + demande.getNombreJours() + " jour(s)";
    }

    private String mapOrderStatusToPaymentStatus(String orderStatus) {
        String normalized = orderStatus == null ? "" : orderStatus.trim().toUpperCase();
        if ("COMPLETED".equals(normalized)) {
            return "PAYE";
        }
        if ("PAYER_ACTION_REQUIRED".equals(normalized) || "APPROVED".equals(normalized) || "CREATED".equals(normalized)) {
            return "EN_COURS";
        }
        if ("VOIDED".equals(normalized)) {
            return "ECHEC";
        }
        return "EN_COURS";
    }

    private String mapCaptureStatusToPaymentStatus(String orderStatus, String captureStatus) {
        String normalizedCapture = captureStatus == null ? "" : captureStatus.trim().toUpperCase();
        if ("COMPLETED".equals(normalizedCapture) || "COMPLETED".equalsIgnoreCase(orderStatus)) {
            return "PAYE";
        }
        if ("PENDING".equals(normalizedCapture)) {
            return "EN_COURS";
        }
        if ("REFUNDED".equals(normalizedCapture) || "PARTIALLY_REFUNDED".equals(normalizedCapture)) {
            return "REMBOURSE";
        }
        return "ECHEC";
    }

    private String normalizePaymentStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return "A_PAYER";
        }
        return status.trim().toUpperCase();
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String shortenUrl(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        String normalized = value.trim();
        if (normalized.length() <= 220) {
            return normalized;
        }
        return normalized.substring(0, 220) + "...";
    }
}
