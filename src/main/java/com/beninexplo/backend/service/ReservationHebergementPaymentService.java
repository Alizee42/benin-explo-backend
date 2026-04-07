package com.beninexplo.backend.service;

import com.beninexplo.backend.config.PayPalProperties;
import com.beninexplo.backend.dto.ReservationHebergementDTO;
import com.beninexplo.backend.dto.payment.CaptureHebergementPayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.CreateHebergementPayPalOrderRequestDTO;
import com.beninexplo.backend.dto.payment.HebergementPayPalCaptureResponseDTO;
import com.beninexplo.backend.dto.payment.HebergementPayPalConfigDTO;
import com.beninexplo.backend.dto.payment.HebergementPayPalOrderResponseDTO;
import com.beninexplo.backend.entity.PaiementReservationHebergement;
import com.beninexplo.backend.entity.ReservationHebergement;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.PaiementReservationHebergementRepository;
import com.beninexplo.backend.repository.ReservationHebergementRepository;
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
public class ReservationHebergementPaymentService {

    private static final Logger log = LoggerFactory.getLogger(ReservationHebergementPaymentService.class);

    private final PaiementReservationHebergementRepository paymentRepository;
    private final ReservationHebergementRepository reservationRepository;
    private final ReservationHebergementService reservationHebergementService;
    private final AuthenticatedUserService authenticatedUserService;
    private final PayPalApiClient payPalApiClient;
    private final PayPalProperties payPalProperties;

    public ReservationHebergementPaymentService(PaiementReservationHebergementRepository paymentRepository,
                                                ReservationHebergementRepository reservationRepository,
                                                ReservationHebergementService reservationHebergementService,
                                                AuthenticatedUserService authenticatedUserService,
                                                PayPalApiClient payPalApiClient,
                                                PayPalProperties payPalProperties) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.reservationHebergementService = reservationHebergementService;
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

