package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.ClientDemande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientDemandeRepository extends JpaRepository<ClientDemande, Long> {
}
