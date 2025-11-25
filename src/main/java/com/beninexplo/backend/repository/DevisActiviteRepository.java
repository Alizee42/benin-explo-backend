package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.DevisActivite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DevisActiviteRepository extends JpaRepository<DevisActivite, Long> {

    List<DevisActivite> findByDevis_IdDevis(Long devisId);
}
