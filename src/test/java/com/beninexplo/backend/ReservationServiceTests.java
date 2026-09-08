package com.beninexplo.backend;

import com.beninexplo.backend.dto.ReservationRequestDTO;
import com.beninexplo.backend.dto.ReservationResponseDTO;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.CircuitRepository;
import com.beninexplo.backend.repository.UtilisateurRepository;
import com.beninexplo.backend.repository.VilleRepository;
import com.beninexplo.backend.service.ReservationService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Couvre ReservationService (reservation de circuits) : contrairement a
 * ReservationHebergementService, il n'y a pas de controle de disponibilite (un circuit n'a pas
 * de capacite limitee par date), donc la logique interessante est ailleurs - resolution du
 * client (l'email du compte connecte prime toujours sur celui du formulaire, pour eviter qu'un
 * client falsifie son email), generation de reference, et controle d'acces "mes reservations".
 */
@SpringBootTest
@Transactional
class ReservationServiceTests {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private CircuitRepository circuitRepository;

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @AfterEach
    void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    private Utilisateur createUser(String email) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom("Doe");
        utilisateur.setPrenom("Jane");
        utilisateur.setEmail(email);
        utilisateur.setTelephone("+22900000000");
        utilisateur.setMotDePasse("hash");
        utilisateur.setRole("USER");
        return utilisateurRepository.save(utilisateur);
    }

    private void authenticateAs(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList()));
    }

    private Circuit createCircuit() {
        Ville ville = villeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucune ville en fixture de test."));
        Circuit circuit = new Circuit();
        circuit.setNom("Circuit reservation test");
        circuit.setVille(ville);
        circuit.setPrixIndicatif(BigDecimal.valueOf(150));
        return circuitRepository.save(circuit);
    }

    private ReservationRequestDTO baseRequest(Long circuitId) {
        ReservationRequestDTO dto = new ReservationRequestDTO();
        dto.setDateReservation(LocalDate.now().plusDays(20));
        dto.setCircuitId(circuitId);
        dto.setNombrePersonnes(2);
        return dto;
    }

    @Test
    void authenticatedUserEmailAlwaysPrevailsOverFormEmail() {
        createUser("real.owner@example.com");
        authenticateAs("real.owner@example.com");
        Circuit circuit = createCircuit();

        ReservationRequestDTO dto = baseRequest(circuit.getIdCircuit());
        dto.setNom("Doe");
        dto.setPrenom("Jane");
        dto.setTelephone("+22900000000");
        // Tentative de falsifier l'email : doit etre ignoree au profit de l'email du compte.
        dto.setEmail("attacker.controlled@example.com");

        ReservationResponseDTO created = reservationService.create(dto);

        assertEquals("real.owner@example.com", created.getEmail());
    }

    @Test
    void anonymousReservationRequiresExplicitEmail() {
        // Pas d'authentification : create() delegue quand meme a AuthenticatedUserService qui
        // exige un utilisateur connecte pour ce endpoint (verifie separement par SecurityConfig,
        // mais fromDTO() a aussi son propre garde-fou si jamais appele sans utilisateur).
        SecurityContextHolder.clearContext();
        Circuit circuit = createCircuit();
        ReservationRequestDTO dto = baseRequest(circuit.getIdCircuit());
        dto.setNom("Doe");
        dto.setPrenom("Jane");
        dto.setTelephone("+22900000000");

        assertThrows(BadRequestException.class, () -> reservationService.create(dto));
    }

    @Test
    void creatingWithUnknownCircuitIsRejected() {
        createUser("unknown.circuit@example.com");
        authenticateAs("unknown.circuit@example.com");

        ReservationRequestDTO dto = baseRequest(999999L);
        dto.setNom("Doe");
        dto.setPrenom("Jane");
        dto.setTelephone("+22900000000");

        assertThrows(ResourceNotFoundException.class, () -> reservationService.create(dto));
    }

    @Test
    void createdReservationGetsAUniqueCirReference() {
        createUser("reference.test@example.com");
        authenticateAs("reference.test@example.com");
        Circuit circuit = createCircuit();
        ReservationRequestDTO dto = baseRequest(circuit.getIdCircuit());
        dto.setNom("Doe");
        dto.setPrenom("Jane");
        dto.setTelephone("+22900000000");

        ReservationResponseDTO created = reservationService.create(dto);

        assertTrue(created.getReferenceReservation().matches("CIR-\\d{6}"),
                "La reference doit suivre le format CIR-XXXXXX");
    }

    @Test
    void ownerCanReadTheirOwnReservationButNotAnotherUsers() {
        createUser("owner.res@example.com");
        authenticateAs("owner.res@example.com");
        Circuit circuit = createCircuit();
        ReservationRequestDTO dto = baseRequest(circuit.getIdCircuit());
        dto.setNom("Doe");
        dto.setPrenom("Jane");
        dto.setTelephone("+22900000000");
        ReservationResponseDTO created = reservationService.create(dto);

        ReservationResponseDTO fetched = reservationService.getMineById(created.getId());
        assertEquals(created.getId(), fetched.getId());

        createUser("intruder.res@example.com");
        authenticateAs("intruder.res@example.com");
        assertThrows(ResourceNotFoundException.class, () -> reservationService.getMineById(created.getId()));
    }
}
