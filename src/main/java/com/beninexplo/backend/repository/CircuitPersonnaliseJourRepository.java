package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.CircuitPersonnaliseJour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CircuitPersonnaliseJourRepository extends JpaRepository<CircuitPersonnaliseJour, Long> {
    
    List<CircuitPersonnaliseJour> findByCircuitPersonnaliseIdOrderByNumeroJourAsc(Long circuitPersonnaliseId);
}
