package com.beninexplo.backend;

import com.beninexplo.backend.dto.LoginRequestDTO;
import com.beninexplo.backend.dto.UpdateProfilRequestDTO;
import com.beninexplo.backend.dto.UtilisateurCreateDTO;
import com.beninexplo.backend.dto.UtilisateurDTO;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ConflictException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.UtilisateurRepository;
import com.beninexplo.backend.security.jwt.JwtUtil;
import com.beninexplo.backend.service.UtilisateurService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Couvre le coeur de l'authentification (inscription, login, JWT, gestion des roles), jamais
 * teste jusqu'ici malgre son importance critique pour la securite du site.
 */
@SpringBootTest
@Transactional
class UtilisateurServiceTests {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private UtilisateurCreateDTO createDto(String email) {
        UtilisateurCreateDTO dto = new UtilisateurCreateDTO();
        dto.setNom("Doe");
        dto.setPrenom("Jane");
        dto.setEmail(email);
        dto.setTelephone("+22900000000");
        dto.setMotDePasse("SuperSecret123");
        return dto;
    }

    @Test
    void createUserHashesPasswordAndNeverStoresItInClear() {
        utilisateurService.createUser(createDto("hash.test@example.com"));

        Utilisateur stored = utilisateurRepository.findByEmail("hash.test@example.com").orElseThrow();

        assertNotEquals("SuperSecret123", stored.getMotDePasse(),
                "Le mot de passe ne doit jamais etre stocke en clair");
        assertTrue(stored.getMotDePasse().length() > 20, "Un hash BCrypt fait plus de 20 caracteres");
    }

    @Test
    void createUserRejectsDuplicateEmail() {
        utilisateurService.createUser(createDto("duplicate@example.com"));

        assertThrows(ConflictException.class,
                () -> utilisateurService.createUser(createDto("duplicate@example.com")));
    }

    @Test
    void newUserGetsDefaultRoleUser() {
        UtilisateurDTO created = utilisateurService.createUser(createDto("default.role@example.com"));

        assertEquals("USER", created.getRole());
    }

    @Test
    void loginWithCorrectPasswordReturnsValidJwtWithMatchingEmailAndRole() {
        utilisateurService.createUser(createDto("login.ok@example.com"));

        var response = utilisateurService.login(new LoginRequestDTO("login.ok@example.com", "SuperSecret123"));

        assertTrue(jwtUtil.validateToken(response.getToken()));
        assertEquals("login.ok@example.com", jwtUtil.extractEmail(response.getToken()));
        assertEquals("USER", jwtUtil.extractRole(response.getToken()));
    }

    @Test
    void loginWithWrongPasswordIsRejected() {
        utilisateurService.createUser(createDto("login.badpwd@example.com"));

        assertThrows(BadRequestException.class,
                () -> utilisateurService.login(new LoginRequestDTO("login.badpwd@example.com", "WrongPassword")));
    }

    @Test
    void loginWithUnknownEmailIsRejected() {
        assertThrows(ResourceNotFoundException.class,
                () -> utilisateurService.login(new LoginRequestDTO("nobody@example.com", "whatever")));
    }

    @Test
    void createParticipantAutoUpgradesExistingUserRole() {
        utilisateurService.createUser(createDto("upgrade.participant@example.com"));

        UtilisateurDTO upgraded = utilisateurService.createParticipantAuto("upgrade.participant@example.com");

        assertEquals("PARTICIPANT", upgraded.getRole());
        assertEquals(1, utilisateurRepository.findAll().stream()
                .filter(u -> u.getEmail().equals("upgrade.participant@example.com")).count(),
                "Aucun compte duplique ne doit etre cree pour un email deja existant");
    }

    @Test
    void createParticipantAutoCreatesNewAccountWithRandomPasswordWhenUnknown() {
        UtilisateurDTO created = utilisateurService.createParticipantAuto("new.participant@example.com");

        assertEquals("PARTICIPANT", created.getRole());
        Utilisateur stored = utilisateurRepository.findByEmail("new.participant@example.com").orElseThrow();
        assertNotEquals(null, stored.getMotDePasse());
        assertTrue(stored.getMotDePasse().length() > 20);
    }

    @Test
    void updateCurrentUserProfileIgnoresBlankFieldsAndKeepsExistingValues() {
        utilisateurService.createUser(createDto("update.profile@example.com"));

        UpdateProfilRequestDTO update = new UpdateProfilRequestDTO();
        update.setNom("NouveauNom");
        update.setPrenom("");   // vide : ne doit pas ecraser le prenom existant
        update.setTelephone(null); // null : ne doit pas ecraser le telephone existant

        UtilisateurDTO updated = utilisateurService.updateCurrentUserProfile("update.profile@example.com", update);

        assertEquals("NouveauNom", updated.getNom());
        assertEquals("Jane", updated.getPrenom(), "Un prenom vide dans la requete ne doit pas ecraser l'existant");
        assertEquals("+22900000000", updated.getTelephone(), "Un telephone null dans la requete ne doit pas ecraser l'existant");
    }

    @Test
    void deleteUserRemovesTheAccount() {
        UtilisateurDTO created = utilisateurService.createUser(createDto("to.delete@example.com"));

        utilisateurService.deleteUser(created.getId());

        assertThrows(ResourceNotFoundException.class, () -> utilisateurService.getUserById(created.getId()));
    }

    @Test
    void deleteUnknownUserIsRejected() {
        assertThrows(ResourceNotFoundException.class, () -> utilisateurService.deleteUser(999999L));
    }
}
