package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.LoginRequestDTO;
import com.beninexplo.backend.dto.LoginResponseDTO;
import com.beninexplo.backend.dto.UpdateProfilRequestDTO;
import com.beninexplo.backend.dto.UtilisateurCreateDTO;
import com.beninexplo.backend.dto.UtilisateurDTO;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ConflictException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.UtilisateurRepository;
import com.beninexplo.backend.security.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class UtilisateurService {

    private static final Logger log = LoggerFactory.getLogger(UtilisateurService.class);

    private final UtilisateurRepository utilisateurRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurNotificationService utilisateurNotificationService;

    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                              JwtUtil jwtUtil,
                              PasswordEncoder passwordEncoder,
                              UtilisateurNotificationService utilisateurNotificationService) {
        this.utilisateurRepository = utilisateurRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.utilisateurNotificationService = utilisateurNotificationService;
    }

    public UtilisateurDTO createUser(UtilisateurCreateDTO dto) {
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Cet email est deja utilise.");
        }

        Utilisateur user = new Utilisateur();
        user.setNom(dto.getNom());
        user.setPrenom(dto.getPrenom());
        user.setEmail(dto.getEmail());
        user.setTelephone(dto.getTelephone());
        user.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        user.setRole("USER");
        user.setDateCreation(LocalDateTime.now());

        utilisateurRepository.save(user);
        utilisateurNotificationService.sendWelcomeEmail(user);
        return toDTO(user);
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        log.info("Tentative de connexion pour email: {}", dto.getEmail());

        Utilisateur user = utilisateurRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));

        if (!passwordEncoder.matches(dto.getMotDePasse(), user.getMotDePasse())) {
            log.warn("Echec d'authentification pour: {}", dto.getEmail());
            throw new BadRequestException("Mot de passe incorrect.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        log.info("Connexion reussie pour: {}", user.getEmail());

        return new LoginResponseDTO(
                token,
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getTelephone(),
                user.getRole()
        );
    }

    public UtilisateurDTO createParticipantAuto(String email) {
        Optional<Utilisateur> existing = utilisateurRepository.findByEmail(email);

        if (existing.isPresent()) {
            Utilisateur user = existing.get();
            if (!user.getRole().contains("PARTICIPANT")) {
                user.setRole("PARTICIPANT");
                utilisateurRepository.save(user);
            }
            return toDTO(user);
        }

        Utilisateur participant = new Utilisateur();
        participant.setNom("Participant");
        participant.setPrenom("Tombola");
        participant.setEmail(email);
        participant.setTelephone(null);
        byte[] randomBytes = new byte[24];
        new SecureRandom().nextBytes(randomBytes);
        String randomPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        participant.setMotDePasse(passwordEncoder.encode(randomPassword));
        participant.setRole("PARTICIPANT");
        participant.setDateCreation(LocalDateTime.now());

        utilisateurRepository.save(participant);
        return toDTO(participant);
    }

    public UtilisateurDTO assignParticipantRole(Long id) {
        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));

        user.setRole("PARTICIPANT");
        utilisateurRepository.save(user);

        return toDTO(user);
    }

    public UtilisateurDTO toDTO(Utilisateur user) {
        return new UtilisateurDTO(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getTelephone(),
                user.getRole()
        );
    }

    public UtilisateurDTO getUserById(Long id) {
        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));
        return toDTO(user);
    }

    public List<UtilisateurDTO> getAllUsers() {
        return utilisateurRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UtilisateurDTO updateCurrentUserProfile(String email, UpdateProfilRequestDTO dto) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));

        if (org.springframework.util.StringUtils.hasText(dto.getNom())) {
            user.setNom(dto.getNom().trim());
        }
        if (org.springframework.util.StringUtils.hasText(dto.getPrenom())) {
            user.setPrenom(dto.getPrenom().trim());
        }
        if (dto.getTelephone() != null) {
            user.setTelephone(dto.getTelephone().isBlank() ? null : dto.getTelephone().trim());
        }

        utilisateurRepository.save(user);
        return toDTO(user);
    }

    public void deleteUser(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur introuvable.");
        }
        utilisateurRepository.deleteById(id);
    }
}
