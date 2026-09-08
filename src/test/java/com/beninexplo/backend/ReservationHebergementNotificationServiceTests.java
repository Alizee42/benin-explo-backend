package com.beninexplo.backend;

import com.beninexplo.backend.entity.Hebergement;
import com.beninexplo.backend.entity.ReservationHebergement;
import com.beninexplo.backend.service.ReservationHebergementNotificationService;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ReservationHebergementNotificationService formate un montant EUR -> FCFA (taux fixe) et
 * mappe le statut de reservation vers un libelle humain dans l'email envoye au client. Ces
 * deux comportements etaient jusqu'ici invisibles (private, jamais exerces par un test) :
 * verifies ici via le contenu reel du MimeMessage capture.
 */
@SpringBootTest(properties = "reservation.notifications.hebergement.mail.enabled=true")
class ReservationHebergementNotificationServiceTests {

    @Autowired
    private ReservationHebergementNotificationService notificationService;

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

    private ReservationHebergement newReservation(String statut, BigDecimal prixTotalEur) {
        Hebergement hebergement = new Hebergement();
        hebergement.setNom("Hotel Notification Test");
        hebergement.setPrixParNuit(BigDecimal.valueOf(50));

        ReservationHebergement reservation = new ReservationHebergement(
                hebergement, "Doe", "Jane", "notif.test@example.com", "+22900000000",
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(12), 2, null);
        reservation.setPrixTotal(prixTotalEur);
        reservation.setStatut(statut);
        return reservation;
    }

    @Test
    void creationConfirmationConvertsEuroAmountToFcfaInBody() throws Exception {
        MimeMessage captured = realMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(captured);

        // 100 EUR * 655.957 = 65595.7, arrondi a 65596 F CFA (format francais : espace insecable).
        notificationService.sendCreationConfirmation(newReservation("EN_ATTENTE", BigDecimal.valueOf(100)));

        verify(mailSender, timeout(2000)).send(any(MimeMessage.class));
        String body = extractPlainTextBody(captured);
        assertTrue(body.contains("F CFA"), "Le corps de l'email doit contenir le montant converti en F CFA");
        String normalizedBody = body.replaceAll("[\\s\\p{Zs}]+", " ");
        assertTrue(normalizedBody.contains("65 596"),
                "100 EUR doit se convertir en environ 65596 F CFA (taux fixe 655.957)");
    }

    @Test
    void statusUpdateSubjectReflectsTheHumanReadableStatusLabel() throws Exception {
        MimeMessage captured = realMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(captured);

        notificationService.sendStatusUpdate(newReservation("CONFIRMEE", BigDecimal.valueOf(50)));

        verify(mailSender, timeout(2000)).send(any(MimeMessage.class));
        assertEquals("Reservation Confirmee - Hotel Notification Test", captured.getSubject());
    }

    @Test
    void cancelledStatusProducesTheCancelledLabel() throws Exception {
        MimeMessage captured = realMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(captured);

        notificationService.sendStatusUpdate(newReservation("ANNULE", BigDecimal.valueOf(50)));

        verify(mailSender, timeout(2000)).send(any(MimeMessage.class));
        assertTrue(captured.getSubject().contains("Annulee"),
                "Le statut brut ANNULE (sans E final) doit etre normalise en Annulee dans le sujet");
    }
}
