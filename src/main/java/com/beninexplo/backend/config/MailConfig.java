package com.beninexplo.backend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender() {
        // Fournit un JavaMailSender minimal pour éviter l'echec de demarrage
        // lorsque la configuration mail n'est pas presente en dev.
        return new JavaMailSenderImpl();
    }
}
