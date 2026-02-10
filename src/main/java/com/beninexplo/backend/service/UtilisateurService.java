package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.*;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.repository.UtilisateurRepository;
import com.beninexplo.backend.security.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UtilisateurService {

    private static final Logger log = LoggerFactory.getLogger(UtilisateurService.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /* ----------------------------------------------------
       🟦 1) CRÉATION UTILISATEUR (REGISTER → USER)
    ---------------------------------------------------- */
    public UtilisateurDTO createUser(UtilisateurCreateDTO dto) {

        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        Utilisateur u = new Utilisateur();
        u.setNom(dto.getNom());
        u.setPrenom(dto.getPrenom());
        u.setEmail(dto.getEmail());
        u.setTelephone(dto.getTelephone());
        u.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        u.setRole("USER");
        u.setDateCreation(LocalDateTime.now());

        utilisateurRepository.save(u);

        return toDTO(u);
    }

    /* ----------------------------------------------------
       🟩 2) LOGIN (EMAIL + MOT DE PASSE)
    ---------------------------------------------------- */
    public LoginResponseDTO login(LoginRequestDTO dto) {
        log.info("Tentative de connexion pour email: {}", dto.getEmail());

        Utilisateur user = utilisateurRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable."));

        log.debug("Utilisateur trouvé: {} avec rôle: {}", user.getEmail(), user.getRole());

        if (!passwordEncoder.matches(dto.getMotDePasse(), user.getMotDePasse())) {
            log.warn("Échec d'authentification pour: {}", dto.getEmail());
            throw new RuntimeException("Mot de passe incorrect.");
        }

        log.debug("Authentification réussie pour: {}", user.getEmail());

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        log.info("Token JWT généré pour: {}", user.getEmail());

        return new LoginResponseDTO(
                token,
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole()
        );
    }

    /* ----------------------------------------------------
       🟧 3) CRÉATION AUTOMATIQUE PARTICIPANT (TOMBOLA)
    ---------------------------------------------------- */
    public UtilisateurDTO createParticipantAuto(String email) {

        Optional<Utilisateur> existing = utilisateurRepository.findByEmail(email);

        if (existing.isPresent()) {
            Utilisateur u = existing.get();

            // Si déjà participant, rien à faire
            if (!u.getRole().contains("PARTICIPANT")) {
                u.setRole("PARTICIPANT");
                utilisateurRepository.save(u);
            }

            return toDTO(u);
        }

        // Sinon → créer un compte auto
        Utilisateur p = new Utilisateur();
        p.setNom("Participant");
        p.setPrenom("Tombola");
        p.setEmail(email);
        p.setTelephone(null);
        p.setMotDePasse(passwordEncoder.encode("participant123"));
        p.setRole("PARTICIPANT");
        p.setDateCreation(LocalDateTime.now());

        utilisateurRepository.save(p);

        return toDTO(p);
    }

    /* ----------------------------------------------------
       🟪 4) AJOUTER LE RÔLE PARTICIPANT À UN USER EXISTANT
    ---------------------------------------------------- */
    public UtilisateurDTO assignParticipantRole(Long id) {

        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable."));

        u.setRole("PARTICIPANT");
        utilisateurRepository.save(u);

        return toDTO(u);
    }

    /* ----------------------------------------------------
       🟨 5) CONVERSION ENTITY → DTO
    ---------------------------------------------------- */
    public UtilisateurDTO toDTO(Utilisateur u) {
        return new UtilisateurDTO(
                u.getId(),
                u.getNom(),
                u.getPrenom(),
                u.getEmail(),
                u.getTelephone(),
                u.getRole()
        );
    }

    /* ----------------------------------------------------
       🟩 6) RÉCUPÉRATION UTILISATEUR PAR ID
    ---------------------------------------------------- */
    public UtilisateurDTO getUserById(Long id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable."));
        return toDTO(u);
    }
}

