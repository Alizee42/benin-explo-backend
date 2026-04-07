package com.beninexplo.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@ConfigurationProperties(prefix = "paypal")
public class PayPalProperties {

    private static final Pattern PAYPAL_LOCALE_PATTERN =
            Pattern.compile("^[a-z]{2}(?:-[A-Z][a-z]{3})?(?:-(?:[A-Z]{2}))?$");

    private boolean enabled;
    private boolean sandbox = true;
    private String clientId;
    private String clientSecret;
    private String currency = "EUR";
    private String brandName = "Benin Explo";
    private String locale = "fr-FR";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public void setSandbox(boolean sandbox) {
        this.sandbox = sandbox;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getLocale() {
        if (!StringUtils.hasText(locale)) {
            return "fr-FR";
        }

        String normalized = locale.trim().replace('_', '-');
        if (PAYPAL_LOCALE_PATTERN.matcher(normalized).matches()) {
            return normalized;
        }

        return "fr-FR";
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getBaseUrl() {
        return sandbox
                ? "https://api-m.sandbox.paypal.com"
                : "https://api-m.paypal.com";
    }

    public boolean isReady() {
        return enabled
                && StringUtils.hasText(clientId)
                && StringUtils.hasText(clientSecret)
                && StringUtils.hasText(currency);
    }
}
