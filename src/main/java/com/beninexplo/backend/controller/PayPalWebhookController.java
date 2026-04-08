package com.beninexplo.backend.controller;

import com.beninexplo.backend.service.PayPalWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks/paypal")
public class PayPalWebhookController {

    private static final Logger log = LoggerFactory.getLogger(PayPalWebhookController.class);

    private final PayPalWebhookService webhookService;

    public PayPalWebhookController(PayPalWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader(value = "PAYPAL-AUTH-ALGO", required = false) String authAlgo,
            @RequestHeader(value = "PAYPAL-CERT-URL", required = false) String certUrl,
            @RequestHeader(value = "PAYPAL-TRANSMISSION-ID", required = false) String transmissionId,
            @RequestHeader(value = "PAYPAL-TRANSMISSION-SIG", required = false) String transmissionSig,
            @RequestHeader(value = "PAYPAL-TRANSMISSION-TIME", required = false) String transmissionTime,
            @RequestBody String rawBody) {

        boolean valid = webhookService.verifySignature(
                authAlgo, certUrl, transmissionId, transmissionSig, transmissionTime, rawBody);

        if (!valid) {
            log.warn("Webhook PayPal rejete: signature invalide. transmissionId={}", transmissionId);
            return ResponseEntity.status(401).build();
        }

        try {
            webhookService.processEvent(rawBody);
        } catch (Exception ex) {
            // On renvoie 200 quand même pour éviter que PayPal retente indéfiniment
            log.error("Erreur lors du traitement du webhook PayPal: transmissionId={}", transmissionId, ex);
        }

        return ResponseEntity.ok().build();
    }
}
