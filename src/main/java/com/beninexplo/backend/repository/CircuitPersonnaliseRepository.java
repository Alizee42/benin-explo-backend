package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.CircuitPersonnalise;
import com.beninexplo.backend.entity.CircuitPersonnalise.StatutDemande;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    // Devis acceptes, non payes, dont la relance (7j) n'a pas encore ete envoyee, acceptes avant
    // le seuil donne. Utilise par le job planifie de relance de paiement. LEFT JOIN explicite
    // (plutot que "cp.paiement is null or cp.paiement.statut <> 'PAYE'") : le OR direct sur une
    // relation potentiellement absente retombe sur UNKNOWN (ni vrai ni faux) en SQL standard,
    // excluant a tort les demandes sans paiement du tout.
    @Query("""
            select cp
            from CircuitPersonnalise cp
            left join cp.paiement p
            where cp.statut = :statut
              and cp.dateTraitement <= :seuil
              and cp.dateRappelPaiementEnvoye is null
              and (p is null or p.statut <> 'PAYE')
            """)
    List<CircuitPersonnalise> findAccepteNonPayeSansRappelAvant(@Param("statut") StatutDemande statut, @Param("seuil") LocalDate seuil);

    // Devis acceptes, non payes, acceptes avant le seuil d'expiration (14j). Utilise par le job
    // planifie d'expiration automatique. Meme raison de LEFT JOIN explicite que ci-dessus.
    @Query("""
            select cp
            from CircuitPersonnalise cp
            left join cp.paiement p
            where cp.statut = :statut
              and cp.dateTraitement <= :seuil
              and (p is null or p.statut <> 'PAYE')
            """)
    List<CircuitPersonnalise> findAccepteNonPayeAvant(@Param("statut") StatutDemande statut, @Param("seuil") LocalDate seuil);
}
