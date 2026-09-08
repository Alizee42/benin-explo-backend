package com.beninexplo.backend;

import com.beninexplo.backend.config.PayPalProperties;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.service.PayPalApiClient;
import com.beninexplo.backend.service.payment.PayPalPaymentOrchestrator;
import com.beninexplo.backend.service.payment.PayablePaymentAdapter;
import com.beninexplo.backend.service.payment.PaymentRecord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Teste PayPalPaymentOrchestrator de façon isolée (sans Spring, sans base), avec un adaptateur
 * et un PayPalApiClient factices. Ces règles sont désormais partagées par les 3 flux de
 * paiement (circuit, hébergement, circuit personnalisé) — un bug ici impacterait les 3 à la fois.
 */
class PayPalPaymentOrchestratorTests {

    private static final Long ENTITY_ID = 1L;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private PayPalApiClient payPalApiClient;
    private FakeAdapter adapter;
    private PayPalPaymentOrchestrator<String, FakePayment> orchestrator;

    @BeforeEach
    void setUp() {
        payPalApiClient = mock(PayPalApiClient.class);
        PayPalProperties payPalProperties = new PayPalProperties();
        payPalProperties.setEnabled(true);
        payPalProperties.setClientId("client-id");
        payPalProperties.setClientSecret("client-secret");
        payPalProperties.setCurrency("EUR");
        adapter = new FakeAdapter();
        orchestrator = new PayPalPaymentOrchestrator<>(adapter, payPalApiClient, payPalProperties);
    }

    @Test
    void createOrderPersistsPaypalOrderIdAndPaidStatusIsMapped() throws Exception {
        JsonNode response = objectMapper.readTree("""
                {"id": "ORDER-123", "status": "CREATED"}
                """);
        when(payPalApiClient.createOrder(any(), anyString(), anyString(), anyString(), any(), any(), anyString()))
                .thenReturn(response);

        PayPalPaymentOrchestrator.OrderResult result = orchestrator.createOrder(ENTITY_ID, "https://return", "https://cancel");

        assertEquals("ORDER-123", result.orderId);
        assertEquals("EN_COURS", result.paymentStatus);
        assertEquals(0, BigDecimal.valueOf(42).compareTo(result.montant));
        FakePayment stored = adapter.paymentFor(ENTITY_ID);
        assertEquals("ORDER-123", stored.paypalOrderId);
        assertEquals("EN_COURS", stored.statut);
    }

    @Test
    void createOrderRejectsAlreadyPaidEntity() {
        adapter.paymentFor(ENTITY_ID).statut = "PAYE";

        assertThrows(BadRequestException.class,
                () -> orchestrator.createOrder(ENTITY_ID, "https://return", "https://cancel"));
    }

    @Test
    void captureOrderMapsCompletedCaptureToPaidStatusAndSetsDatePaiement() throws Exception {
        adapter.paymentFor(ENTITY_ID).paypalOrderId = "ORDER-123";

        JsonNode response = objectMapper.readTree("""
                {
                  "status": "COMPLETED",
                  "payer": {"payer_id": "PAYER-1"},
                  "purchase_units": [
                    {"payments": {"captures": [{"id": "CAPTURE-1", "status": "COMPLETED"}]}}
                  ]
                }
                """);
        when(payPalApiClient.captureOrder(anyString(), anyString())).thenReturn(response);

        PayPalPaymentOrchestrator.CaptureResult result = orchestrator.captureOrder(ENTITY_ID, "ORDER-123");

        assertEquals("PAYE", result.paymentStatus);
        assertEquals("CAPTURE-1", result.captureId);
        FakePayment stored = adapter.paymentFor(ENTITY_ID);
        assertEquals("PAYE", stored.statut);
        assertEquals("CAPTURE-1", stored.paypalCaptureId);
        assertEquals(0, BigDecimal.valueOf(42).compareTo(stored.montant));
        assertNotNull(stored.datePaiement);
    }

