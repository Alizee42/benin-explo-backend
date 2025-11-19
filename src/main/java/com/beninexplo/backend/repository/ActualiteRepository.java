package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Actualite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActualiteRepository extends JpaRepository<Actualite, Long> {
}
