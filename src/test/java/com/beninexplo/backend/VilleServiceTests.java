package com.beninexplo.backend;

import com.beninexplo.backend.dto.VilleDTO;
import com.beninexplo.backend.entity.Activite;
import com.beninexplo.backend.entity.TypeActivite;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.exception.ConflictException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.ActiviteRepository;
import com.beninexplo.backend.repository.VilleRepository;
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
    private VilleRepository villeRepository;

    @Autowired
    private ActiviteRepository activiteRepository;

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
    void deletingVilleReferencedByAnActiviteIsRejectedWithClearMessage() {
        VilleDTO dto = new VilleDTO();
        dto.setNom("Ville Avec Activite " + System.nanoTime());
        dto.setZoneId(anyZone().getIdZone());
        VilleDTO created = villeService.create(dto);

        Ville ville = villeRepository.findById(created.getId()).orElseThrow();
        Activite activite = new Activite();
        activite.setNom("Activite Test");
        activite.setType(TypeActivite.ACTIVITE);
        activite.setVille(ville);
        activiteRepository.save(activite);

        ConflictException ex = assertThrows(ConflictException.class, () -> villeService.delete(created.getId()));
        assertTrue(ex.getMessage().contains("activite"),
                "Le message doit indiquer clairement que la ville est utilisee par des activites");
    }

    @Test
    void deletingUnreferencedVilleSucceeds() {
        VilleDTO dto = new VilleDTO();
        dto.setNom("Ville Sans Reference " + System.nanoTime());
        dto.setZoneId(anyZone().getIdZone());
        VilleDTO created = villeService.create(dto);

        villeService.delete(created.getId());

        assertThrows(ResourceNotFoundException.class, () -> villeService.getById(created.getId()));
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
