package com.beninexplo.backend.service.payment;

import com.beninexplo.backend.config.PayPalProperties;
import com.beninexplo.backend.dto.payment.HebergementPayPalConfigDTO;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.service.PayPalApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Porte toute la mécanique commune aux flux de paiement PayPal (création et capture de
 * commande, mapping des statuts PayPal -> statuts internes, garde-fous anti double-paiement).
 *
 * Chaque domaine métier (circuit, hébergement, circuit personnalisé) fournit uniquement un
 * {@link PayablePaymentAdapter} qui sait retrouver son entité et son paiement ; cet
 * orchestrateur ne connaît aucun détail métier des 3 domaines.
 *
 * @param <E> le type de l'entité payable
 * @param <P> le type de paiement associé
 */
public class PayPalPaymentOrchestrator<E, P extends PaymentRecord> {

    private static final Logger log = LoggerFactory.getLogger(PayPalPaymentOrchestrator.class);

    private final PayablePaymentAdapter<E, P> adapter;
    private final PayPalApiClient payPalApiClient;
    private final PayPalProperties payPalProperties;

    public PayPalPaymentOrchestrator(PayablePaymentAdapter<E, P> adapter,
                                      PayPalApiClient payPalApiClient,
                                      PayPalProperties payPalProperties) {
        this.adapter = adapter;
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

    public static final class OrderResult {
        public final Long entityId;
        public final String orderId;
        public final String paypalStatus;
        public final String paymentStatus;
        public final BigDecimal montant;
        public final String devise;

        private OrderResult(Long entityId, String orderId, String paypalStatus, String paymentStatus,
                             BigDecimal montant, String devise) {
            this.entityId = entityId;
            this.orderId = orderId;
            this.paypalStatus = paypalStatus;
            this.paymentStatus = paymentStatus;
            this.montant = montant;
            this.devise = devise;
        }
    }

    public OrderResult createOrder(Long entityId, String returnUrl, String cancelUrl) {
        E entity = adapter.getOwnedEntity(entityId);
        adapter.validatePayable(entity);
        BigDecimal amount = adapter.getPayableAmount(entity);

        P payment = adapter.getOrCreatePayment(entity, amount);
        if ("PAYE".equals(normalizePaymentStatus(payment.getStatut()))) {
            throw new BadRequestException("Ce paiement a deja ete regle.");
        }

        String requestId = UUID.randomUUID().toString();
        Long id = adapter.getEntityId(entity);
        log.info("Starting PayPal create-order for {}: entityId={}, paymentId={}, amount={}, currency={}, locale={}, returnUrl={}, cancelUrl={}",
                adapter.entityLabel(), id, payment.getId(), amount, payPalProperties.getCurrency(),
                payPalProperties.getLocale(), shortenUrl(returnUrl), shortenUrl(cancelUrl));

        JsonNode response = payPalApiClient.createOrder(
                amount,
                adapter.buildDescription(entity),
                adapter.referencePrefix() + "-" + id,
                adapter.referencePrefix() + "-" + id,
                returnUrl,
                cancelUrl,
                requestId
        );

        String orderId = response.path("id").asText("");
        if (!StringUtils.hasText(orderId)) {
            throw new BadRequestException("PayPal n'a pas retourne de commande exploitable.");
        }

        String paypalStatus = response.path("status").asText("");
        payment.setProvider("PAYPAL");
        payment.setStatut(mapOrderStatusToPaymentStatus(paypalStatus));
        payment.setMontant(amount);
        payment.setDevise(payPalProperties.getCurrency());
        payment.setPaypalOrderId(orderId);
        payment.setPaypalRequestId(requestId);
        payment.setOrderPayload(response.toString());
        adapter.savePayment(payment);

        log.info("PayPal order created for {}: entityId={}, paymentId={}, orderId={}, paypalStatus={}, paymentStatus={}",
                adapter.entityLabel(), id, payment.getId(), orderId, paypalStatus, payment.getStatut());

        return new OrderResult(id, orderId, paypalStatus, payment.getStatut(), payment.getMontant(), payment.getDevise());
    }

    public static final class CaptureResult {
        public final Object entityView;
        public final String orderId;
        public final String captureId;
        public final String status;
        public final String paymentStatus;

        private CaptureResult(Object entityView, String orderId, String captureId, String status, String paymentStatus) {
            this.entityView = entityView;
            this.orderId = orderId;
            this.captureId = captureId;
            this.status = status;
            this.paymentStatus = paymentStatus;
        }
    }

    public CaptureResult captureOrder(Long entityId, String orderId) {
        E entity = adapter.getOwnedEntity(entityId);
        BigDecimal amount = adapter.getPayableAmount(entity);
        P payment = adapter.getOrCreatePayment(entity, amount);

        if ("PAYE".equals(normalizePaymentStatus(payment.getStatut())) && orderId.equals(payment.getPaypalOrderId())) {
            return new CaptureResult(adapter.getUpdatedEntityView(entity), payment.getPaypalOrderId(),
                    payment.getPaypalCaptureId(), "COMPLETED", payment.getStatut());
        }

        if (StringUtils.hasText(payment.getPaypalOrderId()) && !orderId.equals(payment.getPaypalOrderId())) {
            throw new BadRequestException("La commande PayPal ne correspond pas a ce paiement.");
        }

        String requestId = UUID.randomUUID().toString();
        JsonNode response = payPalApiClient.captureOrder(orderId, requestId);
        JsonNode captureNode = response.path("purchase_units").path(0).path("payments").path("captures").path(0);

        String orderStatus = response.path("status").asText("");
        String captureStatus = captureNode.path("status").asText(orderStatus);
        String paymentStatus = mapCaptureStatusToPaymentStatus(orderStatus, captureStatus);

        payment.setProvider("PAYPAL");
        payment.setStatut(paymentStatus);
        payment.setMontant(amount);
        payment.setDevise(payPalProperties.getCurrency());
        payment.setPaypalOrderId(orderId);
        payment.setPaypalCaptureId(blankToNull(captureNode.path("id").asText("")));
        payment.setPaypalPayerId(blankToNull(response.path("payer").path("payer_id").asText("")));
        payment.setPaypalRequestId(requestId);
        payment.setCapturePayload(response.toString());
        if ("PAYE".equals(paymentStatus)) {
            payment.setDatePaiement(LocalDateTime.now());
        }
        adapter.savePayment(payment);

        log.info("PayPal capture completed for {}: entityId={}, paymentId={}, orderId={}, captureId={}, orderStatus={}, captureStatus={}, paymentStatus={}",
                adapter.entityLabel(), adapter.getEntityId(entity), payment.getId(), payment.getPaypalOrderId(),
                payment.getPaypalCaptureId(), orderStatus, captureStatus, payment.getStatut());

        return new CaptureResult(adapter.getUpdatedEntityView(entity), payment.getPaypalOrderId(),
                payment.getPaypalCaptureId(), orderStatus, payment.getStatut());
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
