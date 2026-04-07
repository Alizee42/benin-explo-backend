package com.beninexplo.backend.service;

import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    private final UtilisateurRepository utilisateurRepository;

    public AuthenticatedUserService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public Utilisateur getRequiredCurrentUser() {
        String email = getCurrentUserEmail();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Un compte connecte est requis.");
        }

        String email = authentication.getName();
        if (email == null || email.trim().isEmpty() || "anonymousUser".equalsIgnoreCase(email)) {
            throw new BadRequestException("Un compte connecte est requis.");
        }

        return email.trim();
    }
}
