package com.beninexplo.backend.service;

import com.beninexplo.backend.config.PayPalProperties;
import com.beninexplo.backend.dto.ReservationResponseDTO;
import com.beninexplo.backend.dto.payment.CaptureCircuitPayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.CircuitPayPalCaptureResponseDTO;
import com.beninexplo.backend.dto.payment.CircuitPayPalOrderResponseDTO;
import com.beninexplo.backend.dto.payment.CreateCircuitPayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.HebergementPayPalConfigDTO;
import com.beninexplo.backend.entity.PaiementReservationCircuit;
import com.beninexplo.backend.entity.Reservation;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.PaiementReservationCircuitRepository;
import com.beninexplo.backend.repository.ReservationRepository;
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
public class ReservationCircuitPaymentService {

    private static final Logger log = LoggerFactory.getLogger(ReservationCircuitPaymentService.class);

    private final PaiementReservationCircuitRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final AuthenticatedUserService authenticatedUserService;
    private final PayPalApiClient payPalApiClient;
    private final PayPalProperties payPalProperties;

    public ReservationCircuitPaymentService(PaiementReservationCircuitRepository paymentRepository,
                                            ReservationRepository reservationRepository,
                                            ReservationService reservationService,
                                            AuthenticatedUserService authenticatedUserService,
                                            PayPalApiClient payPalApiClient,
                                            PayPalProperties payPalProperties) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.reservationService = reservationService;
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

    public CircuitPayPalOrderResponseDTO createOrder(CreateCircuitPayPalOrderRequestDTO request) {
        Reservation reservation = getOwnedReservation(request.getReservationId());
        validateReservationIsPayable(reservation);
        BigDecimal amount = getPayableAmount(reservation);

        PaiementReservationCircuit payment = getOrCreatePayment(reservation, amount);
        if ("PAYE".equals(normalizePaymentStatus(payment.getStatut()))) {
            throw new BadRequestException("Cette reservation a deja ete reglee.");
        }

        String requestId = UUID.randomUUID().toString();
        log.info("Starting PayPal create-order for circuit: reservationId={}, userId={}, paymentId={}, amount={}, currency={}, locale={}, circuit={}, dateReservation={}, returnUrl={}, cancelUrl={}",
                reservation.getIdReservation(),
                reservation.getUtilisateur() != null ? reservation.getUtilisateur().getId() : null,
                payment.getId(),
                amount,
                payPalProperties.getCurrency(),
                payPalProperties.getLocale(),
                reservation.getCircuit().getNom(),
                reservation.getDateReservation(),
                shortenUrl(request.getReturnUrl()),
                shortenUrl(request.getCancelUrl()));

        JsonNode response = payPalApiClient.createOrder(
                amount,
                buildDescription(reservation),
                "circuit-" + reservation.getIdReservation(),
                "reservation-circuit-" + reservation.getIdReservation(),
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

        CircuitPayPalOrderResponseDTO dto = new CircuitPayPalOrderResponseDTO();
        dto.setReservationId(reservation.getIdReservation());
        dto.setOrderId(orderId);
        dto.setStatus(response.path("status").asText(""));
        dto.setStatutPaiement(payment.getStatut());
        dto.setMontant(payment.getMontant());
        dto.setDevise(payment.getDevise());
        return dto;
    }

    public CircuitPayPalCaptureResponseDTO captureOrder(CaptureCircuitPayPalOrderRequestDTO request) {
        Reservation reservation = getOwnedReservation(request.getReservationId());
        BigDecimal amount = getPayableAmount(reservation);
        PaiementReservationCircuit payment = getOrCreatePayment(reservation, amount);

        if ("PAYE".equals(normalizePaymentStatus(payment.getStatut()))
                && request.getOrderId().equals(payment.getPaypalOrderId())) {
            return buildCaptureResponse(payment, reservationService.getMineById(reservation.getIdReservation()), "COMPLETED");
        }

        if (StringUtils.hasText(payment.getPaypalOrderId())
                && !request.getOrderId().equals(payment.getPaypalOrderId())) {
            throw new BadRequestException("La commande PayPal ne correspond pas a cette reservation.");
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

        return buildCaptureResponse(payment, reservationService.getMineById(reservation.getIdReservation()), orderStatus);
    }

    private CircuitPayPalCaptureResponseDTO buildCaptureResponse(PaiementReservationCircuit payment,
                                                                 ReservationResponseDTO reservation,
                                                                 String status) {
        CircuitPayPalCaptureResponseDTO dto = new CircuitPayPalCaptureResponseDTO();
        dto.setReservationId(reservation.getId());
        dto.setOrderId(payment.getPaypalOrderId());
        dto.setCaptureId(payment.getPaypalCaptureId());
        dto.setStatus(status);
        dto.setStatutPaiement(payment.getStatut());
        dto.setReservation(reservation);
        return dto;
    }

    private PaiementReservationCircuit getOrCreatePayment(Reservation reservation, BigDecimal amount) {
        return paymentRepository.findByReservationIdReservation(reservation.getIdReservation())
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
                    PaiementReservationCircuit payment = new PaiementReservationCircuit();
                    payment.setReservation(reservation);
                    payment.setProvider("PAYPAL");
                    payment.setStatut("A_PAYER");
                    payment.setMontant(amount);
                    payment.setDevise(payPalProperties.getCurrency());
                    return paymentRepository.save(payment);
                });
    }

    private Reservation getOwnedReservation(Long reservationId) {
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        return reservationRepository.findByIdReservationAndUtilisateurId(reservationId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation circuit introuvable pour ce compte."));
    }

    private void validateReservationIsPayable(Reservation reservation) {
        String reservationStatus = normalizeReservationStatus(reservation.getStatut());
        if ("ANNULEE".equals(reservationStatus)) {
            throw new BadRequestException("Une reservation annulee ne peut pas etre payee.");
        }
        if (getPayableAmount(reservation).compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Cette reservation ne contient pas de montant payable.");
        }
    }

    private BigDecimal getPayableAmount(Reservation reservation) {
        if (reservation.getCircuit() == null || reservation.getCircuit().getPrixIndicatif() == null) {
            throw new BadRequestException("Ce circuit ne contient pas de montant payable.");
        }
        return reservation.getCircuit().getPrixIndicatif();
    }

    private String buildDescription(Reservation reservation) {
        return "Reservation circuit " + reservation.getCircuit().getNom()
                + " pour le " + reservation.getDateReservation();
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

    private String normalizeReservationStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return "EN_ATTENTE";
        }
        String normalized = status.trim().toUpperCase();
        if ("ANNULE".equals(normalized)) {
            return "ANNULEE";
        }
        return normalized;
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
