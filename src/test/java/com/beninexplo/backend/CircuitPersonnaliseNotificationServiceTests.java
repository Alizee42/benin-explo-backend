package com.beninexplo.backend;

import com.beninexplo.backend.entity.CircuitPersonnalise;
import com.beninexplo.backend.service.CircuitPersonnaliseNotificationService;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regression pour le bug trouve en audit : CircuitPersonnaliseService.updateStatut() changeait
 * le statut d'une demande sans jamais notifier le client par email, malgre le workflow prevu
 * (commentaireAdmin/motifRefus). Meme pattern que ReservationHebergementNotificationServiceTests.
 */
@SpringBootTest(properties = "reservation.notifications.circuit-personnalise.mail.enabled=true")
class CircuitPersonnaliseNotificationServiceTests {

    @Autowired
    private CircuitPersonnaliseNotificationService notificationService;

    @MockitoBean
    private JavaMailSender mailSender;

    private MimeMessage realMimeMessage() {
        return new MimeMessage(Session.getDefaultInstance(new Properties()));
    }

    private String extractPlainTextBody(MimeMessage message) throws Exception {
        Object content = message.getContent();
        while (content instanceof Multipart multipart) {
            content = multipart.getBodyPart(0).getContent();
        }
        return String.valueOf(content);
    }

    private CircuitPersonnalise newDemande(CircuitPersonnalise.StatutDemande statut) {
        CircuitPersonnalise demande = new CircuitPersonnalise();
        demande.setNomClient("Doe");
        demande.setPrenomClient("Jane");
        demande.setEmailClient("notif.cps.test@example.com");
        demande.setTelephoneClient("+22900000000");
        demande.setNombreJours(3);
        demande.setNombrePersonnes(2);
        demande.setStatut(statut);
        demande.setDevisePrixEstime("EUR");
        return demande;
    }

    @Test
    void acceptedStatusSendsEmailWithPrixFinal() throws Exception {
        MimeMessage captured = realMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(captured);

        CircuitPersonnalise demande = newDemande(CircuitPersonnalise.StatutDemande.ACCEPTE);
        demande.setPrixFinal(BigDecimal.valueOf(450));

        notificationService.sendStatutUpdate(demande);

        verify(mailSender, timeout(2000)).send(any(MimeMessage.class));
        assertTrue(captured.getSubject().toLowerCase().contains("accepte"));
        String body = extractPlainTextBody(captured);
        assertTrue(body.contains("450"), "Le corps de l'email doit mentionner le prix final");
    }

    @Test
    void refusedStatusSendsEmailWithMotifRefus() throws Exception {
        MimeMessage captured = realMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(captured);

        CircuitPersonnalise demande = newDemande(CircuitPersonnalise.StatutDemande.REFUSE);
        demande.setMotifRefus("Dates indisponibles");

        notificationService.sendStatutUpdate(demande);

        verify(mailSender, timeout(2000)).send(any(MimeMessage.class));
        String body = extractPlainTextBody(captured);
        assertTrue(body.contains("Dates indisponibles"), "Le corps de l'email doit contenir le motif de refus");
    }

    @Test
    void refusedStatusEmailIncludesLinkToSubmitANewDemande() throws Exception {
        MimeMessage captured = realMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(captured);

        CircuitPersonnalise demande = newDemande(CircuitPersonnalise.StatutDemande.REFUSE);

        notificationService.sendStatutUpdate(demande);

        verify(mailSender, timeout(2000)).send(any(MimeMessage.class));
        String body = extractPlainTextBody(captured);
        assertTrue(body.contains("/circuit-personnalise"),
                "Le refus doit inviter le client a soumettre une nouvelle demande via un lien actionnable");
    }

    @Test
    void intermediateStatusDoesNotSendAnyEmail() {
        CircuitPersonnalise demande = newDemande(CircuitPersonnalise.StatutDemande.EN_TRAITEMENT);

        notificationService.sendStatutUpdate(demande);

        verify(mailSender, never()).createMimeMessage();
    }

    @Test
    void demandeWithoutEmailClientDoesNotThrowAndDoesNotSend() {
        CircuitPersonnalise demande = newDemande(CircuitPersonnalise.StatutDemande.ACCEPTE);
        demande.setEmailClient(null);
        demande.setPrixFinal(BigDecimal.valueOf(100));

        notificationService.sendStatutUpdate(demande);

        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
