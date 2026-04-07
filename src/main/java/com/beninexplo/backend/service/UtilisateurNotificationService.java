package com.beninexplo.backend.service;

import com.beninexplo.backend.entity.Utilisateur;
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
public class UtilisateurNotificationService {

    private static final Logger log = LoggerFactory.getLogger(UtilisateurNotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${utilisateur.notifications.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${reservation.notifications.mail.from:no-reply@beninexplo.com}")
    private String fromAddress;

    public UtilisateurNotificationService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSender = mailSenderProvider.getIfAvailable();
    }

    @Async
    public void sendWelcomeEmail(Utilisateur utilisateur) {
        if (!mailEnabled) {
            log.info("Email de bienvenue desactive pour utilisateur id={}", utilisateur.getId());
            return;
        }

        if (mailSender == null) {
            log.warn("JavaMailSender indisponible. Email de bienvenue ignore pour utilisateur id={}", utilisateur.getId());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(utilisateur.getEmail());
            helper.setSubject("Bienvenue sur Benin Explo");
            helper.setText(buildPlainText(utilisateur), buildHtml(utilisateur));
            mailSender.send(message);
            log.info("Email de bienvenue envoye pour utilisateur id={}", utilisateur.getId());
        } catch (Exception e) {
            log.warn("Envoi de l'email de bienvenue impossible pour utilisateur id={}: {}", utilisateur.getId(), e.getMessage());
        }
    }

    private String buildPlainText(Utilisateur utilisateur) {
        String prenom = defaultText(utilisateur.getPrenom(), "voyageur");

        return """
                Bonjour %s,

                Votre compte Benin Explo a bien ete cree.

                Vous pouvez maintenant :
                - retrouver vos reservations dans votre espace client
                - finaliser plus facilement vos prochains paiements
                - suivre les confirmations de sejour

                Email de connexion : %s

                Merci et a tres bientot,
                L'equipe Benin Explo
                """.formatted(prenom, defaultText(utilisateur.getEmail(), "-"));
    }

    private String buildHtml(Utilisateur utilisateur) {
        String prenom = escapeHtml(defaultText(utilisateur.getPrenom(), "voyageur"));
        String email = escapeHtml(defaultText(utilisateur.getEmail(), "-"));

        return """
                <!doctype html>
                <html lang="fr">
                  <body style="margin:0;padding:24px 0;background:#f5f1e8;color:#16211d;font-family:Arial,'Helvetica Neue',sans-serif;">
                    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="border-collapse:collapse;background:#f5f1e8;">
                      <tr>
                        <td align="center" style="padding:0 16px;">
                          <table role="presentation" width="680" cellpadding="0" cellspacing="0" style="width:680px;max-width:680px;border-collapse:separate;border-spacing:0;background:#ffffff;border:1px solid #e8dfd0;border-radius:22px;overflow:hidden;">
                            <tr>
                              <td style="padding:34px 36px;background:linear-gradient(135deg,#145746 0%%,#0f766e 100%%);color:#ffffff;">
                                <div style="font-size:12px;letter-spacing:0.18em;text-transform:uppercase;opacity:0.8;font-weight:700;">Benin Explo</div>
                                <h1 style="margin:16px 0 10px;font-size:28px;line-height:1.2;font-weight:800;">Bienvenue a bord</h1>
                                <p style="margin:0;font-size:15px;line-height:1.7;color:#e7f4ef;">
                                  Votre espace client est maintenant actif. Vous etes pret a reserver et suivre vos sejours plus facilement.
                                </p>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:28px 36px 10px;">
                                <p style="margin:0 0 18px;font-size:15px;line-height:1.7;color:#34423c;">
                                  Bonjour <strong>%s</strong>,
                                </p>
                                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="border-collapse:separate;border-spacing:0;background:#faf8f4;border:1px solid #ece4d8;border-radius:18px;overflow:hidden;">
                                  <tr>
                                    <td colspan="2" style="padding:18px 20px;background:#f1eadf;font-size:15px;font-weight:800;color:#1d2a24;">
                                      Recapitulatif du compte
                                    </td>
                                  </tr>
                                  <tr>
                                    <td style="padding:14px 20px;border-top:1px solid #ece4d8;font-size:14px;color:#6a756f;width:42%%;">Adresse email</td>
                                    <td style="padding:14px 20px;border-top:1px solid #ece4d8;font-size:14px;font-weight:700;color:#16211d;">%s</td>
                                  </tr>
                                  <tr>
                                    <td style="padding:14px 20px;border-top:1px solid #ece4d8;font-size:14px;color:#6a756f;width:42%%;">Statut</td>
                                    <td style="padding:14px 20px;border-top:1px solid #ece4d8;font-size:14px;font-weight:700;color:#145746;">Compte actif</td>
                                  </tr>
                                </table>
                                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="border-collapse:separate;border-spacing:0;margin-top:18px;background:#eef7f4;border:1px solid #d5ebe3;border-radius:18px;overflow:hidden;">
                                  <tr>
                                    <td style="padding:18px 20px;">
                                      <div style="font-size:14px;font-weight:800;color:#145746;margin-bottom:8px;">Ce que vous pouvez faire maintenant</div>
                                      <div style="font-size:14px;line-height:1.8;color:#355046;">
                                        - retrouver vos reservations depuis votre espace client<br>
                                        - finaliser vos prochains paiements plus rapidement<br>
                                        - conserver la meme adresse email pour tout votre suivi
                                      </div>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:18px 36px 34px;">
                                <div style="font-size:13px;line-height:1.7;color:#6a756f;border-top:1px solid #ece4d8;padding-top:18px;">
                                  Merci pour votre confiance.<br>
                                  L'equipe Benin Explo
                                </div>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """.formatted(prenom, email);
    }

    private String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String escapeHtml(String value) {
        return HtmlUtils.htmlEscape(defaultText(value, ""));
    }
}
