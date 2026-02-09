package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Circuit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CircuitRepository extends JpaRepository<Circuit, Long> {

    // Obtenir uniquement les circuits actifs (pour la partie publique)
    List<Circuit> findByActifTrue();

    // Obtenir tous les circuits d'une zone donnée via ville
    // Circuit -> Ville -> Zone
    List<Circuit> findByVille_Zone_IdZone(Long zoneId);

    // Obtenir tous les circuits d'une ville donnée
    List<Circuit> findByVille_IdVille(Long villeId);

    // Recherche texte dans le nom du circuit (utile pour les filtres admin)
    List<Circuit> findByNomContainingIgnoreCase(String nom);
}
