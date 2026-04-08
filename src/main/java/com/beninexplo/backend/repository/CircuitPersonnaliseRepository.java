package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.CircuitPersonnalise;
import com.beninexplo.backend.entity.CircuitPersonnalise.StatutDemande;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CircuitPersonnaliseRepository extends JpaRepository<CircuitPersonnalise, Long> {

    @Override
    @EntityGraph(attributePaths = {"hebergement", "utilisateur", "paiement", "jours", "jours.zone", "jours.ville"})
    Optional<CircuitPersonnalise> findById(Long id);

    List<CircuitPersonnalise> findByStatut(StatutDemande statut);

    List<CircuitPersonnalise> findByEmailClient(String email);

    @EntityGraph(attributePaths = {"hebergement", "utilisateur", "paiement", "jours", "jours.zone", "jours.ville"})
    List<CircuitPersonnalise> findByStatutOrderByDateCreationDesc(StatutDemande statut);

    @EntityGraph(attributePaths = {"hebergement", "utilisateur", "paiement", "jours", "jours.zone", "jours.ville"})
    List<CircuitPersonnalise> findAllByOrderByDateCreationDesc();

    @EntityGraph(attributePaths = {"hebergement", "utilisateur", "paiement", "jours", "jours.zone", "jours.ville"})
    List<CircuitPersonnalise> findByUtilisateurIdOrEmailClientIgnoreCaseOrderByDateCreationDesc(Long utilisateurId, String emailClient);
}
