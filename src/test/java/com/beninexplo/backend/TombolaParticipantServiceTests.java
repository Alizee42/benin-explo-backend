package com.beninexplo.backend;

import com.beninexplo.backend.dto.TombolaParticipantDTO;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.ConflictException;
import com.beninexplo.backend.repository.UtilisateurRepository;
import com.beninexplo.backend.service.TombolaParticipantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TombolaParticipantService gere l'inscription a la tombola (systeme actuellement branche,
 * meme si le frontend ne l'appelle pas encore). Le garde-fou anti double-inscription est la
 * regle la plus importante : si elle etait cassee, l'equite de la tombola serait compromise
 * (un meme participant pourrait multiplier ses chances). Jamais teste jusqu'ici.
 */
@SpringBootTest
@Transactional
class TombolaParticipantServiceTests {

    @Autowired
    private TombolaParticipantService tombolaParticipantService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Test
    void inscribingAnUnknownEmailCreatesAParticipantAccount() {
        TombolaParticipantDTO result = tombolaParticipantService.inscrireParticipant(
                "nouveau.participant@example.com", "Doe", "Jane");

        assertEquals("nouveau.participant@example.com", result.getEmail());
        Utilisateur created = utilisateurRepository.findByEmail("nouveau.participant@example.com").orElseThrow();
        assertEquals("PARTICIPANT", created.getRole());
    }

    @Test
    void inscribingAnExistingUserUpgradesTheirRoleToParticipant() {
        Utilisateur existing = new Utilisateur();
        existing.setNom("Doe");
        existing.setPrenom("John");
        existing.setEmail("existing.user@example.com");
        existing.setTelephone("+22900000000");
        existing.setMotDePasse("hash");
        existing.setRole("USER");
        utilisateurRepository.save(existing);

        tombolaParticipantService.inscrireParticipant("existing.user@example.com", "Doe", "John");

        Utilisateur upgraded = utilisateurRepository.findByEmail("existing.user@example.com").orElseThrow();
        assertEquals("PARTICIPANT", upgraded.getRole());
    }

    @Test
    void sameUserCannotRegisterTwiceToTheTombola() {
        tombolaParticipantService.inscrireParticipant("double.inscription@example.com", "Doe", "Jane");

        assertThrows(ConflictException.class, () ->
                tombolaParticipantService.inscrireParticipant("double.inscription@example.com", "Doe", "Jane"));
    }

    @Test
    void missingNomAndPrenomFallBackToDefaultValuesForNewAccount() {
        TombolaParticipantDTO result = tombolaParticipantService.inscrireParticipant(
                "sans.nom@example.com", null, null);

        assertEquals("Participant", result.getNom());
        assertEquals("Tombola", result.getPrenom());
    }
}
