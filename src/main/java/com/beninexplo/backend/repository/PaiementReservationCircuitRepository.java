package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.PaiementReservationCircuit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaiementReservationCircuitRepository extends JpaRepository<PaiementReservationCircuit, Long> {

    Optional<PaiementReservationCircuit> findByReservationIdReservation(Long reservationId);
}
