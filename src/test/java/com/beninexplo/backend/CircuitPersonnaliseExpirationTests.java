package com.beninexplo.backend;

import com.beninexplo.backend.dto.CircuitPersonnaliseDTO;
import com.beninexplo.backend.entity.CircuitPersonnalise;
import com.beninexplo.backend.entity.PaiementCircuitPersonnalise;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.repository.CircuitPersonnaliseRepository;
import com.beninexplo.backend.repository.PaiementCircuitPersonnaliseRepository;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression pour le chantier relance/expiration : un devis ACCEPTE mais jamais paye restait
 * en limbo indefiniment avant ce chantier. sendRappelsPaiement() relance a 7 jours,
 * expirerDemandesNonPayees() fait passer le statut a EXPIRE a 14 jours (job planifie quotidien,
 * CircuitPersonnaliseExpirationJob, teste indirectement via les methodes de service qu'il appelle).
 */
@SpringBootTest
@Transactional
class CircuitPersonnaliseExpirationTests {

    @Autowired
    private CircuitPersonnaliseService circuitPersonnaliseService;

    @Autowired
    private CircuitPersonnaliseRepository circuitPersonnaliseRepository;

    @Autowired
    private PaiementCircuitPersonnaliseRepository paiementRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @AfterEach
    void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElseGet(() -> {
            Utilisateur u = new Utilisateur();
            u.setNom("Test");
            u.setPrenom("Client");
            u.setEmail(email);
            u.setTelephone("+22900000000");
            u.setMotDePasse("hash");
            u.setRole("USER");
            return utilisateurRepository.save(u);
        });
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList()));
    }

    private CircuitPersonnalise createAcceptedDemande(String email, int joursDepuisAcceptation) {
        authenticateAs(email);

        CircuitPersonnaliseDTO dto = new CircuitPersonnaliseDTO();
        dto.setNomClient("Doe");
        dto.setPrenomClient("Jane");
        dto.setEmailClient(email);
        dto.setTelephoneClient("+22900000000");
        dto.setNombreJours(2);
        dto.setNombrePersonnes(2);
        dto.setDateVoyageSouhaitee(LocalDate.now().plusMonths(1));
        CircuitPersonnaliseDTO created = circuitPersonnaliseService.create(dto);

        circuitPersonnaliseService.updateStatut(created.getId(), "ACCEPTE", BigDecimal.valueOf(300), null, null);

        CircuitPersonnalise entity = circuitPersonnaliseRepository.findById(created.getId()).orElseThrow();
        entity.setDateTraitement(LocalDate.now().minusDays(joursDepuisAcceptation));
        return circuitPersonnaliseRepository.save(entity);
    }

    @Test
    void sendRappelsPaiementMarksReminderSentForOldUnpaidAcceptedDemande() {
        CircuitPersonnalise demande = createAcceptedDemande("expiration.rappel@example.com", 8);

        circuitPersonnaliseService.sendRappelsPaiement();

        CircuitPersonnalise reloaded = circuitPersonnaliseRepository.findById(demande.getId()).orElseThrow();
        assertNotNull(reloaded.getDateRappelPaiementEnvoye(), "Le rappel doit etre marque comme envoye");
        assertEquals(CircuitPersonnalise.StatutDemande.ACCEPTE, reloaded.getStatut(), "Le statut ne doit pas changer a ce stade");
    }

    @Test
    void sendRappelsPaiementDoesNotSendTwiceForTheSameDemande() {
        CircuitPersonnalise demande = createAcceptedDemande("expiration.rappel-once@example.com", 8);

        circuitPersonnaliseService.sendRappelsPaiement();
        var firstReminderDate = circuitPersonnaliseRepository.findById(demande.getId()).orElseThrow().getDateRappelPaiementEnvoye();

        circuitPersonnaliseService.sendRappelsPaiement();
        var secondReminderDate = circuitPersonnaliseRepository.findById(demande.getId()).orElseThrow().getDateRappelPaiementEnvoye();

        assertEquals(firstReminderDate, secondReminderDate, "Un second appel ne doit pas re-marquer/renvoyer le rappel");
    }

    @Test
    void sendRappelsPaiementIgnoresRecentlyAcceptedDemande() {
        CircuitPersonnalise demande = createAcceptedDemande("expiration.rappel-recent@example.com", 2);

        circuitPersonnaliseService.sendRappelsPaiement();

        CircuitPersonnalise reloaded = circuitPersonnaliseRepository.findById(demande.getId()).orElseThrow();
        assertNull(reloaded.getDateRappelPaiementEnvoye(), "Une demande acceptee il y a seulement 2 jours ne doit pas etre relancee");
    }

    @Test
    void sendRappelsPaiementIgnoresAlreadyPaidDemande() {
        CircuitPersonnalise demande = createAcceptedDemande("expiration.rappel-paye@example.com", 8);
        PaiementCircuitPersonnalise paiement = new PaiementCircuitPersonnalise();
        paiement.setCircuitPersonnalise(demande);
        paiement.setStatut("PAYE");
        paiement.setMontant(BigDecimal.valueOf(300));
        paiementRepository.save(paiement);

        circuitPersonnaliseService.sendRappelsPaiement();

        CircuitPersonnalise reloaded = circuitPersonnaliseRepository.findById(demande.getId()).orElseThrow();
        assertNull(reloaded.getDateRappelPaiementEnvoye(), "Une demande deja payee ne doit jamais etre relancee");
    }

    @Test
    void expirerDemandesNonPayeesSetsStatutExpireAfterFourteenDays() {
        CircuitPersonnalise demande = createAcceptedDemande("expiration.expire@example.com", 15);

        circuitPersonnaliseService.expirerDemandesNonPayees();

        CircuitPersonnalise reloaded = circuitPersonnaliseRepository.findById(demande.getId()).orElseThrow();
        assertEquals(CircuitPersonnalise.StatutDemande.EXPIRE, reloaded.getStatut());
    }

    @Test
    void expirerDemandesNonPayeesIgnoresDemandeAcceptedLessThanFourteenDaysAgo() {
        CircuitPersonnalise demande = createAcceptedDemande("expiration.expire-recent@example.com", 10);

        circuitPersonnaliseService.expirerDemandesNonPayees();

        CircuitPersonnalise reloaded = circuitPersonnaliseRepository.findById(demande.getId()).orElseThrow();
        assertEquals(CircuitPersonnalise.StatutDemande.ACCEPTE, reloaded.getStatut(),
                "Une demande acceptee il y a seulement 10 jours ne doit pas expirer");
    }

    @Test
    void expirerDemandesNonPayeesIgnoresAlreadyPaidDemande() {
        CircuitPersonnalise demande = createAcceptedDemande("expiration.expire-paye@example.com", 15);
        PaiementCircuitPersonnalise paiement = new PaiementCircuitPersonnalise();
        paiement.setCircuitPersonnalise(demande);
        paiement.setStatut("PAYE");
        paiement.setMontant(BigDecimal.valueOf(300));
        paiementRepository.save(paiement);

        circuitPersonnaliseService.expirerDemandesNonPayees();

        CircuitPersonnalise reloaded = circuitPersonnaliseRepository.findById(demande.getId()).orElseThrow();
        assertEquals(CircuitPersonnalise.StatutDemande.ACCEPTE, reloaded.getStatut(),
                "Une demande deja payee ne doit jamais expirer");
    }
}
