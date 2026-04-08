package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.payment.CaptureCircuitPersonnalisePayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.CircuitPersonnalisePayPalCaptureResponseDTO;
import com.beninexplo.backend.dto.payment.CircuitPersonnalisePayPalOrderResponseDTO;
import com.beninexplo.backend.dto.payment.CreateCircuitPersonnalisePayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.HebergementPayPalConfigDTO;
import com.beninexplo.backend.service.CircuitPersonnalisePaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paiements/circuit-personnalise/paypal")
public class CircuitPersonnalisePaymentController {

    private final CircuitPersonnalisePaymentService paymentService;

    public CircuitPersonnalisePaymentController(CircuitPersonnalisePaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/config")
    public ResponseEntity<HebergementPayPalConfigDTO> getConfig() {
        return ResponseEntity.ok(paymentService.getClientConfig());
    }

    @PostMapping("/create-order")
    public ResponseEntity<CircuitPersonnalisePayPalOrderResponseDTO> createOrder(
            @Valid @RequestBody CreateCircuitPersonnalisePayPalOrderRequestDTO request) {
        return ResponseEntity.ok(paymentService.createOrder(request));
    }

    @PostMapping("/capture-order")
    public ResponseEntity<CircuitPersonnalisePayPalCaptureResponseDTO> captureOrder(
            @Valid @RequestBody CaptureCircuitPersonnalisePayPalOrderRequestDTO request) {
        return ResponseEntity.ok(paymentService.captureOrder(request));
    }
}
