package com.beninexplo.backend.service;

import com.beninexplo.backend.entity.ReservationHebergement;
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
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class ReservationHebergementNotificationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationHebergementNotificationService.class);
    private static final Locale FR_LOCALE = Locale.FRANCE;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final BigDecimal EUR_TO_FCFA = new BigDecimal("655.957");

    private final JavaMailSender mailSender;

    @Value("${reservation.notifications.hebergement.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${reservation.notifications.mail.from:no-reply@beninexplo.com}")
    private String fromAddress;

    public ReservationHebergementNotificationService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSender = mailSenderProvider.getIfAvailable();
    }

    @Async
    public void sendCreationConfirmation(ReservationHebergement reservation) {
        String subject = "Reservation recue - " + reservation.getHebergement().getNom();
        String plainTextBody = buildCreationPlainText(reservation);
        String htmlBody = buildCreationHtml(reservation);
        sendReservationEmail(reservation, subject, plainTextBody, htmlBody);
    }

    @Async
    public void sendStatusUpdate(ReservationHebergement reservation) {
        String statusLabel = getStatusLabel(reservation.getStatut());
        String subject = "Reservation " + statusLabel + " - " + reservation.getHebergement().getNom();
        String plainTextBody = buildStatusUpdatePlainText(reservation);
        String htmlBody = buildStatusUpdateHtml(reservation);
        sendReservationEmail(reservation, subject, plainTextBody, htmlBody);
    }

    private void sendReservationEmail(
            ReservationHebergement reservation,
            String subject,
            String plainTextBody,
            String htmlBody
    ) {
        if (!mailEnabled) {
            log.info("Notification email desactivee. Reservation id={}, sujet={}", reservation.getIdReservation(), subject);
            return;
        }

        if (mailSender == null) {
            log.warn(
                    "JavaMailSender indisponible. Notification email ignoree pour reservation id={}. Verifier la configuration spring.mail.* chargee au demarrage.",
                    reservation.getIdReservation()
            );
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(reservation.getEmailClient());
            helper.setSubject(subject);
            helper.setText(plainTextBody, htmlBody);
            mailSender.send(message);
            log.info("Email de reservation hebergement envoye pour reservation id={}", reservation.getIdReservation());
        } catch (Exception e) {
            // Do not break business flow if mail fails.
            log.warn("Envoi email impossible pour reservation id={}: {}", reservation.getIdReservation(), e.getMessage());
        }
    }

    private String buildCreationPlainText(ReservationHebergement reservation) {
        return buildPlainTextEmail(
                reservation,
                "Votre reservation a bien ete enregistree.",
                "Notre equipe verifie maintenant votre demande et vous recontacte rapidement.",
                "Statut actuel: " + getStatusLabel(reservation.getStatut())
        );
    }

    private String buildStatusUpdatePlainText(ReservationHebergement reservation) {
        return buildPlainTextEmail(
                reservation,
                getStatusHeadline(reservation.getStatut()),
                getStatusDescription(reservation.getStatut()),
                "Nouveau statut: " + getStatusLabel(reservation.getStatut())
        );
    }

    private String buildPlainTextEmail(
            ReservationHebergement reservation,
            String headline,
            String intro,
            String statusLine
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("Bonjour ").append(defaultText(reservation.getPrenomClient(), "client")).append(",\n\n");
        builder.append(headline).append("\n");
        builder.append(intro).append("\n\n");
        builder.append("Hebergement: ").append(reservation.getHebergement().getNom()).append("\n");
        builder.append("Arrivee: ").append(formatDate(reservation.getDateArrivee())).append("\n");
        builder.append("Depart: ").append(formatDate(reservation.getDateDepart())).append("\n");
        builder.append("Nombre de nuits: ").append(reservation.getNombreNuits()).append("\n");
        builder.append("Voyageurs: ").append(reservation.getNombrePersonnes()).append("\n");
        builder.append("Prix estime: ").append(formatEuro(reservation.getPrixTotal()))
                .append(" (")
                .append(formatFcfa(reservation.getPrixTotal()))
                .append(")\n");
        builder.append(statusLine).append("\n");

        if (hasText(reservation.getCommentaires())) {
            builder.append("Commentaires: ").append(reservation.getCommentaires().trim()).append("\n");
        }

        builder.append("\n");
        builder.append("Merci,\n");
        builder.append("L'equipe Benin Explo");
        return builder.toString();
    }

    private String buildCreationHtml(ReservationHebergement reservation) {
        return buildHtmlEmail(
                reservation,
                "Reservation enregistree",
                "Votre demande de reservation a bien ete recue. Notre equipe revient vers vous rapidement pour la validation finale du sejour.",
                "Statut actuel",
                "La reservation est actuellement en attente. Nous verifions les derniers details puis nous vous confirmons le sejour."
        );
    }

    private String buildStatusUpdateHtml(ReservationHebergement reservation) {
        return buildHtmlEmail(
                reservation,
                getStatusHeadline(reservation.getStatut()),
                getStatusDescription(reservation.getStatut()),
                "Mise a jour du dossier",
                getStatusNextStep(reservation.getStatut())
        );
    }

    private String buildHtmlEmail(
            ReservationHebergement reservation,
            String heading,
            String intro,
            String noteTitle,
            String noteBody
    ) {
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
                                      Recapitulatif du sejour
                                    </td>
                                  </tr>
                                  {{summaryRows}}
                                </table>
                                {{commentsBlock}}
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
                .replace("{{clientName}}", escapeHtml(defaultText(reservation.getPrenomClient(), "client")))
                .replace("{{statusLabel}}", escapeHtml(getStatusLabel(reservation.getStatut())))
                .replace("{{statusBg}}", getStatusBackground(reservation.getStatut()))
                .replace("{{statusText}}", getStatusTextColor(reservation.getStatut()))
                .replace("{{summaryRows}}", buildSummaryRows(reservation))
                .replace("{{commentsBlock}}", buildCommentsBlock(reservation))
                .replace("{{noteTitle}}", escapeHtml(noteTitle))
                .replace("{{noteBody}}", escapeHtml(noteBody));
    }

    private String buildSummaryRows(ReservationHebergement reservation) {
        StringBuilder rows = new StringBuilder();
        rows.append(buildSummaryRow("Hebergement", reservation.getHebergement().getNom(), false));
        rows.append(buildSummaryRow("Arrivee", formatDate(reservation.getDateArrivee()), false));
        rows.append(buildSummaryRow("Depart", formatDate(reservation.getDateDepart()), false));
        rows.append(buildSummaryRow("Nombre de nuits", String.valueOf(reservation.getNombreNuits()), false));
        rows.append(buildSummaryRow("Voyageurs", String.valueOf(reservation.getNombrePersonnes()), false));
        rows.append(buildSummaryRow(
                "Prix estime",
                formatEuro(reservation.getPrixTotal()) + " <span style=\"color:#6a756f;font-size:12px;\">(" + escapeHtml(formatFcfa(reservation.getPrixTotal())) + ")</span>",
                true
        ));
        return rows.toString();
    }

    private String buildSummaryRow(String label, String value, boolean valueIsHtml) {
        String renderedValue = valueIsHtml ? value : escapeHtml(value);
        return """
                <tr>
                  <td style="padding:14px 20px;border-top:1px solid #ece4d8;font-size:14px;color:#6a756f;width:44%%;">%s</td>
                  <td style="padding:14px 20px;border-top:1px solid #ece4d8;font-size:14px;font-weight:700;color:#16211d;">%s</td>
                </tr>
                """.formatted(escapeHtml(label), renderedValue);
    }

    private String buildCommentsBlock(ReservationHebergement reservation) {
        if (!hasText(reservation.getCommentaires())) {
            return "";
        }

        return """
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="border-collapse:separate;border-spacing:0;margin-top:18px;background:#fffaf2;border:1px solid #f0e1c6;border-radius:18px;overflow:hidden;">
                  <tr>
                    <td style="padding:18px 20px;">
                      <div style="font-size:14px;font-weight:800;color:#8a6512;margin-bottom:8px;">Commentaires transmis</div>
                      <div style="font-size:14px;line-height:1.7;color:#5c4a22;">%s</div>
                    </td>
                  </tr>
                </table>
                """.formatted(escapeHtml(reservation.getCommentaires().trim()).replace("\n", "<br>"));
    }

    private String getStatusHeadline(String statut) {
        return switch (normalizeStatus(statut)) {
            case "CONFIRMEE" -> "Reservation confirmee";
            case "ANNULEE" -> "Reservation annulee";
            case "TERMINEE" -> "Reservation terminee";
            default -> "Mise a jour de votre reservation";
        };
    }

    private String getStatusDescription(String statut) {
        return switch (normalizeStatus(statut)) {
            case "CONFIRMEE" -> "Bonne nouvelle : votre reservation a ete confirmee par notre equipe.";
            case "ANNULEE" -> "Votre reservation a ete annulee. Si besoin, notre equipe peut vous accompagner pour une nouvelle demande.";
            case "TERMINEE" -> "Votre sejour est maintenant cloture dans notre systeme. Merci pour votre confiance.";
            default -> "Votre dossier reste en cours de verification par notre equipe.";
        };
    }

    private String getStatusNextStep(String statut) {
        return switch (normalizeStatus(statut)) {
            case "CONFIRMEE" -> "Nous vous recommandons de conserver cet email. Si vous avez une demande complementaire, il suffit de repondre a ce message ou de contacter l'agence.";
            case "ANNULEE" -> "Si vous souhaitez repositionner d'autres dates ou un autre hebergement, notre equipe peut vous proposer une nouvelle solution.";
            case "TERMINEE" -> "Nous esperons que le sejour s'est bien passe. Vous pouvez nous recontacter pour une prochaine escapade.";
            default -> "Aucune action n'est necessaire pour le moment. Nous reviendrons vers vous des que la reservation sera validee.";
        };
    }

    private String getStatusLabel(String statut) {
        return switch (normalizeStatus(statut)) {
            case "CONFIRMEE" -> "Confirmee";
            case "ANNULEE" -> "Annulee";
            case "TERMINEE" -> "Terminee";
            default -> "En attente";
        };
    }

    private String getStatusBackground(String statut) {
        return switch (normalizeStatus(statut)) {
            case "CONFIRMEE" -> "#def4ea";
            case "ANNULEE" -> "#fde8e7";
            case "TERMINEE" -> "#e5eefb";
            default -> "#fff3d8";
        };
    }

    private String getStatusTextColor(String statut) {
        return switch (normalizeStatus(statut)) {
            case "CONFIRMEE" -> "#145746";
            case "ANNULEE" -> "#a53328";
            case "TERMINEE" -> "#305ca8";
            default -> "#8a6512";
        };
    }

    private String normalizeStatus(String statut) {
        if (statut == null || statut.isBlank()) {
            return "EN_ATTENTE";
        }
        String normalized = statut.trim().toUpperCase();
        if ("ANNULE".equals(normalized)) {
            return "ANNULEE";
        }
        return normalized;
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "-";
        }
        return DATE_FORMATTER.format(date);
    }

    private String formatEuro(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(FR_LOCALE);
        return formatter.format(amount != null ? amount : BigDecimal.ZERO);
    }

    private String formatFcfa(BigDecimal euroAmount) {
        BigDecimal amount = (euroAmount != null ? euroAmount : BigDecimal.ZERO)
                .multiply(EUR_TO_FCFA)
                .setScale(0, RoundingMode.HALF_UP);
        NumberFormat formatter = NumberFormat.getNumberInstance(FR_LOCALE);
        formatter.setMaximumFractionDigits(0);
        return formatter.format(amount) + " F CFA";
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
