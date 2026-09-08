package com.beninexplo.backend;

import com.beninexplo.backend.dto.ParametresSiteDTO;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.ParametresSiteRepository;
import com.beninexplo.backend.service.ParametresSiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ParametresSiteService, comme ZoneService, cache getAll() avec invalidation sur
 * saveOrUpdate(). Contrairement a TarifsCircuitPersonnaliseService (vrai singleton verrouille
 * par findTopByOrderByIdAsc), saveOrUpdate() ici delegue simplement a repo.save() : c'est
 * l'appelant (le controller, en fixant l'id via PUT /{id}) qui garantit qu'on met a jour
 * l'enregistrement existant plutot que d'en creer un nouveau. Documente ce contrat par un test.
 */
@SpringBootTest
@Transactional
class ParametresSiteServiceTests {

    @Autowired
    private ParametresSiteService parametresSiteService;

    @Autowired
    private ParametresSiteRepository parametresSiteRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        var cache = cacheManager.getCache("parametres-site");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void savingWithoutIdCreatesANewRecordEachTime() {
        long countBefore = parametresSiteRepository.count();

        parametresSiteService.saveOrUpdate(new ParametresSiteDTO(null, "a@example.com", "+229000", "Cotonou"));
        parametresSiteService.saveOrUpdate(new ParametresSiteDTO(null, "b@example.com", "+229001", "Ouidah"));

        assertEquals(countBefore + 2, parametresSiteRepository.count(),
                "saveOrUpdate() sans id cree un nouvel enregistrement a chaque appel (contrat different de TarifsCircuitPersonnaliseService)");
    }

    @Test
    void savingWithAnExistingIdUpdatesInPlace() {
        ParametresSiteDTO created = parametresSiteService.saveOrUpdate(
                new ParametresSiteDTO(null, "initial@example.com", "+229000", "Cotonou"));
        long countAfterCreate = parametresSiteRepository.count();

        parametresSiteService.saveOrUpdate(
                new ParametresSiteDTO(created.getId(), "updated@example.com", "+229999", "Porto-Novo"));

        assertEquals(countAfterCreate, parametresSiteRepository.count(),
                "Fournir l'id existant doit mettre a jour l'enregistrement, pas en creer un nouveau");
        assertEquals("updated@example.com", parametresSiteService.get(created.getId()).getEmailContact());
    }

    @Test
    void getAllReflectsChangesAfterCacheEviction() {
        ParametresSiteDTO created = parametresSiteService.saveOrUpdate(
                new ParametresSiteDTO(null, "cache.test@example.com", "+229000", "Cotonou"));

        // Premier appel : peuple le cache.
        parametresSiteService.getAll();

        parametresSiteService.saveOrUpdate(
                new ParametresSiteDTO(created.getId(), "cache.updated@example.com", "+229000", "Cotonou"));

        boolean found = parametresSiteService.getAll().stream()
                .anyMatch(p -> "cache.updated@example.com".equals(p.getEmailContact()));
        assertEquals(true, found, "getAll() doit refleter la modification apres invalidation du cache");
    }

    @Test
    void getUnknownIdIsRejected() {
        assertThrows(ResourceNotFoundException.class, () -> parametresSiteService.get(999999L));
    }
}
