package com.beninexplo.backend.service;

import com.beninexplo.backend.entity.ReservationHebergement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ReservationHebergementNotificationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationHebergementNotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${reservation.notifications.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${reservation.notifications.mail.from:no-reply@beninexplo.com}")
    private String fromAddress;

    public ReservationHebergementNotificationService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSender = mailSenderProvider.getIfAvailable();
    }

    @Async
    public void sendCreationConfirmation(ReservationHebergement reservation) {
        sendReservationEmail(
                reservation,
                "Confirmation de reservation - " + reservation.getHebergement().getNom(),
                "Votre reservation a bien ete enregistree.\n\n"
                        + "Hebergement: " + reservation.getHebergement().getNom() + "\n"
                        + "Arrivee: " + reservation.getDateArrivee() + "\n"
                        + "Depart: " + reservation.getDateDepart() + "\n"
                        + "Nombre de nuits: " + reservation.getNombreNuits() + "\n"
                        + "Prix total estime: " + reservation.getPrixTotal() + " EUR\n"
                        + "Statut: " + reservation.getStatut() + "\n\n"
                        + "Merci."
        );
    }

    @Async
    public void sendStatusUpdate(ReservationHebergement reservation) {
        sendReservationEmail(
                reservation,
                "Mise a jour de votre reservation - " + reservation.getHebergement().getNom(),
                "Le statut de votre reservation a ete mis a jour.\n\n"
                        + "Hebergement: " + reservation.getHebergement().getNom() + "\n"
                        + "Arrivee: " + reservation.getDateArrivee() + "\n"
                        + "Depart: " + reservation.getDateDepart() + "\n"
                        + "Nouveau statut: " + reservation.getStatut() + "\n\n"
                        + "Merci."
        );
    }

    private void sendReservationEmail(ReservationHebergement reservation, String subject, String body) {
        if (!mailEnabled) {
            log.info("Notification email desactivee. Reservation id={}, sujet={}", reservation.getIdReservation(), subject);
            return;
        }

        if (mailSender == null) {
            log.warn("JavaMailSender indisponible. Notification email ignoree pour reservation id={}", reservation.getIdReservation());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(reservation.getEmailClient());
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            // Do not break business flow if mail fails.
            log.warn("Envoi email impossible pour reservation id={}: {}", reservation.getIdReservation(), e.getMessage());
        }
    }
}
