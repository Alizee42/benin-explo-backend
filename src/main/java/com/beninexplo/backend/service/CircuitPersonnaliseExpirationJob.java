package com.beninexplo.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Job quotidien : relance les devis de circuit personnalise acceptes mais non payes (7 jours),
 * puis les fait expirer s'ils restent non payes (14 jours). Sans ce job, un devis ACCEPTE
 * pouvait rester en limbo indefiniment si le client ne payait jamais - trouve en audit.
 */
@Component
public class CircuitPersonnaliseExpirationJob {

    private static final Logger log = LoggerFactory.getLogger(CircuitPersonnaliseExpirationJob.class);

    private final CircuitPersonnaliseService circuitPersonnaliseService;

    public CircuitPersonnaliseExpirationJob(CircuitPersonnaliseService circuitPersonnaliseService) {
        this.circuitPersonnaliseService = circuitPersonnaliseService;
    }

    // Tous les jours a 6h du matin (heure serveur). Les deux passes sont independantes : un echec
    // de l'une ne doit pas empecher l'autre.
    @Scheduled(cron = "0 0 6 * * *")
    public void runDailyCheck() {
        try {
            circuitPersonnaliseService.sendRappelsPaiement();
        } catch (Exception e) {
            log.error("Echec de l'envoi des rappels de paiement circuit personnalise", e);
        }

        try {
            circuitPersonnaliseService.expirerDemandesNonPayees();
        } catch (Exception e) {
            log.error("Echec de l'expiration des devis circuit personnalise non payes", e);
        }
    }
}
