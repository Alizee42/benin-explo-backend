package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Circuit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CircuitRepository extends JpaRepository<Circuit, Long> {

    // Obtenir uniquement les circuits actifs (pour la partie publique)
    List<Circuit> findByActifTrue();

    // Obtenir tous les circuits d’une zone donnée
    // 'idZone' doit correspondre au champ de ton entité Zone (getIdZone)
    List<Circuit> findByZone_IdZone(Long zoneId);

    // Recherche texte dans le nom du circuit (utile pour les filtres admin)
    List<Circuit> findByNomContainingIgnoreCase(String nom);
}
