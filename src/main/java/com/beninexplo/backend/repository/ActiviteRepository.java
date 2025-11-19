package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Activite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActiviteRepository extends JpaRepository<Activite, Long> {
}
