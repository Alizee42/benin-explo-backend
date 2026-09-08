package com.beninexplo.backend.service.payment;

import com.beninexplo.backend.config.PayPalProperties;
import com.beninexplo.backend.entity.PaiementReservationCircuit;
import com.beninexplo.backend.entity.Reservation;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.PaiementReservationCircuitRepository;
import com.beninexplo.backend.repository.ReservationRepository;
import com.beninexplo.backend.service.AuthenticatedUserService;
import com.beninexplo.backend.service.ReservationService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Component
public class ReservationCircuitPaymentAdapter implements PayablePaymentAdapter<Reservation, PaiementReservationCircuit> {

    private final ReservationRepository reservationRepository;
    private final PaiementReservationCircuitRepository paymentRepository;
    private final ReservationService reservationService;
    private final AuthenticatedUserService authenticatedUserService;
    private final PayPalProperties payPalProperties;

    public ReservationCircuitPaymentAdapter(ReservationRepository reservationRepository,
                                             PaiementReservationCircuitRepository paymentRepository,
                                             ReservationService reservationService,
                                             AuthenticatedUserService authenticatedUserService,
                                             PayPalProperties payPalProperties) {
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
        this.reservationService = reservationService;
        this.authenticatedUserService = authenticatedUserService;
        this.payPalProperties = payPalProperties;
    }

    @Override
    public String entityLabel() {
        return "reservation circuit";
    }

    @Override
    public Reservation getOwnedEntity(Long entityId) {
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        return reservationRepository.findByIdReservationAndUtilisateurId(entityId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation circuit introuvable pour ce compte."));
    }

    @Override
    public Long getEntityId(Reservation entity) {
        return entity.getIdReservation();
    }

    @Override
    public void validatePayable(Reservation reservation) {
        String status = reservation.getStatut() == null ? "EN_ATTENTE" : reservation.getStatut().trim().toUpperCase();
        String normalized = "ANNULE".equals(status) ? "ANNULEE" : status;
        if ("ANNULEE".equals(normalized)) {
            throw new BadRequestException("Une reservation annulee ne peut pas etre payee.");
        }
        requireAmountPositive(getPayableAmount(reservation), "Cette reservation ne contient pas de montant payable.");
    }

    @Override
    public BigDecimal getPayableAmount(Reservation reservation) {
        if (reservation.getCircuit() == null || reservation.getCircuit().getPrixIndicatif() == null) {
            throw new BadRequestException("Ce circuit ne contient pas de montant payable.");
        }
        return reservation.getCircuit().getPrixIndicatif();
    }

    @Override
    public String buildDescription(Reservation reservation) {
        return "Reservation circuit " + reservation.getCircuit().getNom()
                + " pour le " + reservation.getDateReservation();
    }

    @Override
    public String referencePrefix() {
        return "circuit";
    }

    @Override
    public PaiementReservationCircuit getOrCreatePayment(Reservation reservation, BigDecimal amount) {
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

    @Override
    public void savePayment(PaiementReservationCircuit payment) {
        paymentRepository.save(payment);
    }

    @Override
    public Object getUpdatedEntityView(Reservation entity) {
        return reservationService.getMineById(entity.getIdReservation());
    }
}