    public HebergementPayPalOrderResponseDTO createOrder(CreateHebergementPayPalOrderRequestDTO request) {
        ReservationHebergement reservation = getOwnedReservation(request.getReservationId());
        validateReservationIsPayable(reservation);

        PaiementReservationHebergement payment = getOrCreatePayment(reservation);
        if ("PAYE".equals(normalizePaymentStatus(payment.getStatut()))) {
            throw new BadRequestException("Cette reservation a deja ete reglee.");
        }

        String requestId = UUID.randomUUID().toString();
        log.info("Starting PayPal create-order: reservationId={}, userId={}, paymentId={}, amount={}, currency={}, locale={}, hebergement={}, arrivee={}, depart={}, returnUrl={}, cancelUrl={}",
                reservation.getIdReservation(),
                reservation.getUtilisateur() != null ? reservation.getUtilisateur().getId() : null,
                payment.getId(),
                reservation.getPrixTotal(),
                payPalProperties.getCurrency(),
                payPalProperties.getLocale(),
                reservation.getHebergement().getNom(),
                reservation.getDateArrivee(),
                reservation.getDateDepart(),
                shortenUrl(request.getReturnUrl()),
                shortenUrl(request.getCancelUrl()));
        JsonNode response = payPalApiClient.createOrder(
                reservation.getPrixTotal(),
                buildDescription(reservation),
                "hebergement-" + reservation.getIdReservation(),
                "reservation-hebergement-" + reservation.getIdReservation(),
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
        payment.setMontant(reservation.getPrixTotal());
        payment.setDevise(payPalProperties.getCurrency());
        payment.setPaypalOrderId(orderId);
        payment.setPaypalRequestId(requestId);
        payment.setOrderPayload(response.toString());
        paymentRepository.save(payment);

        log.info("PayPal order created: reservationId={}, paymentId={}, orderId={}, paypalStatus={}, paymentStatus={}, requestId={}",
                reservation.getIdReservation(),
                payment.getId(),
                orderId,
                response.path("status").asText(""),
                payment.getStatut(),
                requestId);

        HebergementPayPalOrderResponseDTO dto = new HebergementPayPalOrderResponseDTO();
        dto.setReservationId(reservation.getIdReservation());
        dto.setOrderId(orderId);
        dto.setStatus(response.path("status").asText(""));
        dto.setStatutPaiement(payment.getStatut());
        dto.setMontant(payment.getMontant());
        dto.setDevise(payment.getDevise());
        return dto;
    }

    public HebergementPayPalCaptureResponseDTO captureOrder(CaptureHebergementPayPalOrderRequestDTO request) {
        ReservationHebergement reservation = getOwnedReservation(request.getReservationId());
        PaiementReservationHebergement payment = getOrCreatePayment(reservation);

        log.info("Starting PayPal capture: reservationId={}, userId={}, paymentId={}, requestedOrderId={}, currentOrderId={}, currentPaymentStatus={}",
                reservation.getIdReservation(),
                reservation.getUtilisateur() != null ? reservation.getUtilisateur().getId() : null,
                payment.getId(),
                request.getOrderId(),
                payment.getPaypalOrderId(),
                payment.getStatut());

        if ("PAYE".equals(normalizePaymentStatus(payment.getStatut()))
                && request.getOrderId().equals(payment.getPaypalOrderId())) {
            log.info("PayPal capture skipped because reservation is already paid: reservationId={}, orderId={}",
                    reservation.getIdReservation(), request.getOrderId());
            return buildCaptureResponse(payment, reservationHebergementService.getMineById(reservation.getIdReservation()), "COMPLETED");
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
        payment.setMontant(reservation.getPrixTotal());
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

        log.info("PayPal capture completed: reservationId={}, paymentId={}, orderId={}, captureId={}, orderStatus={}, captureStatus={}, paymentStatus={}, datePaiement={}",
                reservation.getIdReservation(),
                payment.getId(),
                payment.getPaypalOrderId(),
                payment.getPaypalCaptureId(),
                orderStatus,
                captureStatus,
                payment.getStatut(),
                payment.getDatePaiement());

        ReservationHebergementDTO updatedReservation = reservationHebergementService.getMineById(reservation.getIdReservation());
        return buildCaptureResponse(payment, updatedReservation, orderStatus);
    }

    private HebergementPayPalCaptureResponseDTO buildCaptureResponse(PaiementReservationHebergement payment,
                                                                     ReservationHebergementDTO reservation,
                                                                     String status) {
        HebergementPayPalCaptureResponseDTO dto = new HebergementPayPalCaptureResponseDTO();
        dto.setReservationId(reservation.getId());
        dto.setOrderId(payment.getPaypalOrderId());
        dto.setCaptureId(payment.getPaypalCaptureId());
        dto.setStatus(status);
        dto.setStatutPaiement(payment.getStatut());
        dto.setReservation(reservation);
        return dto;
    }

    private PaiementReservationHebergement getOrCreatePayment(ReservationHebergement reservation) {
        return paymentRepository.findByReservationHebergementIdReservation(reservation.getIdReservation())
                .map(existing -> {
                    existing.setMontant(reservation.getPrixTotal());
                    if (!StringUtils.hasText(existing.getDevise())) {
                        existing.setDevise(payPalProperties.getCurrency());
                    }
                    if (!StringUtils.hasText(existing.getStatut())) {
                        existing.setStatut("A_PAYER");
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    PaiementReservationHebergement payment = new PaiementReservationHebergement();
                    payment.setReservationHebergement(reservation);
                    payment.setProvider("PAYPAL");
                    payment.setStatut("A_PAYER");
                    payment.setMontant(reservation.getPrixTotal());
                    payment.setDevise(payPalProperties.getCurrency());
                    return paymentRepository.save(payment);
                });
    }

    private ReservationHebergement getOwnedReservation(Long reservationId) {
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        return reservationRepository.findByIdReservationAndUtilisateurId(reservationId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation hebergement introuvable pour ce compte."));
    }

    private void validateReservationIsPayable(ReservationHebergement reservation) {
        String reservationStatus = normalizeReservationStatus(reservation.getStatut());
        if ("ANNULEE".equals(reservationStatus)) {
            throw new BadRequestException("Une reservation annulee ne peut pas etre payee.");
        }
        if (reservation.getPrixTotal() == null || reservation.getPrixTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Cette reservation ne contient pas de montant payable.");
        }
    }

    private String buildDescription(ReservationHebergement reservation) {
        return "Reservation hebergement " + reservation.getHebergement().getNom()
                + " du " + reservation.getDateArrivee()
                + " au " + reservation.getDateDepart();
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
