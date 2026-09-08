package com.beninexplo.backend;

import com.beninexplo.backend.dto.ActiviteDTO;
import com.beninexplo.backend.entity.CategorieActivite;
import com.beninexplo.backend.entity.TypeActivite;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.CategorieActiviteRepository;
import com.beninexplo.backend.repository.VilleRepository;
import com.beninexplo.backend.repository.ZoneRepository;
import com.beninexplo.backend.service.ActiviteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ActiviteService a plus de logique qu'un CRUD simple : la ville est obligatoire (dependance
 * cle pour CircuitPersonnaliseService.validateJourActivites, deja teste, qui verifie qu'une
 * activite appartient a la ville/zone du jour choisi), et categorie/image sont optionnelles
 * avec possibilite de les retirer explicitement (null) lors d'une mise a jour.
 */
@SpringBootTest
@Transactional
class ActiviteServiceTests {

    @Autowired
    private ActiviteService activiteService;

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private CategorieActiviteRepository categorieActiviteRepository;

    private Ville anyVille() {
        return villeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucune ville en fixture de test."));
    }

    private ActiviteDTO baseDto() {
        ActiviteDTO dto = new ActiviteDTO();
        dto.setNom("Randonnee test");
        dto.setType(TypeActivite.ACTIVITE);
        dto.setDescription("Une activite de test.");
        dto.setVilleId(anyVille().getIdVille());
        dto.setDureeInterne(120);
        dto.setPoids(10);
        return dto;
    }

    @Test
    void creatingWithoutVilleIsRejected() {
        ActiviteDTO dto = baseDto();
        dto.setVilleId(null);

        assertThrows(IllegalArgumentException.class, () -> activiteService.create(dto));
    }

    @Test
    void creatingWithUnknownVilleIsRejected() {
        ActiviteDTO dto = baseDto();
        dto.setVilleId(999999L);

        assertThrows(ResourceNotFoundException.class, () -> activiteService.create(dto));
    }

    @Test
    void creatingWithUnknownCategorieIsRejected() {
        ActiviteDTO dto = baseDto();
        dto.setCategorieId(999999L);

        assertThrows(ResourceNotFoundException.class, () -> activiteService.create(dto));
    }

    @Test
    void createdActiviteCarriesVilleAndZoneInfo() {
        Ville ville = anyVille();
        ActiviteDTO dto = baseDto();

        ActiviteDTO created = activiteService.create(dto);

        assertEquals(ville.getIdVille(), created.getVilleId());
        if (ville.getZone() != null) {
            assertEquals(ville.getZone().getIdZone(), created.getZoneId());
        }
    }

    @Test
    void updatingWithoutCategorieIdRemovesAnExistingCategorie() {
        CategorieActivite categorie = new CategorieActivite();
        categorie.setNom("Categorie Test " + System.nanoTime());
        categorie = categorieActiviteRepository.save(categorie);

        ActiviteDTO dto = baseDto();
        dto.setCategorieId(categorie.getIdCategorie());
        ActiviteDTO created = activiteService.create(dto);
        assertEquals(categorie.getIdCategorie(), created.getCategorieId());

        ActiviteDTO updateDto = baseDto();
        updateDto.setCategorieId(null);
        ActiviteDTO updated = activiteService.update(created.getId(), updateDto);

        assertNull(updated.getCategorieId(), "Omettre categorieId lors d'une mise a jour doit retirer la categorie existante");
    }

    @Test
    void getByZoneRejectsUnknownZone() {
        assertThrows(ResourceNotFoundException.class, () -> activiteService.getByZone(999999L));
    }

    @Test
    void getByVilleReturnsOnlyActivitiesOfThatVille() {
        Ville ville = anyVille();
        activiteService.create(baseDto());

        var result = activiteService.getByVille(ville.getIdVille());

        assertEquals(true, result.stream().allMatch(a -> ville.getIdVille().equals(a.getVilleId())));
    }
}
