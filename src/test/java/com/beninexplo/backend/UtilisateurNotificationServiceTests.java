package com.beninexplo.backend;

import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.service.UtilisateurNotificationService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * UtilisateurNotificationService.sendWelcomeEmail() est @Async : on ne peut pas observer son
 * effet en appelant simplement la methode, il faut attendre le thread async (Mockito.timeout)
 * et mocker JavaMailSender (mailEnabled=false par defaut hors de ce test dedie). Verifie le
 * comportement reel de contenu (sujet, destinataire), pas seulement que "ca ne plante pas".
 */
@SpringBootTest(properties = "utilisateur.notifications.mail.enabled=true")
class UtilisateurNotificationServiceTests {

    @Autowired
    private UtilisateurNotificationService notificationService;

    @MockitoBean
    private JavaMailSender mailSender;

    private MimeMessage realMimeMessage() {
        return new MimeMessage(Session.getDefaultInstance(new Properties()));
    }

    @Test
    void sendWelcomeEmailSendsAMessageToTheUsersAddress() throws Exception {
        MimeMessage captured = realMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(captured);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Doe");
        utilisateur.setPrenom("Jane");
        utilisateur.setEmail("welcome.test@example.com");

        notificationService.sendWelcomeEmail(utilisateur);

        verify(mailSender, timeout(2000)).send(any(MimeMessage.class));
        org.junit.jupiter.api.Assertions.assertEquals("welcome.test@example.com", captured.getAllRecipients()[0].toString());
        org.junit.jupiter.api.Assertions.assertEquals("Bienvenue sur Benin Explo", captured.getSubject());
    }

    @Test
    void sendWelcomeEmailDoesNotFailWhenPrenomIsMissing() throws Exception {
        MimeMessage captured = realMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(captured);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(2L);
        utilisateur.setEmail("no.prenom@example.com");
        utilisateur.setPrenom(null);

        notificationService.sendWelcomeEmail(utilisateur);

        // Ne doit pas lever d'exception (attrapee et loguee cote service) ; on verifie juste
        // que l'envoi a quand meme lieu malgre le prenom manquant (fallback "voyageur").
        verify(mailSender, timeout(2000)).send(any(MimeMessage.class));
    }
}
