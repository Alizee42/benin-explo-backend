package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.PaiementCircuitPersonnalise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaiementCircuitPersonnaliseRepository extends JpaRepository<PaiementCircuitPersonnalise, Long> {

    Optional<PaiementCircuitPersonnalise> findByCircuitPersonnaliseId(Long circuitPersonnaliseId);
}
