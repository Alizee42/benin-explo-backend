package com.beninexplo.backend;

import com.beninexplo.backend.dto.VilleDTO;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.ZoneRepository;
import com.beninexplo.backend.service.VilleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * VilleService suit le meme pattern de cache que ZoneService/ParametresSiteService
 * (getAll @Cacheable("villes"), @CacheEvict sur create/update/delete) : verifie que
 * l'invalidation fonctionne. Couvre aussi la validation de zone et le retrait explicite d'une
 * zone lors d'une mise a jour.
 */
@SpringBootTest
@Transactional
class VilleServiceTests {

    @Autowired
    private VilleService villeService;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        var cache = cacheManager.getCache("villes");
        if (cache != null) {
            cache.clear();
        }
    }

    private Zone anyZone() {
        return zoneRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucune zone en fixture de test."));
    }

    @Test
    void createdVilleAppearsInGetAllDespiteCaching() {
        villeService.getAll();

        String uniqueName = "Ville Cache Test " + System.nanoTime();
        VilleDTO dto = new VilleDTO();
        dto.setNom(uniqueName);
        dto.setZoneId(anyZone().getIdZone());
        villeService.create(dto);

        assertTrue(villeService.getAll().stream().anyMatch(v -> uniqueName.equals(v.getNom())),
                "create() doit invalider le cache 'villes'");
    }

    @Test
    void creatingWithUnknownZoneIsRejected() {
        VilleDTO dto = new VilleDTO();
        dto.setNom("Ville Zone Inconnue");
        dto.setZoneId(999999L);

        assertThrows(ResourceNotFoundException.class, () -> villeService.create(dto));
    }

    @Test
    void updatingWithoutZoneIdRemovesTheExistingZone() {
        VilleDTO dto = new VilleDTO();
        dto.setNom("Ville Avec Zone " + System.nanoTime());
        dto.setZoneId(anyZone().getIdZone());
        VilleDTO created = villeService.create(dto);
        assertEquals(anyZone().getIdZone(), created.getZoneId());

        VilleDTO updateDto = new VilleDTO();
        updateDto.setNom(created.getNom());
        updateDto.setZoneId(null);
        VilleDTO updated = villeService.update(created.getId(), updateDto);

        assertNull(updated.getZoneId(), "Omettre zoneId lors d'une mise a jour doit retirer la zone existante");
    }

    @Test
    void getByZoneReturnsOnlyVillesOfThatZone() {
        Zone zone = anyZone();
        VilleDTO dto = new VilleDTO();
        dto.setNom("Ville Filtre Zone " + System.nanoTime());
        dto.setZoneId(zone.getIdZone());
        villeService.create(dto);

        var result = villeService.getByZone(zone.getIdZone());

        assertTrue(result.stream().allMatch(v -> zone.getIdZone().equals(v.getZoneId())));
    }
}
