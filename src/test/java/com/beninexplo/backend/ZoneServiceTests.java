package com.beninexplo.backend;

import com.beninexplo.backend.dto.VilleDTO;
import com.beninexplo.backend.dto.ZoneDTO;
import com.beninexplo.backend.exception.ConflictException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.service.VilleService;
import com.beninexplo.backend.service.ZoneService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression pour le bug trouve en audit : deleteZone() appelait deleteById() sans verification,
 * la contrainte FK en base bloquait bien la suppression mais l'erreur remontait comme un 500
 * generique "Une erreur de base de donnees est survenue" au lieu d'un message clair pour l'admin.
 */
@SpringBootTest
@Transactional
class ZoneServiceTests {

    @Autowired
    private ZoneService zoneService;

    @Autowired
    private VilleService villeService;

    @Test
    void deletingZoneReferencedByAVilleIsRejectedWithClearMessage() {
        ZoneDTO zone = zoneService.createZone(new ZoneDTO(null, "Zone Test " + UUID.randomUUID(), "desc"));
        VilleDTO ville = new VilleDTO();
        ville.setNom("Ville Test " + UUID.randomUUID());
        ville.setZoneId(zone.getIdZone());
        villeService.create(ville);

        ConflictException ex = assertThrows(ConflictException.class, () -> zoneService.deleteZone(zone.getIdZone()));
        assertTrue(ex.getMessage().contains("ville"),
                "Le message doit indiquer clairement que la zone est utilisee par des villes");
    }

    @Test
    void deletingUnreferencedZoneSucceeds() {
        ZoneDTO zone = zoneService.createZone(new ZoneDTO(null, "Zone Sans Ville " + UUID.randomUUID(), "desc"));

        zoneService.deleteZone(zone.getIdZone());

        assertThrows(ResourceNotFoundException.class, () -> zoneService.getZoneById(zone.getIdZone()));
    }

    @Test
    void deletingUnknownZoneIsRejected() {
        assertThrows(ResourceNotFoundException.class, () -> zoneService.deleteZone(999999L));
    }
}
