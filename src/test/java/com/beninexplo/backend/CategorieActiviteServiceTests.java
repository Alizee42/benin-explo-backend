package com.beninexplo.backend;

import com.beninexplo.backend.dto.CategorieActiviteDTO;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.service.CategorieActiviteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * CategorieActiviteService est un CRUD simple (comme HebergementService) : couverture minimale
 * du cycle de vie complet (creation, lecture, mise a jour, suppression, 404 sur id inconnu).
 */
@SpringBootTest
@Transactional
class CategorieActiviteServiceTests {

    @Autowired
    private CategorieActiviteService categorieActiviteService;

    @Test
    void fullCrudLifecycle() {
        CategorieActiviteDTO created = categorieActiviteService.create(
                new CategorieActiviteDTO(null, "Categorie Test", "Description initiale"));
        assertEquals("Categorie Test", categorieActiviteService.get(created.getId()).getNom());

        CategorieActiviteDTO updated = categorieActiviteService.update(
                created.getId(), new CategorieActiviteDTO(created.getId(), "Categorie Renommee", "Description mise a jour"));
        assertEquals("Categorie Renommee", updated.getNom());

        categorieActiviteService.delete(created.getId());
        assertFalse(categorieActiviteService.getAll().stream().anyMatch(c -> c.getId().equals(created.getId())));
    }

    @Test
    void gettingUnknownIdIsRejected() {
        assertThrows(ResourceNotFoundException.class, () -> categorieActiviteService.get(999999L));
    }

    @Test
    void updatingUnknownIdIsRejected() {
        assertThrows(ResourceNotFoundException.class, () ->
                categorieActiviteService.update(999999L, new CategorieActiviteDTO(999999L, "X", "Y")));
    }
}
