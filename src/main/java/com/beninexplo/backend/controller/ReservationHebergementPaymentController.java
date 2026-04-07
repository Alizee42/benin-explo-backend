package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.payment.CaptureHebergementPayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.CreateHebergementPayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.HebergementPayPalCaptureResponseDTO;
import com.beninexplo.backend.dto.payment.HebergementPayPalConfigDTO;
import com.beninexplo.backend.dto.payment.HebergementPayPalOrderResponseDTO;
import com.beninexplo.backend.service.ReservationHebergementPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paiements/hebergement/paypal")
public class ReservationHebergementPaymentController {

    private final ReservationHebergementPaymentService paymentService;

    public ReservationHebergementPaymentController(ReservationHebergementPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/config")
    public ResponseEntity<HebergementPayPalConfigDTO> getConfig() {
        return ResponseEntity.ok(paymentService.getClientConfig());
    }

    @PostMapping("/create-order")
    public ResponseEntity<HebergementPayPalOrderResponseDTO> createOrder(
            @Valid @RequestBody CreateHebergementPayPalOrderRequestDTO request) {
        return ResponseEntity.ok(paymentService.createOrder(request));
    }

    @PostMapping("/capture-order")
    public ResponseEntity<HebergementPayPalCaptureResponseDTO> captureOrder(
            @Valid @RequestBody CaptureHebergementPayPalOrderRequestDTO request) {
        return ResponseEntity.ok(paymentService.captureOrder(request));
    }
}
