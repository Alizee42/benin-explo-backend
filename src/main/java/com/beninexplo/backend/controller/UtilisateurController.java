package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.UtilisateurDTO;
import com.beninexplo.backend.repository.UtilisateurRepository;
import com.beninexplo.backend.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurController(UtilisateurService utilisateurService,
                                 UtilisateurRepository utilisateurRepository) {
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping
    public List<UtilisateurDTO> getAllUsers() {
        return utilisateurRepository.findAll()
                .stream()
                .map(utilisateurService::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UtilisateurDTO getById(@PathVariable Long id) {
        return utilisateurService.getUserById(id);
    }

    @PostMapping("/{id}/add-participant")
    public UtilisateurDTO addParticipantRole(@PathVariable Long id) {
        return utilisateurService.assignParticipantRole(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        utilisateurRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
