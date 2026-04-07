package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.ReservationHebergement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationHebergementRepository extends JpaRepository<ReservationHebergement, Long> {

    @Override
    @EntityGraph(attributePaths = {"hebergement", "utilisateur", "paiement"})
    List<ReservationHebergement> findAll();

    @Override
    @EntityGraph(attributePaths = {"hebergement", "utilisateur", "paiement"})
    java.util.Optional<ReservationHebergement> findById(Long id);

    @EntityGraph(attributePaths = {"hebergement", "utilisateur", "paiement"})
    List<ReservationHebergement> findByHebergementIdHebergement(Long hebergementId);

    @EntityGraph(attributePaths = {"hebergement", "utilisateur", "paiement"})
    List<ReservationHebergement> findByStatut(String statut);

    @EntityGraph(attributePaths = {"hebergement", "utilisateur", "paiement"})
    List<ReservationHebergement> findByDateArriveeBetween(LocalDate startDate, LocalDate endDate);

    @EntityGraph(attributePaths = {"hebergement", "utilisateur", "paiement"})
    List<ReservationHebergement> findByEmailClient(String emailClient);

    @EntityGraph(attributePaths = {"hebergement", "utilisateur", "paiement"})
    List<ReservationHebergement> findByUtilisateurIdOrderByDateCreationDesc(Long utilisateurId);

    @EntityGraph(attributePaths = {"hebergement", "utilisateur", "paiement"})
    java.util.Optional<ReservationHebergement> findByIdReservationAndUtilisateurId(Long idReservation, Long utilisateurId);
}
