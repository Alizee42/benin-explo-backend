package com.beninexplo.backend;

import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.UtilisateurRepository;
import com.beninexplo.backend.service.AuthenticatedUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * AuthenticatedUserService est utilise transversalement par presque tous les services qui ont
 * besoin de savoir "qui est l'utilisateur courant" (reservations, paiements, circuit
 * personnalise...). Teste ici de facon isolee (mock du repository, sans Spring) car un bug
 * dans cette resolution d'identite impacterait silencieusement tout le reste.
 */
class AuthenticatedUserServiceTests {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private AuthenticatedUserService serviceWithRepository(UtilisateurRepository repository) {
        return new AuthenticatedUserService(repository);
    }

    private void authenticateAs(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList()));
    }

    @Test
    void getRequiredCurrentUserReturnsTheMatchingAccountWhenAuthenticated() {
        UtilisateurRepository repository = mock(UtilisateurRepository.class);
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("current.user@example.com");
        when(repository.findByEmail("current.user@example.com")).thenReturn(Optional.of(utilisateur));
        authenticateAs("current.user@example.com");

        Utilisateur result = serviceWithRepository(repository).getRequiredCurrentUser();

        assertEquals("current.user@example.com", result.getEmail());
    }

    @Test
    void getRequiredCurrentUserRejectsWhenNoAuthenticationInContext() {
        UtilisateurRepository repository = mock(UtilisateurRepository.class);
        SecurityContextHolder.clearContext();

        assertThrows(BadRequestException.class, () -> serviceWithRepository(repository).getRequiredCurrentUser());
    }

    @Test
    void getRequiredCurrentUserRejectsAnonymousUser() {
        UtilisateurRepository repository = mock(UtilisateurRepository.class);
        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken("key", "anonymousUser", Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

        assertThrows(BadRequestException.class, () -> serviceWithRepository(repository).getRequiredCurrentUser());
    }

    @Test
    void getRequiredCurrentUserFailsWhenAuthenticatedEmailHasNoMatchingAccount() {
        // Cas de desynchronisation JWT/base : le token est valide et authentifie, mais le
        // compte a ete supprime entre-temps (ou n'a jamais existe).
        UtilisateurRepository repository = mock(UtilisateurRepository.class);
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        authenticateAs("ghost.user@example.com");

        assertThrows(ResourceNotFoundException.class, () -> serviceWithRepository(repository).getRequiredCurrentUser());
    }

    @Test
    void getCurrentUserOrNullReturnsNullInsteadOfThrowingWhenNotAuthenticated() {
        UtilisateurRepository repository = mock(UtilisateurRepository.class);
        SecurityContextHolder.clearContext();

        Utilisateur result = serviceWithRepository(repository).getCurrentUserOrNull();

        assertNull(result);
    }

    @Test
    void getCurrentUserOrNullReturnsTheAccountWhenAuthenticated() {
        UtilisateurRepository repository = mock(UtilisateurRepository.class);
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("optional.user@example.com");
        when(repository.findByEmail("optional.user@example.com")).thenReturn(Optional.of(utilisateur));
        authenticateAs("optional.user@example.com");

        Utilisateur result = serviceWithRepository(repository).getCurrentUserOrNull();

        assertEquals("optional.user@example.com", result.getEmail());
    }
}
