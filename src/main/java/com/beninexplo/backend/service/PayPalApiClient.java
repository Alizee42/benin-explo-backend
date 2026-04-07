package com.beninexplo.backend.service;

import com.beninexplo.backend.config.PayPalProperties;
import com.beninexplo.backend.exception.BadRequestException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
public class PayPalApiClient {

    private static final Logger log = LoggerFactory.getLogger(PayPalApiClient.class);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    private final ObjectMapper objectMapper;
    private final PayPalProperties payPalProperties;

    private volatile String cachedAccessToken;
    private volatile Instant cachedAccessTokenExpiry = Instant.EPOCH;

    public PayPalApiClient(ObjectMapper objectMapper, PayPalProperties payPalProperties) {
        this.objectMapper = objectMapper;
        this.payPalProperties = payPalProperties;
    }

    public JsonNode createOrder(BigDecimal amount,
                                String description,
                                String referenceId,
                                String customId,
                                String returnUrl,
                                String cancelUrl,
                                String requestId) {
        ensurePayPalReady();

        ObjectNode amountNode = objectMapper.createObjectNode();
        amountNode.put("currency_code", payPalProperties.getCurrency());
        amountNode.put("value", normalizeAmount(amount));

        ObjectNode purchaseUnit = objectMapper.createObjectNode();
        purchaseUnit.put("reference_id", referenceId);
        purchaseUnit.put("description", description);
        purchaseUnit.put("custom_id", customId);
        purchaseUnit.set("amount", amountNode);

        ArrayNode purchaseUnits = objectMapper.createArrayNode();
        purchaseUnits.add(purchaseUnit);

        ObjectNode experienceContext = objectMapper.createObjectNode();
        experienceContext.put("brand_name", payPalProperties.getBrandName());
        experienceContext.put("locale", payPalProperties.getLocale());
        experienceContext.put("landing_page", "NO_PREFERENCE");
        experienceContext.put("shipping_preference", "NO_SHIPPING");
        if (StringUtils.hasText(returnUrl)) {
            experienceContext.put("return_url", returnUrl.trim());
        }
        if (StringUtils.hasText(cancelUrl)) {
            experienceContext.put("cancel_url", cancelUrl.trim());
        }

        ObjectNode paypalNode = objectMapper.createObjectNode();
        paypalNode.set("experience_context", experienceContext);

        ObjectNode paymentSource = objectMapper.createObjectNode();
        paymentSource.set("paypal", paypalNode);

        ObjectNode body = objectMapper.createObjectNode();
        body.put("intent", "CAPTURE");
        body.set("purchase_units", purchaseUnits);
        body.set("payment_source", paymentSource);

        return sendJsonRequest("/v2/checkout/orders", "POST", body, requestId);
    }

    public JsonNode captureOrder(String orderId, String requestId) {
        ensurePayPalReady();
        return sendJsonRequest("/v2/checkout/orders/" + orderId + "/capture", "POST", objectMapper.createObjectNode(), requestId);
    }

