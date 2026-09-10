package com.beninexplo.backend.service;

import com.beninexplo.backend.entity.CircuitPersonnalise;
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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Notifie le client par email quand l'admin accepte ou refuse sa demande de circuit
 * personnalise. Bug trouve en audit : CircuitPersonnaliseService.updateStatut() changeait le
 * statut sans jamais notifier le client, malgre les champs commentaireAdmin/motifRefus deja
 * prevus dans le workflow. Meme pattern que ReservationHebergementNotificationService.
 */
@Service
public class CircuitPersonnaliseNotificationService {

    private static final Logger log = LoggerFactory.getLogger(CircuitPersonnaliseNotificationService.class);
    private static final Locale FR_LOCALE = Locale.FRANCE;

    private final JavaMailSender mailSender;

    @Value("${reservation.notifications.circuit-personnalise.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${reservation.notifications.mail.from:no-reply@beninexplo.com}")
    private String fromAddress;

    @Value("${app.frontend-url:https://frontend-benin-explo.netlify.app}")
    private String frontendUrl;

    public CircuitPersonnaliseNotificationService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSender = mailSenderProvider.getIfAvailable();
    }

    @Async
    public void sendStatutUpdate(CircuitPersonnalise demande) {
        CircuitPersonnalise.StatutDemande statut = demande.getStatut();
        if (statut != CircuitPersonnalise.StatutDemande.ACCEPTE && statut != CircuitPersonnalise.StatutDemande.REFUSE) {
            return;
        }

        String subject = statut == CircuitPersonnalise.StatutDemande.ACCEPTE
                ? "Votre devis de circuit personnalise a ete accepte"
                : "Votre devis de circuit personnalise n'a pas pu etre retenu";
        String plainTextBody = buildPlainText(demande);
        String htmlBody = buildHtml(demande);

        sendEmail(demande, subject, plainTextBody, htmlBody);
    }

    // Rappel envoye 7 jours apres acceptation si le devis n'est toujours pas paye (voir
    // CircuitPersonnaliseExpirationJob). Un seul rappel par demande (marque via
    // dateRappelPaiementEnvoye cote appelant).
    @Async
    public void sendPaiementRappel(CircuitPersonnalise demande) {
        String subject = "Votre devis de circuit personnalise vous attend";
        String paymentUrl = buildPaiementUrl(demande.getId());

        String plainText = "Bonjour " + defaultText(demande.getPrenomClient(), "client") + ",\n\n"
                + "Votre devis de circuit personnalise (" + formatMontant(demande.getPrixFinal(), demande.getDevisePrixEstime())
                + ") est accepte et vous attend depuis quelques jours.\n\n"
                + "Pour confirmer votre circuit, finalisez le paiement ici : " + paymentUrl + "\n\n"
                + "Passe un delai de 14 jours sans paiement, ce devis expirera automatiquement.\n\n"
                + "Merci,\nL'equipe Benin Explo";

        String htmlBody = buildSimpleNoticeHtml(
                "Votre devis vous attend",
                "Votre devis de circuit personnalise est accepte et vous attend depuis quelques jours.",
                "<a href=\"" + escapeHtml(paymentUrl) + "\" style=\"color:#145746;font-weight:700;\">Finaliser le paiement</a>"
                        + " avant l'expiration du devis (14 jours apres acceptation).",
                defaultText(demande.getPrenomClient(), "client")
        );

        sendEmail(demande, subject, plainText, htmlBody);
    }

    // Notifie le client que son devis accepte a expire faute de paiement sous 14 jours.
    @Async
    public void sendExpiration(CircuitPersonnalise demande) {
        String subject = "Votre devis de circuit personnalise a expire";
        String circuitPersonnaliseUrl = buildCircuitPersonnaliseUrl();

        String plainText = "Bonjour " + defaultText(demande.getPrenomClient(), "client") + ",\n\n"
                + "Votre devis de circuit personnalise accepte n'a pas ete regle dans le delai de 14 jours et a expire.\n\n"
                + "Vous pouvez soumettre une nouvelle demande a tout moment : " + circuitPersonnaliseUrl + "\n\n"
                + "Merci,\nL'equipe Benin Explo";

        String htmlBody = buildSimpleNoticeHtml(
                "Devis expire",
                "Votre devis de circuit personnalise accepte n'a pas ete regle dans le delai de 14 jours et a expire.",
                "Vous pouvez soumettre une nouvelle demande a tout moment : <a href=\"" + escapeHtml(circuitPersonnaliseUrl)
                        + "\" style=\"color:#145746;font-weight:700;\">composer un nouveau circuit personnalise</a>.",
                defaultText(demande.getPrenomClient(), "client")
        );

        sendEmail(demande, subject, plainText, htmlBody);
    }

    private String buildSimpleNoticeHtml(String heading, String intro, String noteBodyHtml, String clientName) {
        String template = """
                <!doctype html>
                <html lang="fr">
                  <body style="margin:0;padding:24px 0;background:#f5f1e8;color:#16211d;font-family:Arial,'Helvetica Neue',sans-serif;">
                    <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="border-collapse:collapse;background:#f5f1e8;">
                      <tr>
                        <td align="center" style="padding:0 16px;">
                          <table role="presentation" width="680" cellpadding="0" cellspacing="0" style="width:680px;max-width:680px;border-collapse:separate;border-spacing:0;background:#ffffff;border:1px solid #e8dfd0;border-radius:22px;overflow:hidden;">
                            <tr>
                              <td style="padding:34px 36px;background:#145746;color:#ffffff;">
                                <div style="font-size:12px;letter-spacing:0.18em;text-transform:uppercase;opacity:0.8;font-weight:700;">Benin Explo</div>
                                <h1 style="margin:16px 0 10px;font-size:28px;line-height:1.2;font-weight:800;">{{heading}}</h1>
                                <p style="margin:0;font-size:15px;line-height:1.7;color:#e7f4ef;">{{intro}}</p>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:28px 36px 34px;">
                                <p style="margin:0 0 18px;font-size:15px;line-height:1.7;color:#34423c;">
                                  Bonjour <strong>{{clientName}}</strong>,
                                </p>
                                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="border-collapse:separate;border-spacing:0;background:#eef7f4;border:1px solid #d5ebe3;border-radius:18px;overflow:hidden;">
                                  <tr>
                                    <td style="padding:18px 20px;font-size:14px;line-height:1.7;color:#355046;">{{noteBody}}</td>
                                  </tr>
                                </table>
                                <div style="font-size:13px;line-height:1.7;color:#6a756f;border-top:1px solid #ece4d8;padding-top:18px;margin-top:18px;">
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
                """;

        return template
                .replace("{{heading}}", escapeHtml(heading))
                .replace("{{intro}}", escapeHtml(intro))
                .replace("{{clientName}}", escapeHtml(clientName))
                .replace("{{noteBody}}", noteBodyHtml);
    }

    private String buildPaiementUrl(Long demandeId) {
        String base = hasText(frontendUrl) ? frontendUrl.trim() : "https://frontend-benin-explo.netlify.app";
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/paiement/circuit-personnalise/" + demandeId;
    }

    private void sendEmail(CircuitPersonnalise demande, String subject, String plainTextBody, String htmlBody) {
        if (!mailEnabled) {
            log.info("Notification email circuit personnalise desactivee. Demande id={}, sujet={}", demande.getId(), subject);
            return;
        }

        if (mailSender == null) {
            log.warn(
                    "JavaMailSender indisponible. Notification email ignoree pour demande id={}. Verifier la configuration spring.mail.* chargee au demarrage.",
                    demande.getId()
            );
            return;
        }

        if (demande.getEmailClient() == null || demande.getEmailClient().isBlank()) {
            log.warn("Aucun email client renseigne pour la demande id={}, notification ignoree.", demande.getId());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(demande.getEmailClient());
            helper.setSubject(subject);
            helper.setText(plainTextBody, htmlBody);
            mailSender.send(message);
            log.info("Email de statut circuit personnalise envoye pour demande id={}", demande.getId());
        } catch (Exception e) {
            // Ne jamais casser le flux metier (changement de statut) si l'envoi d'email echoue.
            log.warn("Envoi email impossible pour demande circuit personnalise id={}: {}", demande.getId(), e.getMessage());
        }
    }

    private String buildPlainText(CircuitPersonnalise demande) {
        boolean accepte = demande.getStatut() == CircuitPersonnalise.StatutDemande.ACCEPTE;
        StringBuilder builder = new StringBuilder();
        builder.append("Bonjour ").append(defaultText(demande.getPrenomClient(), "client")).append(",\n\n");

        if (accepte) {
            builder.append("Bonne nouvelle : votre devis de circuit personnalise a ete accepte.\n\n");
            builder.append("Nombre de jours: ").append(demande.getNombreJours()).append("\n");
            builder.append("Nombre de personnes: ").append(demande.getNombrePersonnes()).append("\n");
            builder.append("Prix final: ").append(formatMontant(demande.getPrixFinal(), demande.getDevisePrixEstime())).append("\n");
        } else {
            builder.append("Votre demande de circuit personnalise n'a malheureusement pas pu etre retenue.\n\n");
            if (hasText(demande.getMotifRefus())) {
                builder.append("Motif: ").append(demande.getMotifRefus().trim()).append("\n");
            }
        }

        if (hasText(demande.getCommentaireAdmin())) {
            builder.append("Commentaire de notre equipe: ").append(demande.getCommentaireAdmin().trim()).append("\n");
        }

        builder.append("\n");
        if (accepte) {
            builder.append("Vous pouvez maintenant proceder au paiement depuis votre espace client pour confirmer ce circuit.\n\n");
        } else {
            builder.append("N'hesitez pas a soumettre une nouvelle demande ajustee : ")
                    .append(buildCircuitPersonnaliseUrl()).append("\n\n");
        }
        builder.append("Merci,\n");
        builder.append("L'equipe Benin Explo");
        return builder.toString();
    }

    private String buildHtml(CircuitPersonnalise demande) {
        boolean accepte = demande.getStatut() == CircuitPersonnalise.StatutDemande.ACCEPTE;
        String heading = accepte ? "Devis accepte" : "Devis non retenu";
        String intro = accepte
                ? "Votre devis de circuit personnalise a ete accepte par notre equipe."
                : "Votre demande de circuit personnalise n'a malheureusement pas pu etre retenue.";
        String statusLabel = accepte ? "Accepte" : "Refuse";
        String statusBg = accepte ? "#def4ea" : "#fde8e7";
        String statusText = accepte ? "#145746" : "#a53328";
        String noteTitle = accepte ? "Prochaine etape" : "Envie de reessayer ?";
        String noteBody = accepte
                ? "Vous pouvez proceder au paiement depuis votre espace client pour confirmer ce circuit."
                : "N'hesitez pas a soumettre une nouvelle demande ajustee : <a href=\"" + escapeHtml(buildCircuitPersonnaliseUrl())
                        + "\" style=\"color:#145746;font-weight:700;\">composer un nouveau circuit personnalise</a>.";

        String template = """
                <!doctype html>
                <html lang="fr">
                  <body style="margin:0;padding:24px 0;background:#f5f1e8;color:#16211d;font-family:Arial,'Helvetica Neue',sans-serif;">
                    <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="border-collapse:collapse;background:#f5f1e8;">
                      <tr>
                        <td align="center" style="padding:0 16px;">
                          <table role="presentation" width="680" cellpadding="0" cellspacing="0" style="width:680px;max-width:680px;border-collapse:separate;border-spacing:0;background:#ffffff;border:1px solid #e8dfd0;border-radius:22px;overflow:hidden;">
                            <tr>
                              <td style="padding:34px 36px;background:#145746;color:#ffffff;">
                                <div style="font-size:12px;letter-spacing:0.18em;text-transform:uppercase;opacity:0.8;font-weight:700;">Benin Explo</div>
                                <h1 style="margin:16px 0 10px;font-size:28px;line-height:1.2;font-weight:800;">{{heading}}</h1>
                                <p style="margin:0;font-size:15px;line-height:1.7;color:#e7f4ef;">{{intro}}</p>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:28px 36px 10px;">
                                <p style="margin:0 0 18px;font-size:15px;line-height:1.7;color:#34423c;">
                                  Bonjour <strong>{{clientName}}</strong>,
                                </p>
                                <table role="presentation" cellpadding="0" cellspacing="0" style="border-collapse:collapse;margin-bottom:20px;">
                                  <tr>
                                    <td style="background:{{statusBg}};color:{{statusText}};padding:10px 16px;border-radius:999px;font-size:13px;font-weight:800;letter-spacing:0.03em;">
                                      Statut: {{statusLabel}}
                                    </td>
                                  </tr>
                                </table>
                                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="border-collapse:separate;border-spacing:0;background:#faf8f4;border:1px solid #ece4d8;border-radius:18px;overflow:hidden;">
                                  <tr>
                                    <td colspan="2" style="padding:18px 20px;background:#f1eadf;font-size:15px;font-weight:800;color:#1d2a24;">
                                      Recapitulatif de la demande
                                    </td>
                                  </tr>
                                  {{summaryRows}}
                                </table>
                                {{commentBlock}}
                                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="border-collapse:separate;border-spacing:0;margin-top:18px;background:#eef7f4;border:1px solid #d5ebe3;border-radius:18px;overflow:hidden;">
                                  <tr>
                                    <td style="padding:18px 20px;">
                                      <div style="font-size:14px;font-weight:800;color:#145746;margin-bottom:8px;">{{noteTitle}}</div>
                                      <div style="font-size:14px;line-height:1.7;color:#355046;">{{noteBody}}</div>
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
                """;

        return template
                .replace("{{heading}}", escapeHtml(heading))
                .replace("{{intro}}", escapeHtml(intro))
                .replace("{{clientName}}", escapeHtml(defaultText(demande.getPrenomClient(), "client")))
                .replace("{{statusLabel}}", escapeHtml(statusLabel))
                .replace("{{statusBg}}", statusBg)
                .replace("{{statusText}}", statusText)
                .replace("{{summaryRows}}", buildSummaryRows(demande, accepte))
                .replace("{{commentBlock}}", buildCommentBlock(demande, accepte))
                .replace("{{noteTitle}}", escapeHtml(noteTitle))
                // noteBody contient volontairement du HTML de confiance (lien <a> genere par
                // buildCircuitPersonnaliseUrl(), pas d'input utilisateur) pour le cas refuse -
                // ne pas ré-échapper ici, contrairement aux autres champs de ce template.
                .replace("{{noteBody}}", noteBody);
    }

    private String buildSummaryRows(CircuitPersonnalise demande, boolean accepte) {
        StringBuilder rows = new StringBuilder();
        rows.append(buildSummaryRow("Nombre de jours", String.valueOf(demande.getNombreJours())));
        rows.append(buildSummaryRow("Nombre de personnes", String.valueOf(demande.getNombrePersonnes())));
        if (accepte) {
            rows.append(buildSummaryRow("Prix final", formatMontant(demande.getPrixFinal(), demande.getDevisePrixEstime())));
        }
        return rows.toString();
    }

    private String buildSummaryRow(String label, String value) {
        return """
                <tr>
                  <td style="padding:14px 20px;border-top:1px solid #ece4d8;font-size:14px;color:#6a756f;width:44%%;">%s</td>
                  <td style="padding:14px 20px;border-top:1px solid #ece4d8;font-size:14px;font-weight:700;color:#16211d;">%s</td>
                </tr>
                """.formatted(escapeHtml(label), escapeHtml(value));
    }

    private String buildCommentBlock(CircuitPersonnalise demande, boolean accepte) {
        String text = accepte ? null : demande.getMotifRefus();
        String comment = demande.getCommentaireAdmin();
        if (!hasText(text) && !hasText(comment)) {
            return "";
        }

        StringBuilder body = new StringBuilder();
        if (hasText(text)) {
            body.append(escapeHtml(text.trim()));
        }
        if (hasText(comment)) {
            if (body.length() > 0) {
                body.append("<br><br>");
            }
            body.append(escapeHtml(comment.trim()));
        }

        return """
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="border-collapse:separate;border-spacing:0;margin-top:18px;background:#fffaf2;border:1px solid #f0e1c6;border-radius:18px;overflow:hidden;">
                  <tr>
                    <td style="padding:18px 20px;">
                      <div style="font-size:14px;font-weight:800;color:#8a6512;margin-bottom:8px;">Message de notre equipe</div>
                      <div style="font-size:14px;line-height:1.7;color:#5c4a22;">%s</div>
                    </td>
                  </tr>
                </table>
                """.formatted(body);
    }

    private String formatMontant(BigDecimal amount, String devise) {
        BigDecimal value = amount != null ? amount : BigDecimal.ZERO;
        if ("XOF".equalsIgnoreCase(devise)) {
            NumberFormat formatter = NumberFormat.getNumberInstance(FR_LOCALE);
            formatter.setMaximumFractionDigits(0);
            return formatter.format(value) + " F CFA";
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(FR_LOCALE);
        return formatter.format(value);
    }

    private String buildCircuitPersonnaliseUrl() {
        String base = hasText(frontendUrl) ? frontendUrl.trim() : "https://frontend-benin-explo.netlify.app";
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/circuit-personnalise";
    }

    private String defaultText(String value, String fallback) {
        return hasText(value) ? value.trim() : fallback;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String escapeHtml(String value) {
        return HtmlUtils.htmlEscape(value != null ? value : "-");
    }
}
