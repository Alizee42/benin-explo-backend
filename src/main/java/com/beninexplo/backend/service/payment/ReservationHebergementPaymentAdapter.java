package com.beninexplo.backend.service.payment;

import com.beninexplo.backend.config.PayPalProperties;
import com.beninexplo.backend.entity.PaiementReservationHebergement;
import com.beninexplo.backend.entity.ReservationHebergement;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.PaiementReservationHebergementRepository;
import com.beninexplo.backend.repository.ReservationHebergementRepository;
import com.beninexplo.backend.service.AuthenticatedUserService;
import com.beninexplo.backend.service.ReservationHebergementService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Component
public class ReservationHebergementPaymentAdapter
        implements PayablePaymentAdapter<ReservationHebergement, PaiementReservationHebergement> {

    private final ReservationHebergementRepository reservationRepository;
    private final PaiementReservationHebergementRepository paymentRepository;
    private final ReservationHebergementService reservationHebergementService;
    private final AuthenticatedUserService authenticatedUserService;
    private final PayPalProperties payPalProperties;

    public ReservationHebergementPaymentAdapter(ReservationHebergementRepository reservationRepository,
                                                 PaiementReservationHebergementRepository paymentRepository,
                                                 ReservationHebergementService reservationHebergementService,
                                                 AuthenticatedUserService authenticatedUserService,
                                                 PayPalProperties payPalProperties) {
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
        this.reservationHebergementService = reservationHebergementService;
        this.authenticatedUserService = authenticatedUserService;
        this.payPalProperties = payPalProperties;
    }

    @Override
    public String entityLabel() {
        return "reservation hebergement";
    }

    @Override
    public ReservationHebergement getOwnedEntity(Long entityId) {
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        return reservationRepository.findByIdReservationAndUtilisateurId(entityId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation hebergement introuvable pour ce compte."));
    }

    @Override
    public Long getEntityId(ReservationHebergement entity) {
        return entity.getIdReservation();
    }

    @Override
    public void validatePayable(ReservationHebergement reservation) {
        String status = reservation.getStatut() == null ? "EN_ATTENTE" : reservation.getStatut().trim().toUpperCase();
        String normalized = "ANNULE".equals(status) ? "ANNULEE" : status;
        if ("ANNULEE".equals(normalized)) {
            throw new BadRequestException("Une reservation annulee ne peut pas etre payee.");
        }
        requireAmountPositive(getPayableAmount(reservation), "Cette reservation ne contient pas de montant payable.");
    }

    @Override
    public BigDecimal getPayableAmount(ReservationHebergement reservation) {
        return reservation.getPrixTotal();
    }

    @Override
    public String buildDescription(ReservationHebergement reservation) {
        return "Reservation hebergement " + reservation.getHebergement().getNom()
                + " du " + reservation.getDateArrivee()
                + " au " + reservation.getDateDepart();
    }

    @Override
    public String referencePrefix() {
        return "hebergement";
    }

    @Override
    public PaiementReservationHebergement getOrCreatePayment(ReservationHebergement reservation, BigDecimal amount) {
        return paymentRepository.findByReservationHebergementIdReservation(reservation.getIdReservation())
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
                    PaiementReservationHebergement payment = new PaiementReservationHebergement();
                    payment.setReservationHebergement(reservation);
                    payment.setProvider("PAYPAL");
                    payment.setStatut("A_PAYER");
                    payment.setMontant(amount);
                    payment.setDevise(payPalProperties.getCurrency());
                    return paymentRepository.save(payment);
                });
    }

    @Override
    public void savePayment(PaiementReservationHebergement payment) {
        paymentRepository.save(payment);
    }

    @Override
    public Object getUpdatedEntityView(ReservationHebergement entity) {
        return reservationHebergementService.getMineById(entity.getIdReservation());
    }
}