    @Test
    void captureOrderRejectsMismatchedOrderId() {
        adapter.paymentFor(ENTITY_ID).paypalOrderId = "ORDER-ORIGINAL";

        assertThrows(BadRequestException.class,
                () -> orchestrator.captureOrder(ENTITY_ID, "ORDER-DIFFERENT"));
    }

    @Test
    void captureOrderIsIdempotentWhenAlreadyPaidWithSameOrderId() {
        FakePayment payment = adapter.paymentFor(ENTITY_ID);
        payment.statut = "PAYE";
        payment.paypalOrderId = "ORDER-123";
        payment.paypalCaptureId = "CAPTURE-ALREADY";

        PayPalPaymentOrchestrator.CaptureResult result = orchestrator.captureOrder(ENTITY_ID, "ORDER-123");

        assertEquals("PAYE", result.paymentStatus);
        assertEquals("CAPTURE-ALREADY", result.captureId);
    }

    // --- Doublures de test : simulent un domaine métier quelconque sans dépendre de JPA/Spring ---

    private static class FakePayment implements PaymentRecord {
        Long id = 1L;
        String statut = "A_PAYER";
        BigDecimal montant;
        String devise;
        String paypalOrderId;
        String paypalCaptureId;
        String paypalPayerId;
        String paypalRequestId;
        LocalDateTime datePaiement;
        String orderPayload;
        String capturePayload;

        @Override public Long getId() { return id; }
        @Override public String getStatut() { return statut; }
        @Override public void setStatut(String statut) { this.statut = statut; }
        @Override public BigDecimal getMontant() { return montant; }
        @Override public void setMontant(BigDecimal montant) { this.montant = montant; }
        @Override public String getDevise() { return devise; }
        @Override public void setDevise(String devise) { this.devise = devise; }
        @Override public void setProvider(String provider) { }
        @Override public String getPaypalOrderId() { return paypalOrderId; }
        @Override public void setPaypalOrderId(String paypalOrderId) { this.paypalOrderId = paypalOrderId; }
        @Override public String getPaypalCaptureId() { return paypalCaptureId; }
        @Override public void setPaypalCaptureId(String paypalCaptureId) { this.paypalCaptureId = paypalCaptureId; }
        @Override public void setPaypalPayerId(String paypalPayerId) { this.paypalPayerId = paypalPayerId; }
        @Override public void setPaypalRequestId(String paypalRequestId) { this.paypalRequestId = paypalRequestId; }
        @Override public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }
        @Override public void setOrderPayload(String orderPayload) { this.orderPayload = orderPayload; }
        @Override public void setCapturePayload(String capturePayload) { this.capturePayload = capturePayload; }
    }

    /** L'entité "payable" simulée est simplement la représentation textuelle de son id. */
    private static class FakeAdapter implements PayablePaymentAdapter<String, FakePayment> {
        private final Map<String, FakePayment> payments = new HashMap<>();

        FakePayment paymentFor(Long entityId) {
            return getOrCreatePayment(toEntity(entityId), BigDecimal.valueOf(42));
        }

        private String toEntity(Long entityId) {
            return "entity-" + entityId;
        }

        @Override public String entityLabel() { return "fake-entity"; }
        @Override public String getOwnedEntity(Long entityId) { return toEntity(entityId); }
        @Override public Long getEntityId(String entity) { return ENTITY_ID; }
        @Override public void validatePayable(String entity) { }
        @Override public BigDecimal getPayableAmount(String entity) { return BigDecimal.valueOf(42); }
        @Override public String buildDescription(String entity) { return "Fake payment " + entity; }
        @Override public String referencePrefix() { return "fake"; }

        @Override
        public FakePayment getOrCreatePayment(String entity, BigDecimal amount) {
            return payments.computeIfAbsent(entity, key -> {
                FakePayment payment = new FakePayment();
                payment.montant = amount;
                return payment;
            });
        }

        @Override public void savePayment(FakePayment payment) { }
        @Override public Object getUpdatedEntityView(String entity) { return entity; }
    }
}
