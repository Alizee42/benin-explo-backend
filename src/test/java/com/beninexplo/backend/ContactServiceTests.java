package com.beninexplo.backend;

import com.beninexplo.backend.dto.ContactMessageDTO;
import com.beninexplo.backend.entity.ParametresSite;
import com.beninexplo.backend.repository.ParametresSiteRepository;
import com.beninexplo.backend.service.ContactService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ContactService.sendContactMessage() envoie 2 emails (a l'agence + confirmation a
 * l'expediteur) et resout le destinataire agence via ParametresSite.emailContact, avec repli
 * sur une adresse par defaut si aucun parametre n'est configure. Jamais teste jusqu'ici.
 */
@SpringBootTest(properties = "contact.mail.enabled=true")
class ContactServiceTests {

    @Autowired
    private ContactService contactService;

    @Autowired
    private ParametresSiteRepository parametresSiteRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    @AfterEach
    void cleanupParametres() {
        parametresSiteRepository.deleteAll();
    }

    private ContactMessageDTO baseMessage() {
        ContactMessageDTO dto = new ContactMessageDTO();
        dto.setNom("Doe");
        dto.setEmail("sender.test@example.com");
        dto.setSujet("Question sur un circuit");
        dto.setMessage("Bonjour, je souhaite des informations.");
        return dto;
    }

    private List<MimeMessage> captureSentMessages() {
        List<MimeMessage> created = new ArrayList<>();
        when(mailSender.createMimeMessage()).thenAnswer(invocation -> {
            MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()));
            created.add(message);
            return message;
        });
        return created;
    }

    @Test
    void sendsTwoEmailsOneToAgencyAndOneConfirmationToSender() {
        captureSentMessages();

        contactService.sendContactMessage(baseMessage());

        verify(mailSender, timeout(2000).times(2)).send(any(MimeMessage.class));
    }

    @Test
    void confirmationEmailIsSentToTheSenderAddress() throws Exception {
        List<MimeMessage> created = captureSentMessages();

        contactService.sendContactMessage(baseMessage());

        verify(mailSender, timeout(2000).times(2)).send(any(MimeMessage.class));
        boolean confirmationSent = created.stream().anyMatch(m -> {
            try {
                return m.getAllRecipients() != null
                        && m.getAllRecipients().length > 0
                        && "sender.test@example.com".equals(m.getAllRecipients()[0].toString());
            } catch (Exception e) {
                return false;
            }
        });
        assertTrue(confirmationSent, "L'expediteur doit recevoir un email de confirmation a sa propre adresse");
    }

    @Test
    void agencyEmailUsesConfiguredEmailContactWhenAvailable() throws Exception {
        ParametresSite parametres = new ParametresSite();
        parametres.setEmailContact("agence.configuree@example.com");
        parametresSiteRepository.save(parametres);

        List<MimeMessage> created = captureSentMessages();

        contactService.sendContactMessage(baseMessage());

        verify(mailSender, timeout(2000).times(2)).send(any(MimeMessage.class));
        boolean agencyEmailUsesConfiguredAddress = created.stream().anyMatch(m -> {
            try {
                return m.getAllRecipients() != null
                        && m.getAllRecipients().length > 0
                        && "agence.configuree@example.com".equals(m.getAllRecipients()[0].toString());
            } catch (Exception e) {
                return false;
            }
        });
        assertTrue(agencyEmailUsesConfiguredAddress,
                "L'email agence doit utiliser ParametresSite.emailContact quand il est configure");
    }

    @Test
    void agencyEmailSubjectIncludesTheProvidedSujet() throws Exception {
        List<MimeMessage> created = captureSentMessages();

        contactService.sendContactMessage(baseMessage());

        verify(mailSender, timeout(2000).times(2)).send(any(MimeMessage.class));
        boolean subjectFound = created.stream().anyMatch(m -> {
            try {
                String subject = m.getSubject();
                return subject != null && subject.contains("Question sur un circuit");
            } catch (Exception e) {
                return false;
            }
        });
        assertTrue(subjectFound, "L'email agence doit reprendre le sujet fourni par l'expediteur");
    }
}
