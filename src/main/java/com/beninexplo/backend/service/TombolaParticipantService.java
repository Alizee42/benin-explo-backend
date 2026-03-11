package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.TombolaParticipantDTO;
import com.beninexplo.backend.entity.TombolaParticipant;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.ConflictException;
import com.beninexplo.backend.repository.TombolaParticipantRepository;
import com.beninexplo.backend.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TombolaParticipantService {

    private final TombolaParticipantRepository tombolaParticipantRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public TombolaParticipantService(TombolaParticipantRepository tombolaParticipantRepository,
                                     UtilisateurRepository utilisateurRepository,
                                     PasswordEncoder passwordEncoder) {
        this.tombolaParticipantRepository = tombolaParticipantRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public TombolaParticipantDTO inscrireParticipant(String email, String nom, String prenom) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElse(null);

        if (utilisateur == null) {
            utilisateur = new Utilisateur();
            utilisateur.setNom(nom != null ? nom : "Participant");
            utilisateur.setPrenom(prenom != null ? prenom : "Tombola");
            utilisateur.setEmail(email);
            utilisateur.setMotDePasse(passwordEncoder.encode("participant123"));
            utilisateur.setRole("PARTICIPANT");
            utilisateur.setDateCreation(LocalDateTime.now());
            utilisateurRepository.save(utilisateur);
        } else if (!"PARTICIPANT".equals(utilisateur.getRole())) {
            utilisateur.setRole("PARTICIPANT");
            utilisateurRepository.save(utilisateur);
        }

        if (tombolaParticipantRepository.findByUtilisateurId(utilisateur.getId()).isPresent()) {
            throw new ConflictException("Cet utilisateur a deja participe a la tombola.");
        }

        TombolaParticipant participant = new TombolaParticipant();
        participant.setUtilisateur(utilisateur);
        participant.setEmail(email);
        participant.setNom(utilisateur.getNom());
        participant.setPrenom(utilisateur.getPrenom());
        participant.setDateInscription(LocalDateTime.now());
        tombolaParticipantRepository.save(participant);

        return toDTO(participant);
    }

    public TombolaParticipantDTO toDTO(TombolaParticipant participant) {
        return new TombolaParticipantDTO(
                participant.getId(),
                participant.getUtilisateur().getId(),
                participant.getEmail(),
                participant.getNom(),
                participant.getPrenom(),
                participant.getDateInscription().toString()
        );
    }
}
