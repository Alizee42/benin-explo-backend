package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.CircuitPersonnalise;
import com.beninexplo.backend.entity.CircuitPersonnalise.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CircuitPersonnaliseRepository extends JpaRepository<CircuitPersonnalise, Long> {
    
    List<CircuitPersonnalise> findByStatut(StatutDemande statut);
    
    List<CircuitPersonnalise> findByEmailClient(String email);
    
    List<CircuitPersonnalise> findByStatutOrderByDateCreationDesc(StatutDemande statut);
    
    List<CircuitPersonnalise> findAllByOrderByDateCreationDesc();
}
