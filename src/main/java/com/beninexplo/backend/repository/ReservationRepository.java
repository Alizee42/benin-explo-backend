package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Reservation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = {"circuit", "utilisateur", "paiement"})
    List<Reservation> findAll();

    @EntityGraph(attributePaths = {"circuit", "utilisateur", "paiement"})
    List<Reservation> findByUtilisateurIdOrderByDateReservationDesc(Long utilisateurId);

    @EntityGraph(attributePaths = {"circuit", "utilisateur", "paiement"})
    Optional<Reservation> findByIdReservationAndUtilisateurId(Long idReservation, Long utilisateurId);
}
