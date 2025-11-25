package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    // Récupérer uniquement les véhicules disponibles
    List<Vehicule> findByDisponibleTrue();

    // Recherche par marque
    List<Vehicule> findByMarqueContainingIgnoreCase(String marque);
}
