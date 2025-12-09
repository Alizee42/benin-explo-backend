package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Ville;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VilleRepository extends JpaRepository<Ville, Long> {

    List<Ville> findByZoneIdZone(Long zoneId);

    List<Ville> findAllByOrderByNomAsc();

    java.util.Optional<Ville> findByNomIgnoreCase(String nom);
}