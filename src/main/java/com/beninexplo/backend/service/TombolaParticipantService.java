package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.TombolaParticipantDTO;
import com.beninexplo.backend.entity.TombolaParticipant;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.repository.TombolaParticipantRepository;
import com.beninexplo.backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TombolaParticipantService {

    @Autowired
    private TombolaParticipantRepository tombolaParticipantRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /* ----------------------------------------------------
       🟦 INSCRIPTION TOMBOLA
       - crée un utilisateur si inexistant
       - ajoute rôle PARTICIPANT si nécessaire
       - enregistre participation
    ---------------------------------------------------- */
    public TombolaParticipantDTO inscrireParticipant(String email, String nom, String prenom) {

        // Vérifier si utilisateur existe déjà
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElse(null);

        if (utilisateur == null) {
            // Création auto du compte participant
            utilisateur = new Utilisateur();
            utilisateur.setNom(nom != null ? nom : "Participant");
            utilisateur.setPrenom(prenom != null ? prenom : "Tombola");
            utilisateur.setEmail(email);
            utilisateur.setMotDePasse(encoder.encode("participant123"));
            utilisateur.setRole("PARTICIPANT");
            utilisateur.setDateCreation(LocalDateTime.now());

            utilisateurRepository.save(utilisateur);

        } else {
            // Ajouter rôle PARTICIPANT si pas déjà
            if (!"PARTICIPANT".equals(utilisateur.getRole())) {
                utilisateur.setRole("PARTICIPANT");
                utilisateurRepository.save(utilisateur);
            }
        }

        // Vérifier si déjà inscrit à la tombola
        if (tombolaParticipantRepository.findByUtilisateurId(utilisateur.getId()).isPresent()) {
            throw new RuntimeException("Cet utilisateur a déjà participé à la tombola.");
        }

        // Création participation
        TombolaParticipant participant = new TombolaParticipant();
        participant.setUtilisateur(utilisateur);
        participant.setEmail(email);
        participant.setNom(utilisateur.getNom());
        participant.setPrenom(utilisateur.getPrenom());
        participant.setDateInscription(LocalDateTime.now());

        tombolaParticipantRepository.save(participant);

        return toDTO(participant);
    }

    /* ----------------------------------------------------
       🟩 CONVERSION ENTITY → DTO
    ---------------------------------------------------- */
    public TombolaParticipantDTO toDTO(TombolaParticipant t) {
        return new TombolaParticipantDTO(
                t.getId(),
                t.getUtilisateur().getId(),
                t.getEmail(),
                t.getNom(),
                t.getPrenom(),
                t.getDateInscription().toString()
        );
    }
}
