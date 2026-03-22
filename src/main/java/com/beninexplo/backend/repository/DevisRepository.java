package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Devis;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DevisRepository extends JpaRepository<Devis, Long> {

    @EntityGraph(attributePaths = {"circuit"})
    List<Devis> findAll();
}
