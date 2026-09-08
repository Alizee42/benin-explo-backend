package com.beninexplo.backend.service.payment;

import com.beninexplo.backend.exception.BadRequestException;

import java.math.BigDecimal;

/**
 * Point de branchement d'un domaine métier (réservation circuit, réservation hébergement,
 * circuit personnalisé...) sur le flux de paiement PayPal générique porté par
 * {@link PayPalPaymentOrchestrator}.
 *
 * <p>Chaque implémentation sait uniquement : retrouver l'entité payable appartenant à
 * l'utilisateur courant, dire si/combien elle coûte, et fournir/rafraîchir son paiement associé.
 * Toute la mécanique PayPal (create-order, capture-order, mapping de statuts) est commune et
 * vit dans l'orchestrateur.
 *
 * @param <E> le type de l'entité payable (Reservation, ReservationHebergement, CircuitPersonnalise...)
 * @param <P> le type de paiement associé (PaiementReservationCircuit, ...)
 */
public interface PayablePaymentAdapter<E, P extends PaymentRecord> {

    /** Libellé utilisé dans les logs et messages d'erreur (ex: "reservation circuit"). */
    String entityLabel();

    /** Retrouve l'entité payable appartenant à l'utilisateur courant, ou lève ResourceNotFoundException. */
    E getOwnedEntity(Long entityId);

    /** Identifiant métier de l'entité (pour les DTO de réponse et les logs). */
    Long getEntityId(E entity);

    /**
     * Vérifie que l'entité peut être payée (statut compatible, montant positif...).
     * Doit lever BadRequestException si ce n'est pas le cas.
     */
    void validatePayable(E entity);

    /** Montant réellement dû, calculé côté serveur — jamais fourni par le client. */
    BigDecimal getPayableAmount(E entity);

    /** Description humaine envoyée à PayPal pour l'écran de paiement. */
    String buildDescription(E entity);

    /** Préfixe utilisé pour reference_id / custom_id PayPal (ex: "circuit", "hebergement"). */
    String referencePrefix();

    /** Récupère le paiement existant pour cette entité, ou en crée un nouveau à l'état A_PAYER. */
    P getOrCreatePayment(E entity, BigDecimal amount);

    /** Persiste le paiement mis à jour. */
    void savePayment(P payment);

    /** Construit la réponse "état à jour de l'entité" à renvoyer après capture (le DTO métier). */
    Object getUpdatedEntityView(E entity);

    default void requireAmountPositive(BigDecimal amount, String message) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(message);
        }
    }
}
