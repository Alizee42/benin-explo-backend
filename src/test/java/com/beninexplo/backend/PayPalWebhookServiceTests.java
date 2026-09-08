package com.beninexplo.backend;

import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.CircuitPersonnalise;
import com.beninexplo.backend.entity.Hebergement;
import com.beninexplo.backend.entity.PaiementCircuitPersonnalise;
import com.beninexplo.backend.entity.PaiementReservationCircuit;
import com.beninexplo.backend.entity.PaiementReservationHebergement;
import com.beninexplo.backend.entity.Reservation;
import com.beninexplo.backend.entity.ReservationHebergement;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.repository.CircuitPersonnaliseRepository;
import com.beninexplo.backend.repository.CircuitRepository;
import com.beninexplo.backend.repository.HebergementRepository;
import com.beninexplo.backend.repository.PaiementCircuitPersonnaliseRepository;
import com.beninexplo.backend.repository.PaiementReservationCircuitRepository;
import com.beninexplo.backend.repository.PaiementReservationHebergementRepository;
import com.beninexplo.backend.repository.ReservationHebergementRepository;
import com.beninexplo.backend.repository.ReservationRepository;
import com.beninexplo.backend.repository.VilleRepository;
import com.beninexplo.backend.service.PayPalWebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * PayPalWebhookService recoit des evenements PayPal asynchrones (le paiement peut evoluer sans
 * action de l'utilisateur : capture confirmee en differe, remboursement...). Jusqu'ici il ne
 * cherchait que dans les paiements d'hebergement : un webhook pour un paiement de circuit ou de
 * circuit personnalise ne mettait jamais a jour le statut. Generalise aux 3 flux (2026-09-08),
 * couvert ici pour les 3 + le garde-fou anti-retrogradation d'un remboursement.
 */
@SpringBootTest
@Transactional
class PayPalWebhookServiceTests {

    @Autowired
    private PayPalWebhookService webhookService;

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private CircuitRepository circuitRepository;

    @Autowired
    private HebergementRepository hebergementRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationHebergementRepository reservationHebergementRepository;

    @Autowired
    private CircuitPersonnaliseRepository circuitPersonnaliseRepository;

    @Autowired
    private PaiementReservationCircuitRepository circuitPaymentRepository;

    @Autowired
    private PaiementReservationHebergementRepository hebergementPaymentRepository;

    @Autowired
    private PaiementCircuitPersonnaliseRepository circuitPersonnalisePaymentRepository;

    private String captureCompletedEvent(String captureId) {
        return """
                {
                  "id": "WH-EVENT-1",
                  "event_type": "PAYMENT.CAPTURE.COMPLETED",
                  "resource": { "id": "%s" }
                }
                """.formatted(captureId);
    }

    private String captureRefundedEvent(String captureId) {
        return """
                {
                  "id": "WH-EVENT-2",
                  "event_type": "PAYMENT.CAPTURE.REFUNDED",
                  "resource": { "id": "%s" }
                }
                """.formatted(captureId);
    }

    @Test
    void completedCaptureUpdatesHebergementPaymentStatus() {
        Hebergement hebergement = hebergementRepository.save(newHebergement());
        ReservationHebergement reservation = reservationHebergementRepository.save(
                newReservationHebergement(hebergement));
        PaiementReservationHebergement payment = new PaiementReservationHebergement();
        payment.setReservationHebergement(reservation);
        payment.setProvider("PAYPAL");
        payment.setStatut("EN_COURS");
        payment.setMontant(BigDecimal.valueOf(100));
        payment.setDevise("EUR");
        payment.setPaypalCaptureId("CAPTURE-HEB-1");
        hebergementPaymentRepository.save(payment);

        webhookService.processEvent(captureCompletedEvent("CAPTURE-HEB-1"));

        PaiementReservationHebergement reloaded = hebergementPaymentRepository.findById(payment.getId()).orElseThrow();
        assertEquals("PAYE", reloaded.getStatut());
        assertNotNull(reloaded.getDatePaiement());
    }

    @Test
    void completedCaptureUpdatesCircuitPaymentStatus() {
        Circuit circuit = circuitRepository.save(newCircuit());
        Reservation reservation = reservationRepository.save(newReservation(circuit));
        PaiementReservationCircuit payment = new PaiementReservationCircuit();
        payment.setReservation(reservation);
        payment.setProvider("PAYPAL");
        payment.setStatut("EN_COURS");
        payment.setMontant(BigDecimal.valueOf(100));
        payment.setDevise("EUR");
        payment.setPaypalCaptureId("CAPTURE-CIR-1");
        circuitPaymentRepository.save(payment);

        webhookService.processEvent(captureCompletedEvent("CAPTURE-CIR-1"));

        PaiementReservationCircuit reloaded = circuitPaymentRepository.findById(payment.getId()).orElseThrow();
        assertEquals("PAYE", reloaded.getStatut());
    }

    @Test
    void completedCaptureUpdatesCircuitPersonnalisePaymentStatus() {
        CircuitPersonnalise demande = circuitPersonnaliseRepository.save(newCircuitPersonnalise());
        PaiementCircuitPersonnalise payment = new PaiementCircuitPersonnalise();
        payment.setCircuitPersonnalise(demande);
        payment.setProvider("PAYPAL");
        payment.setStatut("EN_COURS");
        payment.setMontant(BigDecimal.valueOf(100));
        payment.setDevise("EUR");
        payment.setPaypalCaptureId("CAPTURE-CP-1");
        circuitPersonnalisePaymentRepository.save(payment);

        webhookService.processEvent(captureCompletedEvent("CAPTURE-CP-1"));

        PaiementCircuitPersonnalise reloaded = circuitPersonnalisePaymentRepository.findById(payment.getId()).orElseThrow();
        assertEquals("PAYE", reloaded.getStatut());
    }

    @Test
    void refundedEventNeverRegressesBackToPaye() {
        Hebergement hebergement = hebergementRepository.save(newHebergement());
        ReservationHebergement reservation = reservationHebergementRepository.save(
                newReservationHebergement(hebergement));
        PaiementReservationHebergement payment = new PaiementReservationHebergement();
        payment.setReservationHebergement(reservation);
        payment.setProvider("PAYPAL");
        payment.setStatut("REMBOURSE");
        payment.setMontant(BigDecimal.valueOf(100));
        payment.setDevise("EUR");
        payment.setPaypalCaptureId("CAPTURE-HEB-2");
        payment.setDatePaiement(LocalDateTime.now().minusDays(1));
        hebergementPaymentRepository.save(payment);

        // Un evenement COMPLETED tardif arrive apres coup (retry PayPal) : ne doit pas
        // retrograder un paiement deja rembourse vers PAYE.
        webhookService.processEvent(captureCompletedEvent("CAPTURE-HEB-2"));

        PaiementReservationHebergement reloaded = hebergementPaymentRepository.findById(payment.getId()).orElseThrow();
        assertEquals("REMBOURSE", reloaded.getStatut());
    }

    @Test
    void refundedEventUpdatesStatus() {
        Hebergement hebergement = hebergementRepository.save(newHebergement());
        ReservationHebergement reservation = reservationHebergementRepository.save(
                newReservationHebergement(hebergement));
        PaiementReservationHebergement payment = new PaiementReservationHebergement();
        payment.setReservationHebergement(reservation);
        payment.setProvider("PAYPAL");
        payment.setStatut("PAYE");
        payment.setMontant(BigDecimal.valueOf(100));
        payment.setDevise("EUR");
        payment.setPaypalCaptureId("CAPTURE-HEB-3");
        payment.setDatePaiement(LocalDateTime.now());
        hebergementPaymentRepository.save(payment);

        webhookService.processEvent(captureRefundedEvent("CAPTURE-HEB-3"));

        PaiementReservationHebergement reloaded = hebergementPaymentRepository.findById(payment.getId()).orElseThrow();
        assertEquals("REMBOURSE", reloaded.getStatut());
    }

    @Test
    void unknownCaptureIdIsIgnoredWithoutError() {
        // Aucun paiement ne correspond dans aucun des 3 flux : ne doit pas lever d'exception
        // (le controller renvoie 200 quoi qu'il arrive pour eviter les retries PayPal).
        webhookService.processEvent(captureCompletedEvent("CAPTURE-INCONNUE"));
    }

    @Test
    void malformedJsonBodyIsIgnoredWithoutError() {
        webhookService.processEvent("{ceci n'est pas du json valide");
    }

    private Hebergement newHebergement() {
        Hebergement hebergement = new Hebergement();
        hebergement.setNom("Hotel Webhook Test");
        hebergement.setType("Hotel");
        hebergement.setLocalisation("Cotonou");
        hebergement.setDescription("Fixture webhook.");
        hebergement.setPrixParNuit(BigDecimal.valueOf(50));
        return hebergement;
    }

    private ReservationHebergement newReservationHebergement(Hebergement hebergement) {
        return new ReservationHebergement(hebergement, "Doe", "Jane", "webhook.test@example.com",
                "+22900000000", LocalDate.now().plusDays(10), LocalDate.now().plusDays(12), 2, null);
    }

    private Ville anyVille() {
        return villeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucune ville en fixture de test."));
    }

    private Circuit newCircuit() {
        Circuit circuit = new Circuit();
        circuit.setNom("Circuit Webhook Test");
        circuit.setVille(anyVille());
        circuit.setPrixIndicatif(BigDecimal.valueOf(100));
        return circuit;
    }

    private Reservation newReservation(Circuit circuit) {
        Reservation reservation = new Reservation();
        reservation.setNom("Doe");
        reservation.setPrenom("Jane");
        reservation.setEmail("webhook.circuit@example.com");
        reservation.setTelephone("+22900000000");
        reservation.setDateReservation(LocalDate.now().plusDays(15));
        reservation.setCircuit(circuit);
        reservation.setReferenceReservation(java.util.UUID.randomUUID().toString().substring(0, 20));
        return reservation;
    }

    private CircuitPersonnalise newCircuitPersonnalise() {
        CircuitPersonnalise demande = new CircuitPersonnalise();
        demande.setNomClient("Doe");
        demande.setPrenomClient("Jane");
        demande.setEmailClient("webhook.cp@example.com");
        demande.setTelephoneClient("+22900000000");
        demande.setNombreJours(3);
        demande.setNombrePersonnes(2);
        demande.setStatut(CircuitPersonnalise.StatutDemande.ACCEPTE);
        demande.setDevisePrixEstime("EUR");
        return demande;
    }
}
