package com.beninexplo.backend;

import com.beninexplo.backend.dto.ReservationHebergementDTO;
import com.beninexplo.backend.entity.Hebergement;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.repository.HebergementRepository;
import com.beninexplo.backend.repository.ReservationHebergementRepository;
import com.beninexplo.backend.repository.UtilisateurRepository;
import com.beninexplo.backend.service.ReservationHebergementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prouve que deux demandes de réservation concurrentes sur le même hébergement et les mêmes
 * dates ne peuvent plus toutes les deux réussir (régression du double-booking corrigé par le
 * verrou pessimiste dans ReservationHebergementService / HebergementRepository#findByIdForUpdate).
 */
@SpringBootTest
class ReservationHebergementConcurrencyTests {

    @Autowired
    private ReservationHebergementService reservationHebergementService;

    @Autowired
    private HebergementRepository hebergementRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ReservationHebergementRepository reservationHebergementRepository;

    private Long hebergementId;

    @BeforeEach
    void setUp() {
        Hebergement hebergement = new Hebergement();
        hebergement.setNom("Hotel Concurrence Test");
        hebergement.setType("Hotel");
        hebergement.setLocalisation("Cotonou");
        hebergement.setDescription("Fixture dediee au test de concurrence.");
        hebergement.setPrixParNuit(BigDecimal.valueOf(50));
        hebergementId = hebergementRepository.save(hebergement).getIdHebergement();
    }

    @Test
    void concurrentBookingsOnSameDatesOnlyOneSucceeds() throws Exception {
        LocalDate arrivee = LocalDate.now().plusDays(30);
        LocalDate depart = LocalDate.now().plusDays(33);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        List<Callable<Boolean>> tasks = List.of(
                bookingTask("client1@example.com", arrivee, depart, readyLatch, startLatch),
                bookingTask("client2@example.com", arrivee, depart, readyLatch, startLatch)
        );

        List<Future<Boolean>> futures = tasks.stream().map(executor::submit).collect(Collectors.toList());

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown();

        long successCount = 0;
        for (Future<Boolean> future : futures) {
            if (future.get(10, TimeUnit.SECONDS)) {
                successCount++;
            }
        }
        executor.shutdown();

        assertEquals(1, successCount,
                "Une seule des deux reservations concurrentes sur les memes dates doit reussir.");

        long persisted = reservationHebergementRepository.findByHebergementIdHebergement(hebergementId).stream()
                .filter(r -> !"ANNULEE".equals(r.getStatut()))
                .count();
        assertTrue(persisted <= 1, "Il ne doit pas y avoir de double-booking persiste en base.");
    }

    private Callable<Boolean> bookingTask(String email, LocalDate arrivee, LocalDate depart,
                                           CountDownLatch readyLatch, CountDownLatch startLatch) {
        return () -> {
            createUserIfAbsent(email);
            setAuthenticatedUser(email);
            readyLatch.countDown();
            startLatch.await(5, TimeUnit.SECONDS);

            ReservationHebergementDTO dto = new ReservationHebergementDTO();
            dto.setHebergementId(hebergementId);
            dto.setNomClient("Client");
            dto.setPrenomClient("Test");
            dto.setEmailClient(email);
            dto.setTelephoneClient("+22900000000");
            dto.setDateArrivee(arrivee);
            dto.setDateDepart(depart);
            dto.setNombrePersonnes(2);

            try {
                reservationHebergementService.create(dto);
                return true;
            } catch (RuntimeException ex) {
                return false;
            } finally {
                SecurityContextHolder.clearContext();
            }
        };
    }

    private void createUserIfAbsent(String email) {
        if (utilisateurRepository.findByEmail(email).isPresent()) {
            return;
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom("Test");
        utilisateur.setPrenom("Client");
        utilisateur.setEmail(email);
        utilisateur.setTelephone("+22900000000");
        utilisateur.setMotDePasse("hash");
        utilisateur.setRole("USER");
        utilisateurRepository.save(utilisateur);
    }

    private void setAuthenticatedUser(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList()));
        SecurityContextHolder.setContext(context);
    }
}
