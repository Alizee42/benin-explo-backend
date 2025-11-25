package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.UtilisateurDTO;
import com.beninexplo.backend.service.UtilisateurService;
import com.beninexplo.backend.repository.UtilisateurRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/utilisateurs")
@CrossOrigin("*")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /* ----------------------------------------------------
       🟦 1) LISTE DE TOUS LES UTILISATEURS (ADMIN ONLY)
    ---------------------------------------------------- */
    @GetMapping
    public List<UtilisateurDTO> getAllUsers() {
        return utilisateurRepository.findAll()
                .stream()
                .map(utilisateurService::toDTO)
                .collect(Collectors.toList());
    }

    /* ----------------------------------------------------
       🟩 2) RÉCUPÉRATION PAR ID
    ---------------------------------------------------- */
    @GetMapping("/{id}")
    public UtilisateurDTO getById(@PathVariable Long id) {
        return utilisateurService.getUserById(id);
    }

    /* ----------------------------------------------------
       🟧 3) AJOUT DU RÔLE PARTICIPANT (ADMIN)
    ---------------------------------------------------- */
    @PostMapping("/{id}/add-participant")
    public UtilisateurDTO addParticipantRole(@PathVariable Long id) {
        return utilisateurService.assignParticipantRole(id);
    }

    /* ----------------------------------------------------
       🟥 4) SUPPRESSION D’UN UTILISATEUR (ADMIN)
    ---------------------------------------------------- */
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        utilisateurRepository.deleteById(id);
        return "Utilisateur supprimé avec succès.";
    }
}
