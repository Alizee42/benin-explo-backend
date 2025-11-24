package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Hebergement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HebergementRepository extends JpaRepository<Hebergement, Long> {
}
