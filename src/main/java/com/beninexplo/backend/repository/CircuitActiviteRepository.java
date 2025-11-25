package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.CircuitActivite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CircuitActiviteRepository extends JpaRepository<CircuitActivite, Long> {

    // Obtenir toutes les activités d’un circuit triées par ordre
    List<CircuitActivite> findByCircuit_IdCircuitOrderByOrdreAsc(Long circuitId);
}
