package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.TarifsCircuitPersonnalise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TarifsCircuitPersonnaliseRepository extends JpaRepository<TarifsCircuitPersonnalise, Long> {

    Optional<TarifsCircuitPersonnalise> findTopByOrderByIdAsc();
}
