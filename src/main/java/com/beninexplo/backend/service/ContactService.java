package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ContactMessageDTO;
import com.beninexplo.backend.repository.ParametresSiteRepository;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;

@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);

    private final JavaMailSender mailSender;
    private final ParametresSiteRepository parametresRepo;

    @Value("${contact.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${reservation.notifications.mail.from:no-reply@beninexplo.com}")
    private String fromAddress;

    @Value("${contact.mail.fallback:contact@beninexplo.com}")
    private String fallbackEmail;

    public ContactService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            ParametresSiteRepository parametresRepo
    ) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.parametresRepo = parametresRepo;
    }

    @Async
    public void sendContactMessage(ContactMessageDTO dto) {
        if (!mailEnabled) {
            log.info("Email de contact desactive. Expediteur={}", dto.getEmail());
            return;
        }
        if (mailSender == null) {
            log.warn("JavaMailSender indisponible. Message de contact ignore.");
            return;
        }

        String dest = parametresRepo.findAll().stream()
                .findFirst()
                .map(p -> p.getEmailContact())
                .filter(e -> e != null && !e.isBlank())
                .orElse(fallbackEmail);

        try {
            // Email vers l'agence
            MimeMessage toAgence = mailSender.createMimeMessage();
            MimeMessageHelper h1 = new MimeMessageHelper(toAgence, true, StandardCharsets.UTF_8.name());
            h1.setFrom(fromAddress);
            h1.setTo(dest);
            h1.setReplyTo(dto.getEmail());
            h1.setSubject(buildSubjectAgence(dto));
            h1.setText(buildPlainAgence(dto), buildHtmlAgence(dto));
            mailSender.send(toAgence);
            log.info("Message de contact transmis a l agence. Expediteur={}", dto.getEmail());

            // Email de confirmation à l'expéditeur
            MimeMessage toSender = mailSender.createMimeMessage();
            MimeMessageHelper h2 = new MimeMessageHelper(toSender, true, StandardCharsets.UTF_8.name());
            h2.setFrom(fromAddress);
            h2.setTo(dto.getEmail());
            h2.setSubject("Votre message a bien ete recu — Benin Explo");
            h2.setText(buildPlainConfirmation(dto), buildHtmlConfirmation(dto));
            mailSender.send(toSender);
            log.info("Confirmation de contact envoyee. Destinataire={}", dto.getEmail());

        } catch (Exception e) {
            log.warn("Envoi du message de contact impossible: {}", e.getMessage());
        }
    }

    private String buildSubjectAgence(ContactMessageDTO dto) {
        String sujet = dto.getSujet() != null && !dto.getSujet().isBlank()
                ? dto.getSujet().trim()
                : "Nouveau message depuis le site";
        return "[Contact] " + sujet;
    }

    private String buildPlainAgence(ContactMessageDTO dto) {
        return """
                Nouveau message de contact — Benin Explo

                Nom     : %s
                Email   : %s
                Sujet   : %s

                Message :
                %s
                """.formatted(
                safe(dto.getNom()),
                safe(dto.getEmail()),
                safe(dto.getSujet()),
                safe(dto.getMessage())
        );
    }

    private String buildHtmlAgence(ContactMessageDTO dto) {
        String nom     = esc(dto.getNom());
        String email   = esc(dto.getEmail());
        String sujet   = esc(dto.getSujet() != null ? dto.getSujet() : "—");
        String message = esc(dto.getMessage()).replace("\n", "<br>");

        return """
                <!doctype html>
                <html lang="fr">
                <body style="margin:0;padding:24px 0;background:#f5f1e8;font-family:Arial,'Helvetica Neue',sans-serif;color:#16211d;">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0">
                    <tr><td align="center" style="padding:0 16px;">
                      <table role="presentation" width="620" style="width:620px;max-width:620px;background:#fff;border:1px solid #e8dfd0;border-radius:22px;overflow:hidden;">
                        <tr>
                          <td style="padding:28px 32px;background:linear-gradient(135deg,#145746,#0f766e);color:#fff;">
                            <div style="font-size:11px;letter-spacing:0.18em;text-transform:uppercase;opacity:.8;font-weight:700;">Benin Explo</div>
                            <h1 style="margin:12px 0 6px;font-size:22px;font-weight:800;">Nouveau message de contact</h1>
                            <p style="margin:0;font-size:14px;opacity:.85;">Recu depuis le formulaire du site</p>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:24px 32px;">
                            <table width="100%%" style="border-collapse:separate;border-spacing:0;background:#faf8f4;border:1px solid #ece4d8;border-radius:14px;overflow:hidden;">
                              <tr style="background:#f1eadf;">
                                <td colspan="2" style="padding:14px 18px;font-size:14px;font-weight:800;color:#1d2a24;">Coordonnees de l expediteur</td>
                              </tr>
                              <tr>
                                <td style="padding:12px 18px;border-top:1px solid #ece4d8;font-size:13px;color:#6a756f;width:38%%;">Nom</td>
                                <td style="padding:12px 18px;border-top:1px solid #ece4d8;font-size:13px;font-weight:700;">%s</td>
                              </tr>
                              <tr>
                                <td style="padding:12px 18px;border-top:1px solid #ece4d8;font-size:13px;color:#6a756f;">Email</td>
                                <td style="padding:12px 18px;border-top:1px solid #ece4d8;font-size:13px;font-weight:700;"><a href="mailto:%s" style="color:#145746;">%s</a></td>
                              </tr>
                              <tr>
                                <td style="padding:12px 18px;border-top:1px solid #ece4d8;font-size:13px;color:#6a756f;">Sujet</td>
                                <td style="padding:12px 18px;border-top:1px solid #ece4d8;font-size:13px;font-weight:700;">%s</td>
                              </tr>
                            </table>
                            <div style="margin-top:18px;padding:18px;background:#f6f7f9;border:1px solid #e2e8f0;border-radius:14px;">
                              <div style="font-size:13px;font-weight:800;color:#145746;margin-bottom:10px;">Message</div>
                              <div style="font-size:14px;line-height:1.75;color:#2d3d38;">%s</div>
                            </div>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:16px 32px 28px;border-top:1px solid #ece4d8;">
                            <p style="margin:0;font-size:12px;color:#9aa5a0;">Pour repondre, cliquez sur "Repondre" dans votre client mail — la reponse ira directement a %s</p>
                          </td>
                        </tr>
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(nom, email, email, sujet, message, email);
    }

    private String buildPlainConfirmation(ContactMessageDTO dto) {
        return """
                Bonjour %s,

                Nous avons bien recu votre message et vous repondrons dans les meilleurs delais (generalement sous 24h).

                Recapitulatif de votre message :
                Sujet   : %s
                Message : %s

                Merci de votre confiance,
                L'equipe Benin Explo
                """.formatted(
                safe(dto.getNom()),
                safe(dto.getSujet()),
                safe(dto.getMessage())
        );
    }

    private String buildHtmlConfirmation(ContactMessageDTO dto) {
        String nom     = esc(dto.getNom());
        String sujet   = esc(dto.getSujet() != null ? dto.getSujet() : "—");
        String message = esc(dto.getMessage()).replace("\n", "<br>");

        return """
                <!doctype html>
                <html lang="fr">
                <body style="margin:0;padding:24px 0;background:#f5f1e8;font-family:Arial,'Helvetica Neue',sans-serif;color:#16211d;">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0">
                    <tr><td align="center" style="padding:0 16px;">
                      <table role="presentation" width="620" style="width:620px;max-width:620px;background:#fff;border:1px solid #e8dfd0;border-radius:22px;overflow:hidden;">
                        <tr>
                          <td style="padding:28px 32px;background:linear-gradient(135deg,#145746,#0f766e);color:#fff;">
                            <div style="font-size:11px;letter-spacing:0.18em;text-transform:uppercase;opacity:.8;font-weight:700;">Benin Explo</div>
                            <h1 style="margin:12px 0 6px;font-size:22px;font-weight:800;">Message bien recu !</h1>
                            <p style="margin:0;font-size:14px;opacity:.85;">Nous vous repondrons sous 24h</p>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:24px 32px;">
                            <p style="margin:0 0 18px;font-size:15px;line-height:1.7;color:#34423c;">
                              Bonjour <strong>%s</strong>,<br>
                              votre message a bien ete transmis a notre equipe.
                            </p>
                            <table width="100%%" style="border-collapse:separate;border-spacing:0;background:#faf8f4;border:1px solid #ece4d8;border-radius:14px;overflow:hidden;">
                              <tr style="background:#f1eadf;">
                                <td colspan="2" style="padding:14px 18px;font-size:14px;font-weight:800;color:#1d2a24;">Recapitulatif</td>
                              </tr>
                              <tr>
                                <td style="padding:12px 18px;border-top:1px solid #ece4d8;font-size:13px;color:#6a756f;width:38%%;">Sujet</td>
                                <td style="padding:12px 18px;border-top:1px solid #ece4d8;font-size:13px;font-weight:700;">%s</td>
                              </tr>
                              <tr>
                                <td style="padding:12px 18px;border-top:1px solid #ece4d8;font-size:13px;color:#6a756f;">Message</td>
                                <td style="padding:12px 18px;border-top:1px solid #ece4d8;font-size:13px;line-height:1.65;">%s</td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:16px 32px 28px;border-top:1px solid #ece4d8;">
                            <p style="margin:0;font-size:13px;color:#6a756f;">Merci pour votre confiance.<br>L'equipe Benin Explo</p>
                          </td>
                        </tr>
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(nom, sujet, message);
    }

    private String safe(String v) {
        return v != null && !v.isBlank() ? v.trim() : "—";
    }

    private String esc(String v) {
        return HtmlUtils.htmlEscape(safe(v));
    }
}
