package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.*;
import com.beninexplo.backend.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
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
        return utilisateurService.login(dto);
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
