package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Activite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActiviteRepository extends JpaRepository<Activite, Long> {
    // Filtrer les activités par zone via la relation Ville -> Zone
    List<Activite> findByVille_Zone_IdZone(Long zoneId);
    
    // Filtrer les activités par ville directement
    List<Activite> findByVille_IdVille(Long villeId);
}
