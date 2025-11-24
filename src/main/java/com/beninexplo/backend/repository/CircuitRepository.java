package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Circuit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CircuitRepository extends JpaRepository<Circuit, Long> {

    // Obtenir uniquement les circuits actifs (pour le site public)
    List<Circuit> findByActifTrue();

    // Obtenir tous les circuits d’une zone donnée (utilise la propriété 'idZone' de l'entité Zone)
    List<Circuit> findByZone_IdZone(Long zoneId);

    // Recherche texte (optionnel mais utile)
    List<Circuit> findByNomContainingIgnoreCase(String nom);
}
