package com.beninexplo.backend.service.payment;

import com.beninexplo.backend.config.PayPalProperties;
import com.beninexplo.backend.entity.CircuitPersonnalise;
import com.beninexplo.backend.entity.PaiementCircuitPersonnalise;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.CircuitPersonnaliseRepository;
import com.beninexplo.backend.repository.PaiementCircuitPersonnaliseRepository;
import com.beninexplo.backend.service.AuthenticatedUserService;
import com.beninexplo.backend.service.CircuitPersonnaliseService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Component
public class CircuitPersonnalisePaymentAdapter
        implements PayablePaymentAdapter<CircuitPersonnalise, PaiementCircuitPersonnalise> {

    private final CircuitPersonnaliseRepository circuitPersonnaliseRepository;
    private final PaiementCircuitPersonnaliseRepository paymentRepository;
    private final CircuitPersonnaliseService circuitPersonnaliseService;
    private final AuthenticatedUserService authenticatedUserService;
    private final PayPalProperties payPalProperties;

    public CircuitPersonnalisePaymentAdapter(CircuitPersonnaliseRepository circuitPersonnaliseRepository,
                                              PaiementCircuitPersonnaliseRepository paymentRepository,
                                              CircuitPersonnaliseService circuitPersonnaliseService,
                                              AuthenticatedUserService authenticatedUserService,
                                              PayPalProperties payPalProperties) {
        this.circuitPersonnaliseRepository = circuitPersonnaliseRepository;
        this.paymentRepository = paymentRepository;
        this.circuitPersonnaliseService = circuitPersonnaliseService;
        this.authenticatedUserService = authenticatedUserService;
        this.payPalProperties = payPalProperties;
    }

    @Override
    public String entityLabel() {
        return "circuit personnalise";
    }

    @Override
    public CircuitPersonnalise getOwnedEntity(Long entityId) {
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        CircuitPersonnalise demande = circuitPersonnaliseRepository.findById(entityId)
                .orElseThrow(() -> new ResourceNotFoundException("Devis personnalise introuvable pour ce compte."));

        boolean ownedByUserId = demande.getUtilisateur() != null
                && demande.getUtilisateur().getId() != null
                && demande.getUtilisateur().getId().equals(currentUser.getId());
        boolean ownedByEmail = StringUtils.hasText(demande.getEmailClient())
                && demande.getEmailClient().trim().equalsIgnoreCase(currentUser.getEmail());
        if (!ownedByUserId && !ownedByEmail) {
            throw new ResourceNotFoundException("Devis personnalise introuvable pour ce compte.");
        }
        return demande;
    }

    @Override
    public Long getEntityId(CircuitPersonnalise entity) {
        return entity.getId();
    }

    @Override
    public void validatePayable(CircuitPersonnalise demande) {
        String status = demande.getStatut() != null ? demande.getStatut().name() : "EN_ATTENTE";
        if ("REFUSE".equalsIgnoreCase(status)) {
            throw new BadRequestException("Un devis refuse ne peut pas etre paye.");
        }
        if (!"ACCEPTE".equalsIgnoreCase(status)) {
            throw new BadRequestException("Le paiement sera disponible une fois le devis valide.");
        }
        requireAmountPositive(getPayableAmount(demande), "Ce devis ne contient pas de montant payable.");
    }

    @Override
    public BigDecimal getPayableAmount(CircuitPersonnalise demande) {
        if (demande.getPrixFinal() == null || demande.getPrixFinal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Le devis final n'est pas encore pret pour le paiement.");
        }
        return demande.getPrixFinal();
    }

    @Override
    public String buildDescription(CircuitPersonnalise demande) {
        String reference = StringUtils.hasText(demande.getReferenceReservation())
                ? demande.getReferenceReservation()
                : "CPS-" + String.format("%06d", demande.getId());
        return "Paiement circuit personnalise " + reference
                + " - " + demande.getNombreJours() + " jour(s)";
    }

    @Override
    public String referencePrefix() {
        return "circuit-personnalise";
    }

    @Override
    public PaiementCircuitPersonnalise getOrCreatePayment(CircuitPersonnalise demande, BigDecimal amount) {
        return paymentRepository.findByCircuitPersonnaliseId(demande.getId())
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
                    PaiementCircuitPersonnalise payment = new PaiementCircuitPersonnalise();
                    payment.setCircuitPersonnalise(demande);
                    payment.setProvider("PAYPAL");
                    payment.setStatut("A_PAYER");
                    payment.setMontant(amount);
                    payment.setDevise(payPalProperties.getCurrency());
                    return paymentRepository.save(payment);
                });
    }

    @Override
    public void savePayment(PaiementCircuitPersonnalise payment) {
        paymentRepository.save(payment);
    }

    @Override
    public Object getUpdatedEntityView(CircuitPersonnalise entity) {
        return circuitPersonnaliseService.getMineById(entity.getId());
    }
}
