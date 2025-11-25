package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Recherche par email (login, inscription, vérification)
    Optional<Utilisateur> findByEmail(String email);

    // Vérifie si un email est déjà utilisé
    boolean existsByEmail(String email);
}