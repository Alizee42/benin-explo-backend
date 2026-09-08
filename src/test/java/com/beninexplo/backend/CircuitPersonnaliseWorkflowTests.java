package com.beninexplo.backend;

import com.beninexplo.backend.dto.CircuitPersonnaliseDTO;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.CircuitPersonnaliseRepository;
import com.beninexplo.backend.repository.UtilisateurRepository;
import com.beninexplo.backend.service.CircuitPersonnaliseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Couvre le workflow admin de traitement d'un devis personnalise (updateStatut) et le controle
 * d'acces "mes demandes" (getOwnedDemande, via getMineById) : deux zones de logique metier et
 * de securite jamais testees jusqu'ici.
 */
@SpringBootTest
@Transactional
class CircuitPersonnaliseWorkflowTests {

    @Autowired
    private CircuitPersonnaliseService circuitPersonnaliseService;

    @Autowired
    private CircuitPersonnaliseRepository circuitPersonnaliseRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @AfterEach
    void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    private Utilisateur createUser(String email) {
        return utilisateurRepository.findByEmail(email).orElseGet(() -> {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setNom("Test");
            utilisateur.setPrenom("Client");
            utilisateur.setEmail(email);
            utilisateur.setTelephone("+22900000000");
            utilisateur.setMotDePasse("hash");
            utilisateur.setRole("USER");
            return utilisateurRepository.save(utilisateur);
        });
    }

    private void authenticateAs(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList()));
    }

    private CircuitPersonnaliseDTO createDemande(String email, BigDecimal prixEstimeAttendu) {
        createUser(email);
        authenticateAs(email);

        CircuitPersonnaliseDTO dto = new CircuitPersonnaliseDTO();
        dto.setNomClient("Doe");
        dto.setPrenomClient("Jane");
        dto.setEmailClient(email);
        dto.setTelephoneClient("+22900000000");
        dto.setNombreJours(3);
        dto.setNombrePersonnes(2);
        dto.setDateVoyageSouhaitee(LocalDate.now().plusMonths(1));
        CircuitPersonnaliseDTO created = circuitPersonnaliseService.create(dto);
        assertEquals(0, (prixEstimeAttendu == null ? BigDecimal.ZERO : prixEstimeAttendu)
                .compareTo(created.getPrixEstime()));
        return created;
    }

    @Test
    void acceptingWithoutExplicitPrixFinalFallsBackToPrixEstime() {
        // prixEstime est a 0 (aucune option tarifee) : on force un prixEstime positif en base
        // pour observer le fallback, sans dependre du detail du calcul de prix.
        CircuitPersonnaliseDTO created = createDemande("workflow.fallback@example.com", null);
        var entity = circuitPersonnaliseRepository.findById(created.getId()).orElseThrow();
        entity.setPrixEstime(BigDecimal.valueOf(250));
        circuitPersonnaliseRepository.save(entity);

        CircuitPersonnaliseDTO accepted = circuitPersonnaliseService.updateStatut(
                created.getId(), "ACCEPTE", null, "Devis valide", null);

        assertEquals("ACCEPTE", accepted.getStatut());
        assertEquals(0, BigDecimal.valueOf(250).compareTo(accepted.getPrixFinal()));
    }

    @Test
    void acceptingWithoutAnyPositivePriceIsRejected() {
        CircuitPersonnaliseDTO created = createDemande("workflow.noprix@example.com", null);
        // prixEstime reste a 0 (aucune option tarifee), aucun prixFinal fourni.

        assertThrows(BadRequestException.class, () ->
                circuitPersonnaliseService.updateStatut(created.getId(), "ACCEPTE", null, null, null));
    }

    @Test
    void acceptingClearsAnyPreviousMotifRefus() {
        CircuitPersonnaliseDTO created = createDemande("workflow.clear-refus@example.com", null);

        circuitPersonnaliseService.updateStatut(created.getId(), "REFUSE", null, null, "Budget insuffisant");
        CircuitPersonnaliseDTO accepted = circuitPersonnaliseService.updateStatut(
                created.getId(), "ACCEPTE", BigDecimal.valueOf(300), "Finalement valide", null);

        assertEquals("ACCEPTE", accepted.getStatut());
        assertNull(accepted.getMotifRefus(), "Le motif de refus precedent doit etre efface lors de l'acceptation");
    }

    @Test
    void refusingStoresMotifRefus() {
        CircuitPersonnaliseDTO created = createDemande("workflow.refus@example.com", null);

        CircuitPersonnaliseDTO refused = circuitPersonnaliseService.updateStatut(
                created.getId(), "REFUSE", null, null, "Dates indisponibles");

        assertEquals("REFUSE", refused.getStatut());
        assertEquals("Dates indisponibles", refused.getMotifRefus());
    }

    @Test
    void explicitPrixFinalTakesPrecedenceOverPrixEstime() {
        CircuitPersonnaliseDTO created = createDemande("workflow.explicit-prix@example.com", null);
        var entity = circuitPersonnaliseRepository.findById(created.getId()).orElseThrow();
        entity.setPrixEstime(BigDecimal.valueOf(250));
        circuitPersonnaliseRepository.save(entity);

        CircuitPersonnaliseDTO accepted = circuitPersonnaliseService.updateStatut(
                created.getId(), "ACCEPTE", BigDecimal.valueOf(400), null, null);

        assertEquals(0, BigDecimal.valueOf(400).compareTo(accepted.getPrixFinal()),
                "Le prix final fourni explicitement doit primer sur le prix estime");
    }

    @Test
    void ownerCanReadTheirOwnDemande() {
        CircuitPersonnaliseDTO created = createDemande("workflow.owner@example.com", null);

        CircuitPersonnaliseDTO fetched = circuitPersonnaliseService.getMineById(created.getId());

        assertEquals(created.getId(), fetched.getId());
    }

    @Test
    void anotherUserCannotReadSomeoneElsesDemande() {
        CircuitPersonnaliseDTO created = createDemande("workflow.victim@example.com", null);

        createUser("workflow.attacker@example.com");
        authenticateAs("workflow.attacker@example.com");

        assertThrows(ResourceNotFoundException.class, () -> circuitPersonnaliseService.getMineById(created.getId()));
    }

    @Test
    void deleteRemovesTheDemande() {
        CircuitPersonnaliseDTO created = createDemande("workflow.delete@example.com", null);

        circuitPersonnaliseService.delete(created.getId());

        assertThrows(ResourceNotFoundException.class, () -> circuitPersonnaliseService.getById(created.getId()));
    }
}
