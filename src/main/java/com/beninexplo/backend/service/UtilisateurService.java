package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.UtilisateurCreateDTO;
import com.beninexplo.backend.dto.UtilisateurDTO;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.repository.UtilisateurRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtilisateurService {

    private final UtilisateurRepository repo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UtilisateurService(UtilisateurRepository repo) {
        this.repo = repo;
    }

    private UtilisateurDTO toDTO(Utilisateur u) {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(u.getIdUtilisateur());
        dto.setNom(u.getNom());
        dto.setPrenom(u.getPrenom());
        dto.setEmail(u.getEmail());
        dto.setTelephone(u.getTelephone());
        dto.setRole(u.getRole());
        dto.setActif(u.isActif());
        dto.setDateCreation(u.getDateCreation().toString());
        return dto;
    }

    public List<UtilisateurDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UtilisateurDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public UtilisateurDTO create(UtilisateurCreateDTO dto) {

        // Vérifie si email déjà utilisé
        if (repo.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé.");
        }

        Utilisateur u = new Utilisateur();
        u.setNom(dto.getNom());
        u.setPrenom(dto.getPrenom());
        u.setEmail(dto.getEmail());
        u.setTelephone(dto.getTelephone());
        u.setRole(dto.getRole());

        // Hash du mot de passe
        u.setMotDePasseHash(passwordEncoder.encode(dto.getMotDePasse()));

        repo.save(u);
        return toDTO(u);
    }

    public UtilisateurDTO update(Long id, UtilisateurDTO dto) {
        Utilisateur u = repo.findById(id).orElse(null);
        if (u == null) return null;

        u.setNom(dto.getNom());
        u.setPrenom(dto.getPrenom());
        u.setEmail(dto.getEmail());
        u.setTelephone(dto.getTelephone());
        u.setRole(dto.getRole());
        u.setActif(dto.isActif());

        repo.save(u);
        return toDTO(u);
    }

    /**
     * Authentification d’un utilisateur (login simple)
     */
    public UtilisateurDTO authenticate(String email, String motDePasse) {
        return repo.findByEmail(email)
                .filter(u -> passwordEncoder.matches(motDePasse, u.getMotDePasseHash()))
                .map(this::toDTO)
                .orElse(null);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
