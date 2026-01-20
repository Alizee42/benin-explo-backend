package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.ReservationHebergement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationHebergementRepository extends JpaRepository<ReservationHebergement, Long> {

    List<ReservationHebergement> findByHebergementIdHebergement(Long hebergementId);

    List<ReservationHebergement> findByStatut(String statut);

    List<ReservationHebergement> findByDateArriveeBetween(LocalDate startDate, LocalDate endDate);

    List<ReservationHebergement> findByEmailClient(String emailClient);
}