package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.UpdateRoleRequestDTO;
import com.beninexplo.backend.dto.UtilisateurDTO;
import com.beninexplo.backend.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public List<UtilisateurDTO> getAllUsers() {
        return utilisateurService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UtilisateurDTO getById(@PathVariable Long id) {
        return utilisateurService.getUserById(id);
    }

    @PostMapping("/{id}/add-participant")
    public UtilisateurDTO addParticipantRole(@PathVariable Long id) {
        return utilisateurService.assignParticipantRole(id);
    }

    @PatchMapping("/{id}/role")
    public UtilisateurDTO updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequestDTO dto, Principal principal) {
        return utilisateurService.updateUserRole(id, dto.getRole(), principal.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        utilisateurService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
