package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Circuit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CircuitRepository extends JpaRepository<Circuit, Long> {

    // Obtenir uniquement les circuits actifs (pour la partie publique)
    List<Circuit> findByActifTrue();

    // Variante paginee, utilisee par la liste publique (GET /api/circuits/actifs?page=&size=)
    Page<Circuit> findByActifTrue(Pageable pageable);

    // Variante paginee + filtree par zone, utilisee par la liste publique avec filtre zone
    Page<Circuit> findByActifTrueAndVille_Zone_IdZone(Long zoneId, Pageable pageable);

    // Obtenir tous les circuits d'une zone donnée via ville
    // Circuit -> Ville -> Zone
    List<Circuit> findByVille_Zone_IdZone(Long zoneId);

    // Obtenir tous les circuits d'une ville donnée
    List<Circuit> findByVille_IdVille(Long villeId);

    long countByVille_IdVille(Long villeId);

    // Recherche texte dans le nom du circuit (utile pour les filtres admin)
    List<Circuit> findByNomContainingIgnoreCase(String nom);
}
