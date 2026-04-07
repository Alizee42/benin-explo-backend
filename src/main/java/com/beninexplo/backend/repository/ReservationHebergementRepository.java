package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.ReservationHebergement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationHebergementRepository extends JpaRepository<ReservationHebergement, Long> {

    @EntityGraph(attributePaths = {"hebergement"})
    List<ReservationHebergement> findByHebergementIdHebergement(Long hebergementId);

    @EntityGraph(attributePaths = {"hebergement"})
    List<ReservationHebergement> findByStatut(String statut);

    @EntityGraph(attributePaths = {"hebergement"})
    List<ReservationHebergement> findByDateArriveeBetween(LocalDate startDate, LocalDate endDate);

    @EntityGraph(attributePaths = {"hebergement"})
    List<ReservationHebergement> findByEmailClient(String emailClient);

    @EntityGraph(attributePaths = {"hebergement", "utilisateur"})
    List<ReservationHebergement> findByUtilisateurIdOrderByDateCreationDesc(Long utilisateurId);
}
