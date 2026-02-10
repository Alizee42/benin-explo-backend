package com.beninexplo.backend.config;

import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Créer un administrateur par défaut si aucun n'existe
        if (utilisateurRepository.findByEmail("admin@beninexplo.com").isEmpty()) {
            Utilisateur admin = new Utilisateur();
            admin.setNom("Admin");
            admin.setPrenom("BeninExplo");
            admin.setEmail("admin@beninexplo.com");
            admin.setMotDePasse(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setDateCreation(LocalDateTime.now());

            utilisateurRepository.save(admin);
            log.info("✅ Administrateur par défaut créé : admin@beninexplo.com / admin123");
        } else {
            log.info("Administrateur par défaut déjà existant.");
        }
    }
}