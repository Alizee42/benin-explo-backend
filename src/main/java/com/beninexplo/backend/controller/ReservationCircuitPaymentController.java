package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.payment.CaptureCircuitPayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.CircuitPayPalCaptureResponseDTO;
import com.beninexplo.backend.dto.payment.CircuitPayPalOrderResponseDTO;
import com.beninexplo.backend.dto.payment.CreateCircuitPayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.HebergementPayPalConfigDTO;
import com.beninexplo.backend.service.ReservationCircuitPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paiements/circuit/paypal")
public class ReservationCircuitPaymentController {

    private final ReservationCircuitPaymentService paymentService;

    public ReservationCircuitPaymentController(ReservationCircuitPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/config")
    public ResponseEntity<HebergementPayPalConfigDTO> getConfig() {
        return ResponseEntity.ok(paymentService.getClientConfig());
    }

    @PostMapping("/create-order")
    public ResponseEntity<CircuitPayPalOrderResponseDTO> createOrder(
            @Valid @RequestBody CreateCircuitPayPalOrderRequestDTO request) {
        return ResponseEntity.ok(paymentService.createOrder(request));
    }

    @PostMapping("/capture-order")
    public ResponseEntity<CircuitPayPalCaptureResponseDTO> captureOrder(
            @Valid @RequestBody CaptureCircuitPayPalOrderRequestDTO request) {
        return ResponseEntity.ok(paymentService.captureOrder(request));
    }
}
