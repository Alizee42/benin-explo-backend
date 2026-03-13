package com.beninexplo.backend.config;

import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.enabled:false}")
    private boolean adminBootstrapEnabled;

    @Value("${app.bootstrap.admin.email:}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password:}")
    private String adminPassword;

    public DataInitializer(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!adminBootstrapEnabled) {
            log.info("Bootstrap administrateur desactive.");
            return;
        }

        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            log.warn("Bootstrap administrateur ignore: email ou mot de passe absent.");
            return;
        }

        Utilisateur existingAdmin = utilisateurRepository.findByEmail(adminEmail).orElse(null);
        if (existingAdmin != null) {
            existingAdmin.setNom("Admin");
            existingAdmin.setPrenom("BeninExplo");
            existingAdmin.setMotDePasse(passwordEncoder.encode(adminPassword));
            existingAdmin.setRole("ADMIN");
            if (existingAdmin.getDateCreation() == null) {
                existingAdmin.setDateCreation(LocalDateTime.now());
            }
            utilisateurRepository.save(existingAdmin);
            log.info("Bootstrap administrateur mis a jour pour {}", adminEmail);
            return;
        }

        Utilisateur admin = new Utilisateur();
        admin.setNom("Admin");
        admin.setPrenom("BeninExplo");
        admin.setEmail(adminEmail);
        admin.setMotDePasse(passwordEncoder.encode(adminPassword));
        admin.setRole("ADMIN");
        admin.setDateCreation(LocalDateTime.now());

        utilisateurRepository.save(admin);
        log.info("Administrateur bootstrap cree pour {}", adminEmail);
    }
}
