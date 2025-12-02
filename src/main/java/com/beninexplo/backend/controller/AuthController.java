package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.*;
import com.beninexplo.backend.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UtilisateurService utilisateurService;

    /* ----------------------------------------------------
       🟦 REGISTER → créer un compte USER
    ---------------------------------------------------- */
    @PostMapping("/register")
    public UtilisateurDTO register(@RequestBody UtilisateurCreateDTO dto) {
        return utilisateurService.createUser(dto);
    }

    /* ----------------------------------------------------
       🟩 LOGIN → renvoyer JWT + infos utilisateur
    ---------------------------------------------------- */
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO dto) {
        System.out.println("🚀 Requête LOGIN reçue pour: " + dto.getEmail());
        LoginResponseDTO response = utilisateurService.login(dto);
        System.out.println("✅ LOGIN réussi pour: " + dto.getEmail());
        return response;
    }

    /* ----------------------------------------------------
       🟧 INSCRIPTION TOMBOLA
       → crée automatiquement un compte PARTICIPANT si email inconnu
       → ou ajoute le rôle PARTICIPANT si déjà inscrit
    ---------------------------------------------------- */
    @PostMapping("/tombola")
    public UtilisateurDTO inscriptionTombola(@RequestBody LoginRequestDTO dto) {
        return utilisateurService.createParticipantAuto(dto.getEmail());
    }
}
