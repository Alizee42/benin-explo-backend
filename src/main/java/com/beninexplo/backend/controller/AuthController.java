package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.EmailRequestDTO;
import com.beninexplo.backend.dto.LoginRequestDTO;
import com.beninexplo.backend.dto.LoginResponseDTO;
import com.beninexplo.backend.dto.UpdateProfilRequestDTO;
import com.beninexplo.backend.dto.UtilisateurCreateDTO;
import com.beninexplo.backend.dto.UtilisateurDTO;
import com.beninexplo.backend.service.UtilisateurService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UtilisateurService utilisateurService;

    public AuthController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/register")
    public UtilisateurDTO register(@Valid @RequestBody UtilisateurCreateDTO dto) {
        return utilisateurService.createUser(dto);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
        log.info("Requete de connexion recue pour: {}", dto.getEmail());
        LoginResponseDTO response = utilisateurService.login(dto);
        log.info("Connexion reussie pour: {}", dto.getEmail());
        return response;
    }

    @PatchMapping("/me")
    public UtilisateurDTO updateProfil(@Valid @RequestBody UpdateProfilRequestDTO dto, Principal principal) {
        return utilisateurService.updateCurrentUserProfile(principal.getName(), dto);
    }

    @PostMapping("/tombola")
    public UtilisateurDTO inscriptionTombola(@Valid @RequestBody EmailRequestDTO dto) {
        return utilisateurService.createParticipantAuto(dto.getEmail());
    }
}
