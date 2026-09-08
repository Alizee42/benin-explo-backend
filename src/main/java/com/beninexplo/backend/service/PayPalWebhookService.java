package com.beninexplo.backend.service;

import com.beninexplo.backend.config.PayPalProperties;
import com.beninexplo.backend.entity.PaiementCircuitPersonnalise;
import com.beninexplo.backend.entity.PaiementReservationCircuit;
import com.beninexplo.backend.entity.PaiementReservationHebergement;
import com.beninexplo.backend.repository.PaiementCircuitPersonnaliseRepository;
import com.beninexplo.backend.repository.PaiementReservationCircuitRepository;
import com.beninexplo.backend.repository.PaiementReservationHebergementRepository;
import com.beninexplo.backend.service.payment.PaymentRecord;
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

    private final PaiementReservationHebergementRepository hebergementPaymentRepository;
    private final PaiementReservationCircuitRepository circuitPaymentRepository;
    private final PaiementCircuitPersonnaliseRepository circuitPersonnalisePaymentRepository;
    private final PayPalApiClient payPalApiClient;
    private final PayPalProperties payPalProperties;
    private final ObjectMapper objectMapper;

    public PayPalWebhookService(PaiementReservationHebergementRepository hebergementPaymentRepository,
                                PaiementReservationCircuitRepository circuitPaymentRepository,
                                PaiementCircuitPersonnaliseRepository circuitPersonnalisePaymentRepository,
                                PayPalApiClient payPalApiClient,
                                PayPalProperties payPalProperties,
                                ObjectMapper objectMapper) {
        this.hebergementPaymentRepository = hebergementPaymentRepository;
        this.circuitPaymentRepository = circuitPaymentRepository;
        this.circuitPersonnalisePaymentRepository = circuitPersonnalisePaymentRepository;
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
            case "PAYMENT.CAPTURE.COMPLETED" -> handleCapture(eventId, resource, "PAYE");
            case "PAYMENT.CAPTURE.REFUNDED"  -> handleCapture(eventId, resource, "REMBOURSE");
            case "PAYMENT.CAPTURE.DENIED"    -> handleCapture(eventId, resource, "ECHEC");
            case "PAYMENT.CAPTURE.PENDING"   -> handleCapture(eventId, resource, "EN_COURS");
            case "PAYMENT.CAPTURE.REVERSED"  -> handleCapture(eventId, resource, "REMBOURSE");
            default -> log.debug("Webhook PayPal ignore (type non gere): eventId={}, eventType={}", eventId, eventType);
        }
    }

    private void handleCapture(String eventId, JsonNode resource, String newStatus) {
        Optional<? extends PaymentRecord> payment = findPaymentFromCapture(resource);
        payment.ifPresentOrElse(
                p -> updatePaymentStatus(p, newStatus, eventId, resource),
                () -> log.warn("Webhook PayPal: aucun paiement trouve (aucun des 3 flux), eventId={}, captureId={}",
                        eventId, resource.path("id").asText(""))
        );
    }

    /**
     * Cherche le paiement correspondant à un evenement capture PayPal, en essayant tour à tour
     * les 3 flux de paiement (hébergement, circuit, circuit personnalisé) : PayPal ne dit pas
     * dans son event à quel domaine métier appartient une capture, donc on ne peut pas savoir
     * a priori dans quelle table chercher. Tente d'abord par captureId, puis par orderId.
     */
    private Optional<? extends PaymentRecord> findPaymentFromCapture(JsonNode resource) {
        String captureId = resource.path("id").asText("");
        String orderId = resource.path("supplementary_data").path("related_ids").path("order_id").asText("");

        Optional<PaiementReservationHebergement> hebergement = findByIds(
                hebergementPaymentRepository::findByPaypalCaptureId,
                hebergementPaymentRepository::findByPaypalOrderId,
                captureId, orderId);
        if (hebergement.isPresent()) {
            return hebergement;
        }

        Optional<PaiementReservationCircuit> circuit = findByIds(
                circuitPaymentRepository::findByPaypalCaptureId,
                circuitPaymentRepository::findByPaypalOrderId,
                captureId, orderId);
        if (circuit.isPresent()) {
            return circuit;
        }

        return findByIds(
                circuitPersonnalisePaymentRepository::findByPaypalCaptureId,
                circuitPersonnalisePaymentRepository::findByPaypalOrderId,
                captureId, orderId);
    }

    private <P> Optional<P> findByIds(java.util.function.Function<String, Optional<P>> findByCaptureId,
                                      java.util.function.Function<String, Optional<P>> findByOrderId,
                                      String captureId, String orderId) {
        if (StringUtils.hasText(captureId)) {
            Optional<P> byCapture = findByCaptureId.apply(captureId);
            if (byCapture.isPresent()) {
                return byCapture;
            }
        }
        if (StringUtils.hasText(orderId)) {
            return findByOrderId.apply(orderId);
        }
        return Optional.empty();
    }

    private void updatePaymentStatus(PaymentRecord payment, String newStatus, String eventId, JsonNode resource) {
        String previousStatus = payment.getStatut();
        String captureId = resource.path("id").asText("");

        // Ne pas rétrograder un paiement déjà remboursé vers un statut inférieur (ex: PAYE).
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

        savePayment(payment);

        log.info("PayPal webhook: statut paiement mis a jour. paymentId={}, {} -> {}, eventId={}, captureId={}",
                payment.getId(), previousStatus, newStatus, eventId, captureId);
    }

    private void savePayment(PaymentRecord payment) {
        if (payment instanceof PaiementReservationHebergement hebergement) {
            hebergementPaymentRepository.save(hebergement);
        } else if (payment instanceof PaiementReservationCircuit circuit) {
            circuitPaymentRepository.save(circuit);
        } else if (payment instanceof PaiementCircuitPersonnalise circuitPersonnalise) {
            circuitPersonnalisePaymentRepository.save(circuitPersonnalise);
        }
    }
}
