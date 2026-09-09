package com.beninexplo.backend;

import com.beninexplo.backend.dto.CircuitDTO;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.repository.CircuitRepository;
import com.beninexplo.backend.repository.VilleRepository;
import com.beninexplo.backend.service.CircuitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CircuitService serialise/deserialise plusieurs champs (galerie, programme, points forts...)
 * en JSON stocke comme texte. readProgramme() a un fallback a deux niveaux (nouveau format
 * structure, puis ancien format liste de chaines, puis liste vide) jamais teste jusqu'ici :
 * une regression y serait invisible en usage normal (le service n'ecrit que le nouveau
 * format) mais casserait la lecture de donnees anciennes deja en base.
 */
@SpringBootTest
@Transactional
class CircuitServiceJsonFieldsTests {

    @Autowired
    private CircuitService circuitService;

    @Autowired
    private CircuitRepository circuitRepository;

    @Autowired
    private VilleRepository villeRepository;

    private Long anyVilleId() {
        return villeRepository.findAll().stream().findFirst()
                .map(Ville::getIdVille)
                .orElseThrow(() -> new IllegalStateException("Aucune ville en fixture de test."));
    }

    @Test
    void programmeInNewStructuredFormatIsReadBackAsIs() {
        CircuitDTO dto = new CircuitDTO();
        dto.setTitre("Circuit test format structure");
        dto.setVilleId(anyVilleId());
        dto.setPrixIndicatif(BigDecimal.valueOf(100));
        dto.setProgramme(List.of(new CircuitDTO.ProgrammeDay(1, "Arrivee", "Accueil a l'aeroport", "09:00", null, null)));

        CircuitDTO created = circuitService.create(dto);
        CircuitDTO reloaded = circuitService.getById(created.getId());

        assertEquals(1, reloaded.getProgramme().size());
        assertEquals("Arrivee", reloaded.getProgramme().get(0).getTitle());
    }

    @Test
    void programmeInLegacyStringListFormatFallsBackToPlainDescription() {
        Circuit circuit = new Circuit();
        circuit.setNom("Circuit legacy");
        circuit.setVille(villeRepository.findById(anyVilleId()).orElseThrow());
        circuit.setPrixIndicatif(BigDecimal.valueOf(100));
        // Ancien format : une simple liste de chaines, pas d'objets ProgrammeDay structures.
        circuit.setProgramme("[\"Jour 1 : arrivee et installation\", \"Jour 2 : visite guidee\"]");
        circuit = circuitRepository.save(circuit);

        CircuitDTO reloaded = circuitService.getById(circuit.getIdCircuit());

        assertEquals(2, reloaded.getProgramme().size());
        assertEquals("Jour 1 : arrivee et installation", reloaded.getProgramme().get(0).getDescription());
    }

    @Test
    void malformedProgrammeJsonFallsBackToEmptyListInsteadOfFailing() {
        Circuit circuit = new Circuit();
        circuit.setNom("Circuit json corrompu");
        circuit.setVille(villeRepository.findById(anyVilleId()).orElseThrow());
        circuit.setPrixIndicatif(BigDecimal.valueOf(100));
        circuit.setProgramme("{ceci n'est pas du json valide");
        circuit = circuitRepository.save(circuit);

        CircuitDTO reloaded = circuitService.getById(circuit.getIdCircuit());

        assertTrue(reloaded.getProgramme().isEmpty(),
                "Un programme JSON illisible doit degrader en liste vide, pas faire echouer la lecture du circuit");
    }

    @Test
    void activiteIdsRoundTripsThroughCreateAndReload() {
        CircuitDTO dto = new CircuitDTO();
        dto.setTitre("Circuit avec activites associees");
        dto.setVilleId(anyVilleId());
        dto.setPrixIndicatif(BigDecimal.valueOf(100));
        dto.setActiviteIds(List.of(1L, 2L, 3L));

        CircuitDTO created = circuitService.create(dto);
        CircuitDTO reloaded = circuitService.getById(created.getId());

        assertEquals(List.of(1L, 2L, 3L), reloaded.getActiviteIds(),
                "Regression du bug trouve en audit : activiteIds n'etait jamais lu/ecrit par le backend");
    }

    @Test
    void nullJsonFieldsAreReadAsEmptyLists() {
        Circuit circuit = new Circuit();
        circuit.setNom("Circuit sans donnees annexes");
        circuit.setVille(villeRepository.findById(anyVilleId()).orElseThrow());
        circuit.setPrixIndicatif(BigDecimal.valueOf(100));
        // galerie, programme, pointsForts, inclus, nonInclus, aventures : tous null.
        circuit = circuitRepository.save(circuit);

        CircuitDTO reloaded = circuitService.getById(circuit.getIdCircuit());

        assertTrue(reloaded.getGalerie().isEmpty());
        assertTrue(reloaded.getProgramme().isEmpty());
        assertTrue(reloaded.getPointsForts().isEmpty());
        assertTrue(reloaded.getInclus().isEmpty());
        assertTrue(reloaded.getActiviteIds().isEmpty());
    }
}
