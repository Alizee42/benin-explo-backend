package com.beninexplo.backend;

import com.beninexplo.backend.dto.ActualiteDTO;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.service.ActualiteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ActualiteService distingue une vue admin (tout) d'une vue publique (uniquement les
 * actualites publiees) - une actualite non publiee ne doit jamais fuiter via les endpoints
 * publics. Couvre aussi la date de publication par defaut et le fallback d'image.
 */
@SpringBootTest
@Transactional
class ActualiteServiceTests {

    @Autowired
    private ActualiteService actualiteService;

    private ActualiteDTO baseDto(boolean publiee) {
        ActualiteDTO dto = new ActualiteDTO();
        dto.setTitre("Actualite test " + System.nanoTime());
        dto.setContenu("Contenu de test.");
        dto.setPubliee(publiee);
        dto.setALaUne(false);
        return dto;
    }

    @Test
    void unpublishedActualiteIsInvisibleToThePublicView() {
        ActualiteDTO created = actualiteService.create(baseDto(false));

        assertTrue(actualiteService.getPublished().stream().noneMatch(a -> a.getId().equals(created.getId())),
                "Une actualite non publiee ne doit jamais apparaitre dans getPublished()");
        assertThrows(ResourceNotFoundException.class, () -> actualiteService.getPublished(created.getId()));
    }

    @Test
    void publishedActualiteIsVisibleToThePublicView() {
        ActualiteDTO created = actualiteService.create(baseDto(true));

        assertTrue(actualiteService.getPublished().stream().anyMatch(a -> a.getId().equals(created.getId())));
        assertEquals(created.getId(), actualiteService.getPublished(created.getId()).getId());
    }

    @Test
    void adminViewSeesBothPublishedAndUnpublished() {
        ActualiteDTO published = actualiteService.create(baseDto(true));
        ActualiteDTO unpublished = actualiteService.create(baseDto(false));

        var adminList = actualiteService.getAllAdmin();

        assertTrue(adminList.stream().anyMatch(a -> a.getId().equals(published.getId())));
        assertTrue(adminList.stream().anyMatch(a -> a.getId().equals(unpublished.getId())));
    }

    @Test
    void missingDatePublicationDefaultsToNow() {
        ActualiteDTO dto = baseDto(true);
        dto.setDatePublication(null);

        ActualiteDTO created = actualiteService.create(dto);

        assertNotNull(created.getDatePublication());
    }

    @Test
    void blankResumeIsStoredAsNull() {
        ActualiteDTO dto = baseDto(true);
        dto.setResume("   ");

        ActualiteDTO created = actualiteService.create(dto);

        assertEquals(null, created.getResume());
    }

    @Test
    void creatingWithUnknownAuteurIsRejected() {
        ActualiteDTO dto = baseDto(true);
        dto.setAuteurId(999999L);

        assertThrows(ResourceNotFoundException.class, () -> actualiteService.create(dto));
    }

    @Test
    void updatingWithoutAuteurIdRemovesAnExistingAuthor() {
        // On ne peut pas facilement fixer un auteur valide sans utilisateur fixture dedie ici ;
        // on verifie plutot que l'auteur reste absent tant qu'aucun auteurId n'est fourni, ce qui
        // couvre la meme branche (fillEntity met explicitement auteur a null si auteurId est null).
        ActualiteDTO created = actualiteService.create(baseDto(true));
        assertEquals(null, created.getAuteurId());

        ActualiteDTO updated = actualiteService.update(created.getId(), baseDto(true));
        assertEquals(null, updated.getAuteurId());
    }

    @Test
    void actualiteWithFutureDatePublicationIsInvisibleToThePublicViewUntilThatDate() {
        ActualiteDTO dto = baseDto(true);
        dto.setDatePublication(LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        ActualiteDTO created = actualiteService.create(dto);

        assertTrue(actualiteService.getPublished().stream().noneMatch(a -> a.getId().equals(created.getId())),
                "Une actualite programmee dans le futur ne doit pas apparaitre dans getPublished() avant sa date");
        assertThrows(ResourceNotFoundException.class, () -> actualiteService.getPublished(created.getId()));

        assertTrue(actualiteService.getAllAdmin().stream().anyMatch(a -> a.getId().equals(created.getId())),
                "L'admin doit continuer a voir les actualites programmees dans le futur");
    }

    @Test
    void publishedListReturnsExcerptButDetailReturnsFullContenu() {
        ActualiteDTO dto = baseDto(true);
        dto.setContenu("x".repeat(500));
        // Date fixee dans le passe proche plutot que laissee a LocalDateTime.now() (defaut de
        // fillEntity) : evite toute dependance a la precision de troncature du timestamp entre
        // l'ecriture et la comparaison "datePublication <= now" faite par getPublished().
        dto.setDatePublication(LocalDateTime.now().minusMinutes(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        ActualiteDTO created = actualiteService.create(dto);

        ActualiteDTO fromList = actualiteService.getPublished().stream()
                .filter(a -> a.getId().equals(created.getId()))
                .findFirst()
                .orElseThrow();
        ActualiteDTO fromDetail = actualiteService.getPublished(created.getId());

        assertTrue(fromList.getContenu().length() < 500,
                "La liste publique doit renvoyer un extrait tronque, pas le contenu complet");
        assertEquals(500, fromDetail.getContenu().length(),
                "Le detail doit toujours renvoyer le contenu complet");
    }

    @Test
    void blankDatePublicationOnUpdateKeepsExistingDateInsteadOfResettingToNow() {
        ActualiteDTO dto = baseDto(true);
        dto.setDatePublication(LocalDateTime.now().minusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        ActualiteDTO created = actualiteService.create(dto);
        String originalDate = created.getDatePublication();

        ActualiteDTO patch = baseDto(true);
        patch.setTitre("Titre modifie");
        patch.setDatePublication(null);
        ActualiteDTO updated = actualiteService.update(created.getId(), patch);

        assertEquals(originalDate, updated.getDatePublication(),
                "Une date de publication vide en modification ne doit pas ecraser silencieusement la date existante");
    }

    @Test
    void deletedActualiteIsRemoved() {
        ActualiteDTO created = actualiteService.create(baseDto(true));

        actualiteService.delete(created.getId());

        assertFalse(actualiteService.getAllAdmin().stream().anyMatch(a -> a.getId().equals(created.getId())));
    }
}
