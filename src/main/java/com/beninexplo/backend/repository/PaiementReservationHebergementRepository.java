package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.PaiementReservationHebergement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaiementReservationHebergementRepository extends JpaRepository<PaiementReservationHebergement, Long> {

    @EntityGraph(attributePaths = {"reservationHebergement", "reservationHebergement.hebergement"})
    Optional<PaiementReservationHebergement> findByReservationHebergementIdReservation(Long reservationId);

    Optional<PaiementReservationHebergement> findByPaypalOrderId(String paypalOrderId);
}
