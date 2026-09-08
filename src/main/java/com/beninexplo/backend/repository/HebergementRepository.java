package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Hebergement;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HebergementRepository extends JpaRepository<Hebergement, Long> {

    // Verrou pessimiste : force les créations/modifications concurrentes de réservations
    // sur le même hébergement à s'exécuter en série, pour éviter le double-booking.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select h from Hebergement h where h.idHebergement = :id")
    Optional<Hebergement> findByIdForUpdate(Long id);
}
