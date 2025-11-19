package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.LoginRequestDTO;
import com.beninexplo.backend.dto.LoginResponseDTO;
import com.beninexplo.backend.dto.UtilisateurCreateDTO;
import com.beninexplo.backend.dto.UtilisateurDTO;
import com.beninexplo.backend.security.jwt.JwtUtil;
import com.beninexplo.backend.service.UtilisateurService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilisateurService utilisateurService;
    private final JwtUtil jwtUtil;

    public AuthController(UtilisateurService utilisateurService, JwtUtil jwtUtil) {
        this.utilisateurService = utilisateurService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Inscription d’un utilisateur (register)
     */
    @PostMapping("/register")
    public ResponseEntity<UtilisateurDTO> register(@RequestBody UtilisateurCreateDTO dto) {
        UtilisateurDTO created = utilisateurService.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Connexion (login) avec JWT
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {

        UtilisateurDTO utilisateur = utilisateurService.authenticate(
                request.getEmail(),
                request.getMotDePasse()
        );

        LoginResponseDTO response = new LoginResponseDTO();

        if (utilisateur == null) {
            response.setSuccess(false);
            response.setMessage("Email ou mot de passe incorrect.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Génération du JWT
        String token = jwtUtil.generateToken(utilisateur.getEmail());

        response.setSuccess(true);
        response.setMessage("Connexion réussie.");
        response.setUtilisateur(utilisateur);
        response.setToken(token);

        return ResponseEntity.ok(response);
    }
}