    private JsonNode sendJsonRequest(String path, String method, JsonNode body, String requestId) {
        try {
            log.info("PayPal request start: method={}, path={}, requestId={}, sandbox={}, currency={}, locale={}, body={}",
                    method, path, requestId, payPalProperties.isSandbox(), payPalProperties.getCurrency(),
                    payPalProperties.getLocale(), summarize(body));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(payPalProperties.getBaseUrl() + path))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + getAccessToken())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("PayPal-Request-Id", requestId)
                    .method(method, HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String rawBody = response.body();
                log.error("PayPal API error: method={}, path={}, requestId={}, status={}, debugId={}, name={}, message={}, details={}, requestBody={}, responseBody={}",
                        method,
                        path,
                        requestId,
                        response.statusCode(),
                        extractPayPalDebugId(rawBody),
                        extractPayPalName(rawBody),
                        extractPayPalMessage(rawBody),
                        extractPayPalDetails(rawBody),
                        summarize(body),
                        truncate(rawBody));
                throw new BadRequestException(buildPublicPayPalErrorMessage(
                        rawBody,
                        "Impossible de communiquer avec PayPal pour le moment."
                ));
            }
            JsonNode successBody = objectMapper.readTree(response.body());
            log.info("PayPal request success: method={}, path={}, requestId={}, status={}, paypalStatus={}, resourceId={}",
                    method,
                    path,
                    requestId,
                    response.statusCode(),
                    successBody.path("status").asText(""),
                    successBody.path("id").asText(""));
            return successBody;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("PayPal request interrupted: method={}, path={}, requestId={}", method, path, requestId, ex);
            throw new BadRequestException("Le paiement PayPal est temporairement indisponible.");
        } catch (IOException ex) {
            log.error("PayPal request failed: method={}, path={}, requestId={}", method, path, requestId, ex);
            log.error("PayPal request failed on {} {}", method, path, ex);
            throw new BadRequestException("Le paiement PayPal est temporairement indisponible.");
        }
    }

    private synchronized String getAccessToken() {
        if (cachedAccessToken != null && Instant.now().isBefore(cachedAccessTokenExpiry)) {
            return cachedAccessToken;
        }

        ensurePayPalReady();

        try {
            String basicAuth = Base64.getEncoder().encodeToString(
                    (payPalProperties.getClientId() + ":" + payPalProperties.getClientSecret())
                            .getBytes(StandardCharsets.UTF_8)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(payPalProperties.getBaseUrl() + "/v1/oauth2/token"))
                    .timeout(Duration.ofSeconds(20))
                    .header("Authorization", "Basic " + basicAuth)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=" +
                            URLEncoder.encode("client_credentials", StandardCharsets.UTF_8)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.warn("Unable to obtain PayPal access token: status={}, debugId={}, name={}, message={}, details={}",
                        response.statusCode(),
                        extractPayPalDebugId(response.body()),
                        extractPayPalName(response.body()),
                        extractPayPalMessage(response.body()),
                        extractPayPalDetails(response.body()));
                throw new BadRequestException("Configuration PayPal invalide.");
            }

            JsonNode body = objectMapper.readTree(response.body());
            String accessToken = body.path("access_token").asText(null);
            long expiresIn = body.path("expires_in").asLong(0L);
            if (!StringUtils.hasText(accessToken) || expiresIn <= 0) {
                throw new BadRequestException("PayPal n'a pas retourne de jeton exploitable.");
            }

            cachedAccessToken = accessToken;
            cachedAccessTokenExpiry = Instant.now().plusSeconds(Math.max(60L, expiresIn - 60L));
            log.info("PayPal access token refreshed successfully. ExpiresIn={}s, sandbox={}, currency={}, locale={}",
                    expiresIn, payPalProperties.isSandbox(), payPalProperties.getCurrency(), payPalProperties.getLocale());
            return cachedAccessToken;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Unable to obtain PayPal access token", ex);
            throw new BadRequestException("Le paiement PayPal est temporairement indisponible.");
        } catch (IOException ex) {
            log.error("Unable to obtain PayPal access token", ex);
            throw new BadRequestException("Le paiement PayPal est temporairement indisponible.");
        }
    }

    private String normalizeAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BadRequestException("Le montant PayPal doit etre superieur a zero.");
        }
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private void ensurePayPalReady() {
        if (!payPalProperties.isReady()) {
            throw new BadRequestException("Le paiement PayPal n'est pas configure.");
        }
    }

    private String extractPayPalErrorMessage(String rawBody, String fallback) {
        if (!StringUtils.hasText(rawBody)) {
            return fallback;
        }
        try {
            JsonNode errorNode = objectMapper.readTree(rawBody);
            JsonNode detailsNode = errorNode.path("details");
            if (detailsNode.isArray() && !detailsNode.isEmpty()) {
                String issue = detailsNode.get(0).path("issue").asText("");
                String description = detailsNode.get(0).path("description").asText("");
                String combined = (issue + " " + description).trim();
                if (StringUtils.hasText(combined)) {
                    return combined;
                }
            }
            String message = errorNode.path("message").asText("");
            if (StringUtils.hasText(message)) {
                return message;
            }
        } catch (IOException ex) {
            log.debug("Unable to parse PayPal error payload: {}", rawBody, ex);
        }
        return fallback;
    }

    private String buildPublicPayPalErrorMessage(String rawBody, String fallback) {
        String message = extractPayPalErrorMessage(rawBody, fallback);
        String debugId = extractPayPalDebugId(rawBody);
        if (StringUtils.hasText(debugId)) {
            return message + " [PayPal debug_id=" + debugId + "]";
        }
        return message;
    }

    private String extractPayPalDebugId(String rawBody) {
        return extractPayPalField(rawBody, "debug_id");
    }

    private String extractPayPalName(String rawBody) {
        return extractPayPalField(rawBody, "name");
    }

    private String extractPayPalMessage(String rawBody) {
        return extractPayPalField(rawBody, "message");
    }

    private String extractPayPalField(String rawBody, String fieldName) {
        if (!StringUtils.hasText(rawBody)) {
            return "";
        }
        try {
            return objectMapper.readTree(rawBody).path(fieldName).asText("");
        } catch (IOException ex) {
            log.debug("Unable to extract PayPal field {} from payload", fieldName, ex);
            return "";
        }
    }

    private String extractPayPalDetails(String rawBody) {
        if (!StringUtils.hasText(rawBody)) {
            return "";
        }
        try {
            JsonNode detailsNode = objectMapper.readTree(rawBody).path("details");
            return truncate(detailsNode.isMissingNode() ? "" : detailsNode.toString());
        } catch (IOException ex) {
            log.debug("Unable to extract PayPal details from payload", ex);
            return "";
        }
    }

    private String summarize(JsonNode node) {
        if (node == null || node.isNull()) {
            return "";
        }
        return truncate(node.toString());
    }

    private String truncate(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        String normalized = value.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= 1200) {
            return normalized;
        }
        return normalized.substring(0, 1200) + "...";
    }
}
