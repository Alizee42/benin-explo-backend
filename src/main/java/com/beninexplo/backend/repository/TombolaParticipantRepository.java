package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.TombolaParticipant;
import com.beninexplo.backend.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TombolaParticipantRepository extends JpaRepository<TombolaParticipant, Long> {

    // Retrouver les participations d’un utilisateur
    List<TombolaParticipant> findByUtilisateur(Utilisateur utilisateur);

    // Vérifier si un utilisateur a déjà participé (pour éviter les doublons)
    Optional<TombolaParticipant> findByUtilisateurId(Long utilisateurId);

    // Vérifier si un email a déjà participé
    Optional<TombolaParticipant> findByEmail(String email);
}
