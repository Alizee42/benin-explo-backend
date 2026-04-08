package com.beninexplo.backend.service;

import com.beninexplo.backend.config.PayPalProperties;
import com.beninexplo.backend.entity.PaiementReservationHebergement;
import com.beninexplo.backend.repository.PaiementReservationHebergementRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class PayPalWebhookService {

    private static final Logger log = LoggerFactory.getLogger(PayPalWebhookService.class);

    private final PaiementReservationHebergementRepository paymentRepository;
    private final PayPalApiClient payPalApiClient;
    private final PayPalProperties payPalProperties;
    private final ObjectMapper objectMapper;

    public PayPalWebhookService(PaiementReservationHebergementRepository paymentRepository,
                                PayPalApiClient payPalApiClient,
                                PayPalProperties payPalProperties,
                                ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.payPalApiClient = payPalApiClient;
        this.payPalProperties = payPalProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Vérifie la signature du webhook PayPal si webhookId est configuré.
     * Retourne false si la signature est invalide.
     */
    public boolean verifySignature(String authAlgo, String certUrl, String transmissionId,
                                   String transmissionSig, String transmissionTime, String rawBody) {
        String webhookId = payPalProperties.getWebhookId();
        if (!StringUtils.hasText(webhookId)) {
            log.warn("PayPal webhookId non configure — verification de signature ignoree (sandbox seulement)");
            return true;
        }

        if (!StringUtils.hasText(transmissionId) || !StringUtils.hasText(transmissionSig)) {
            log.warn("Webhook PayPal recu sans headers de signature valides");
            return false;
        }

        return payPalApiClient.verifyWebhookSignature(
                authAlgo, certUrl, transmissionId, transmissionSig, transmissionTime, webhookId, rawBody);
    }

    /**
     * Traite un evenement webhook PayPal.
     */
    public void processEvent(String rawBody) {
        JsonNode event;
        try {
            event = objectMapper.readTree(rawBody);
        } catch (Exception ex) {
            log.error("Impossible de parser le body du webhook PayPal: {}", rawBody, ex);
            return;
        }

        String eventType = event.path("event_type").asText("");
        String eventId = event.path("id").asText("unknown");
        JsonNode resource = event.path("resource");

        log.info("PayPal webhook recu: eventId={}, eventType={}", eventId, eventType);

        switch (eventType.toUpperCase()) {
            case "PAYMENT.CAPTURE.COMPLETED" -> handleCaptureCompleted(eventId, resource);
            case "PAYMENT.CAPTURE.REFUNDED"  -> handleCaptureRefunded(eventId, resource);
            case "PAYMENT.CAPTURE.DENIED"    -> handleCaptureDenied(eventId, resource);
            case "PAYMENT.CAPTURE.PENDING"   -> handleCapturePending(eventId, resource);
            case "PAYMENT.CAPTURE.REVERSED"  -> handleCaptureReversed(eventId, resource);
            default -> log.debug("Webhook PayPal ignore (type non gere): eventId={}, eventType={}", eventId, eventType);
        }
    }

    private void handleCaptureCompleted(String eventId, JsonNode resource) {
        Optional<PaiementReservationHebergement> payment = findPaymentFromCapture(resource);
        payment.ifPresentOrElse(
                p -> updatePaymentStatus(p, "PAYE", eventId, resource),
                () -> log.warn("Webhook PAYMENT.CAPTURE.COMPLETED: aucun paiement trouve, eventId={}, captureId={}", eventId, resource.path("id").asText(""))
        );
    }

    private void handleCaptureRefunded(String eventId, JsonNode resource) {
        Optional<PaiementReservationHebergement> payment = findPaymentFromCapture(resource);
        payment.ifPresentOrElse(
                p -> updatePaymentStatus(p, "REMBOURSE", eventId, resource),
                () -> log.warn("Webhook PAYMENT.CAPTURE.REFUNDED: aucun paiement trouve, eventId={}, captureId={}", eventId, resource.path("id").asText(""))
        );
    }

    private void handleCaptureDenied(String eventId, JsonNode resource) {
        Optional<PaiementReservationHebergement> payment = findPaymentFromCapture(resource);
        payment.ifPresentOrElse(
                p -> updatePaymentStatus(p, "ECHEC", eventId, resource),
                () -> log.warn("Webhook PAYMENT.CAPTURE.DENIED: aucun paiement trouve, eventId={}, captureId={}", eventId, resource.path("id").asText(""))
        );
    }

    private void handleCapturePending(String eventId, JsonNode resource) {
        Optional<PaiementReservationHebergement> payment = findPaymentFromCapture(resource);
        payment.ifPresentOrElse(
                p -> updatePaymentStatus(p, "EN_COURS", eventId, resource),
                () -> log.warn("Webhook PAYMENT.CAPTURE.PENDING: aucun paiement trouve, eventId={}, captureId={}", eventId, resource.path("id").asText(""))
        );
    }

    private void handleCaptureReversed(String eventId, JsonNode resource) {
        Optional<PaiementReservationHebergement> payment = findPaymentFromCapture(resource);
        payment.ifPresentOrElse(
                p -> updatePaymentStatus(p, "REMBOURSE", eventId, resource),
                () -> log.warn("Webhook PAYMENT.CAPTURE.REVERSED: aucun paiement trouve, eventId={}, captureId={}", eventId, resource.path("id").asText(""))
        );
    }

    /**
     * Cherche le paiement correspondant à un evenement capture PayPal.
     * Tente d'abord par captureId, puis par orderId si disponible.
     */
    private Optional<PaiementReservationHebergement> findPaymentFromCapture(JsonNode resource) {
        String captureId = resource.path("id").asText("");
        if (StringUtils.hasText(captureId)) {
            Optional<PaiementReservationHebergement> byCapture = paymentRepository.findByPaypalCaptureId(captureId);
            if (byCapture.isPresent()) {
                return byCapture;
            }
        }

        // Fallback: chercher par orderId si disponible dans supplementary_data
        String orderId = resource.path("supplementary_data").path("related_ids").path("order_id").asText("");
        if (StringUtils.hasText(orderId)) {
            Optional<PaiementReservationHebergement> byOrder = paymentRepository.findByPaypalOrderId(orderId);
            if (byOrder.isPresent()) {
                return byOrder;
            }
        }

        return Optional.empty();
    }

    private void updatePaymentStatus(PaiementReservationHebergement payment, String newStatus,
                                     String eventId, JsonNode resource) {
        String previousStatus = payment.getStatut();
        String captureId = resource.path("id").asText("");

        // Ne pas rétrograder un paiement déjà remboursé ou payé vers un statut inférieur
        if ("REMBOURSE".equals(previousStatus) && "PAYE".equals(newStatus)) {
            log.warn("Webhook PayPal ignoré: tentative de repasser REMBOURSE -> PAYE. eventId={}, paymentId={}", eventId, payment.getId());
            return;
        }

        payment.setStatut(newStatus);
        if (StringUtils.hasText(captureId) && !captureId.equals(payment.getPaypalCaptureId())) {
            payment.setPaypalCaptureId(captureId);
        }
        if ("PAYE".equals(newStatus) && payment.getDatePaiement() == null) {
            payment.setDatePaiement(LocalDateTime.now());
        }

        paymentRepository.save(payment);

        log.info("PayPal webhook: statut paiement mis a jour. paymentId={}, reservationId={}, {} -> {}, eventId={}, captureId={}",
                payment.getId(),
                payment.getReservationHebergement() != null ? payment.getReservationHebergement().getIdReservation() : null,
                previousStatus,
                newStatus,
                eventId,
                captureId);
    }
}
