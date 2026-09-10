package com.beninexplo.backend;

import com.beninexplo.backend.dto.CircuitDTO;
import com.beninexplo.backend.dto.CircuitPersonnaliseDTO;
import com.beninexplo.backend.entity.CircuitPersonnalise;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.CircuitPersonnaliseRepository;
import com.beninexplo.backend.repository.UtilisateurRepository;
import com.beninexplo.backend.repository.VilleRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Autowired
    private VilleRepository villeRepository;

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
    void createCircuitFromDemandeIfAbsentCreatesInactiveCircuitLinkedToDemande() {
        String email = "workflow.circuit-create@example.com";
        createUser(email);
        authenticateAs(email);

        Ville ville = villeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucune ville en fixture de test."));

        CircuitPersonnaliseDTO dto = new CircuitPersonnaliseDTO();
        dto.setNomClient("Doe");
        dto.setPrenomClient("Jane");
        dto.setEmailClient(email);
        dto.setTelephoneClient("+22900000000");
        dto.setNombreJours(2);
        dto.setNombrePersonnes(2);
        dto.setDateVoyageSouhaitee(LocalDate.now().plusMonths(1));
        CircuitPersonnaliseDTO.JourDTO jour = new CircuitPersonnaliseDTO.JourDTO();
        jour.setNumeroJour(1);
        jour.setVilleId(ville.getIdVille());
        jour.setDescriptionJour("Arrivee et decouverte");
        dto.setJours(List.of(jour));

        CircuitPersonnaliseDTO created = circuitPersonnaliseService.create(dto);
        assertEquals(1, created.getJours().size(), "Le jour avec ville doit avoir ete persiste par create()");
        circuitPersonnaliseService.updateStatut(created.getId(), "ACCEPTE", BigDecimal.valueOf(300), null, null);

        circuitPersonnaliseService.createCircuitFromDemandeIfAbsent(created.getId());

        CircuitPersonnalise reloaded = circuitPersonnaliseRepository.findById(created.getId()).orElseThrow();
        assertNotNull(reloaded.getCircuitCree(), "Le circuit catalogue doit etre cree et lie a la demande");
        assertFalse(reloaded.getCircuitCree().isActif(), "Le circuit cree ne doit pas etre visible dans le catalogue public");
        assertEquals(ville.getIdVille(), reloaded.getCircuitCree().getVille().getIdVille());
    }

    @Test
    void createCircuitFromDemandeIfAbsentIsIdempotent() {
        String email = "workflow.circuit-idempotent@example.com";
        createUser(email);
        authenticateAs(email);

        Ville ville = villeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucune ville en fixture de test."));

        CircuitPersonnaliseDTO dto = new CircuitPersonnaliseDTO();
        dto.setNomClient("Doe");
        dto.setPrenomClient("Jane");
        dto.setEmailClient(email);
        dto.setTelephoneClient("+22900000000");
        dto.setNombreJours(1);
        dto.setNombrePersonnes(1);
        dto.setDateVoyageSouhaitee(LocalDate.now().plusMonths(1));
        CircuitPersonnaliseDTO.JourDTO jour = new CircuitPersonnaliseDTO.JourDTO();
        jour.setNumeroJour(1);
        jour.setVilleId(ville.getIdVille());
        dto.setJours(List.of(jour));

        CircuitPersonnaliseDTO created = circuitPersonnaliseService.create(dto);
        circuitPersonnaliseService.updateStatut(created.getId(), "ACCEPTE", BigDecimal.valueOf(300), null, null);

        circuitPersonnaliseService.createCircuitFromDemandeIfAbsent(created.getId());
        Long firstCircuitId = circuitPersonnaliseRepository.findById(created.getId()).orElseThrow()
                .getCircuitCree().getIdCircuit();

        circuitPersonnaliseService.createCircuitFromDemandeIfAbsent(created.getId());
        Long secondCircuitId = circuitPersonnaliseRepository.findById(created.getId()).orElseThrow()
                .getCircuitCree().getIdCircuit();

        assertEquals(firstCircuitId, secondCircuitId, "Un second appel ne doit pas creer un nouveau circuit");
    }

    @Test
    void ownerCanReadTheCircuitCreeOnceGenerated() {
        String email = "workflow.circuit-cree-owner@example.com";
        createUser(email);
        authenticateAs(email);

        Ville ville = villeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucune ville en fixture de test."));

        CircuitPersonnaliseDTO dto = new CircuitPersonnaliseDTO();
        dto.setNomClient("Doe");
        dto.setPrenomClient("Jane");
        dto.setEmailClient(email);
        dto.setTelephoneClient("+22900000000");
        dto.setNombreJours(1);
        dto.setNombrePersonnes(1);
        dto.setDateVoyageSouhaitee(LocalDate.now().plusMonths(1));
        CircuitPersonnaliseDTO.JourDTO jour = new CircuitPersonnaliseDTO.JourDTO();
        jour.setNumeroJour(1);
        jour.setVilleId(ville.getIdVille());
        dto.setJours(List.of(jour));

        CircuitPersonnaliseDTO created = circuitPersonnaliseService.create(dto);
        circuitPersonnaliseService.updateStatut(created.getId(), "ACCEPTE", BigDecimal.valueOf(300), null, null);
        circuitPersonnaliseService.createCircuitFromDemandeIfAbsent(created.getId());

        CircuitDTO circuitCree = circuitPersonnaliseService.getMineCircuitCree(created.getId());

        assertNotNull(circuitCree);
        assertFalse(circuitCree.isActif(), "Le circuit renvoye doit rester inactif (pas de fuite vers le catalogue public)");
    }

    @Test
    void gettingCircuitCreeBeforeItExistsIsRejected() {
        CircuitPersonnaliseDTO created = createDemande("workflow.circuit-cree-missing@example.com", null);

        assertThrows(ResourceNotFoundException.class, () -> circuitPersonnaliseService.getMineCircuitCree(created.getId()));
    }

    @Test
    void anotherUserCannotReadSomeoneElsesCircuitCree() {
        String ownerEmail = "workflow.circuit-cree-victim@example.com";
        createUser(ownerEmail);
        authenticateAs(ownerEmail);

        Ville ville = villeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucune ville en fixture de test."));

        CircuitPersonnaliseDTO dto = new CircuitPersonnaliseDTO();
        dto.setNomClient("Doe");
        dto.setPrenomClient("Jane");
        dto.setEmailClient(ownerEmail);
        dto.setTelephoneClient("+22900000000");
        dto.setNombreJours(1);
        dto.setNombrePersonnes(1);
        dto.setDateVoyageSouhaitee(LocalDate.now().plusMonths(1));
        CircuitPersonnaliseDTO.JourDTO jour = new CircuitPersonnaliseDTO.JourDTO();
        jour.setNumeroJour(1);
        jour.setVilleId(ville.getIdVille());
        dto.setJours(List.of(jour));

        CircuitPersonnaliseDTO created = circuitPersonnaliseService.create(dto);
        circuitPersonnaliseService.updateStatut(created.getId(), "ACCEPTE", BigDecimal.valueOf(300), null, null);
        circuitPersonnaliseService.createCircuitFromDemandeIfAbsent(created.getId());

        createUser("workflow.circuit-cree-attacker@example.com");
        authenticateAs("workflow.circuit-cree-attacker@example.com");

        assertThrows(ResourceNotFoundException.class, () -> circuitPersonnaliseService.getMineCircuitCree(created.getId()));
    }

    @Test
    void createCircuitFromDemandeWithNoVilleOnAnyJourDoesNotThrow() {
        CircuitPersonnaliseDTO created = createDemande("workflow.circuit-no-ville@example.com", null);

        circuitPersonnaliseService.createCircuitFromDemandeIfAbsent(created.getId());

        CircuitPersonnalise reloaded = circuitPersonnaliseRepository.findById(created.getId()).orElseThrow();
        assertNull(reloaded.getCircuitCree(),
                "Sans ville sur aucun jour, aucun circuit ne doit etre cree, mais sans lever d'exception (ne doit jamais casser la confirmation de paiement)");
    }

    @Test
    void deleteRemovesTheDemande() {
        CircuitPersonnaliseDTO created = createDemande("workflow.delete@example.com", null);

        circuitPersonnaliseService.delete(created.getId());

        assertThrows(ResourceNotFoundException.class, () -> circuitPersonnaliseService.getById(created.getId()));
    }
}
