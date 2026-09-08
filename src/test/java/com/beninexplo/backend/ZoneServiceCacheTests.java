package com.beninexplo.backend;

import com.beninexplo.backend.dto.ZoneDTO;
import com.beninexplo.backend.service.ZoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ZoneService.getAllZones() est @Cacheable("zones"), avec @CacheEvict sur create/update/delete.
 * Jamais verifie que l'invalidation fonctionne reellement : si elle etait cassee, l'admin
 * verrait des zones obsoletes dans le back-office apres une modification. Le cache Spring
 * n'est pas transactionnel (contrairement a la base H2 de test) : on le vide explicitement
 * avant chaque test pour eviter toute fuite entre tests.
 */
@SpringBootTest
@Transactional
class ZoneServiceCacheTests {

    @Autowired
    private ZoneService zoneService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearZonesCache() {
        var cache = cacheManager.getCache("zones");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void createdZoneAppearsInGetAllZonesDespiteCaching() {
        List<ZoneDTO> before = zoneService.getAllZones();
        int countBefore = before.size();

        String uniqueName = "Zone Cache Test " + UUID.randomUUID();
        zoneService.createZone(new ZoneDTO(null, uniqueName, "Fixture de test cache"));

        List<ZoneDTO> after = zoneService.getAllZones();

        assertTrue(after.size() > countBefore,
                "createZone() doit invalider le cache 'zones' : le nombre de zones doit augmenter");
        assertTrue(after.stream().anyMatch(z -> uniqueName.equals(z.getNom())),
                "La zone nouvellement creee doit apparaitre dans getAllZones() apres invalidation du cache");
    }

    @Test
    void updatedZoneNameIsReflectedAfterCacheEviction() {
        String initialName = "Zone Update Test " + UUID.randomUUID();
        ZoneDTO created = zoneService.createZone(new ZoneDTO(null, initialName, "Avant modification"));

        // Premier appel : peuple le cache avec l'ancien nom.
        zoneService.getAllZones();

        String updatedName = "Zone Mise A Jour " + UUID.randomUUID();
        zoneService.updateZone(created.getIdZone(), new ZoneDTO(created.getIdZone(), updatedName, "Apres modification"));

        List<ZoneDTO> after = zoneService.getAllZones();

        assertTrue(after.stream().anyMatch(z -> updatedName.equals(z.getNom())),
                "Le nom mis a jour doit apparaitre dans getAllZones() apres invalidation du cache");
        assertTrue(after.stream().noneMatch(z -> initialName.equals(z.getNom())),
                "L'ancien nom (issu du cache non invalide) ne doit plus apparaitre");
    }

    @Test
    void deletedZoneDisappearsFromGetAllZonesDespiteCaching() {
        String name = "Zone A Supprimer " + UUID.randomUUID();
        ZoneDTO created = zoneService.createZone(new ZoneDTO(null, name, "A supprimer"));

        // Premier appel : peuple le cache avec la zone encore presente.
        zoneService.getAllZones();

        zoneService.deleteZone(created.getIdZone());

        List<ZoneDTO> after = zoneService.getAllZones();

        assertTrue(after.stream().noneMatch(z -> name.equals(z.getNom())),
                "deleteZone() doit invalider le cache : la zone supprimee ne doit plus apparaitre");
    }
}
