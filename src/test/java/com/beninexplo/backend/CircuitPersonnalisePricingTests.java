package com.beninexplo.backend;

import com.beninexplo.backend.dto.CircuitPersonnaliseDTO;
import com.beninexplo.backend.dto.TarifsCircuitPersonnaliseDTO;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.service.CircuitPersonnaliseService;
import com.beninexplo.backend.service.TarifsCircuitPersonnaliseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Couvre le calcul de prix d'un circuit personnalise (CircuitPersonnaliseService), la partie
 * la plus sensible du service (impacte directement ce que le client paie) et jusqu'ici non
 * testee. Les methodes de calcul detaille sont privees : on les exerce via create(), avec des
 * tarifs de reference poses explicitement pour rendre le montant attendu previsible.
 */
@SpringBootTest
@Transactional
class CircuitPersonnalisePricingTests {

    @Autowired
    private CircuitPersonnaliseService circuitPersonnaliseService;

    @Autowired
    private TarifsCircuitPersonnaliseService tarifsService;

    @BeforeEach
    void setUpTarifs() {
        TarifsCircuitPersonnaliseDTO tarifs = new TarifsCircuitPersonnaliseDTO();
        tarifs.setDevise("EUR");
        tarifs.setTransportCompactParJour(BigDecimal.valueOf(20));
        tarifs.setTransportFamilialParJour(BigDecimal.valueOf(35));
        tarifs.setTransportMinibusParJour(BigDecimal.valueOf(50));
        tarifs.setTransportBusParJour(BigDecimal.valueOf(80));
        tarifs.setGuideParJour(BigDecimal.valueOf(15));
        tarifs.setChauffeurParJour(BigDecimal.valueOf(10));
        tarifs.setPensionCompleteParPersonneParJour(BigDecimal.valueOf(25));
        tarifsService.saveOrUpdate(tarifs);
    }

    private CircuitPersonnaliseDTO baseDemande() {
        CircuitPersonnaliseDTO dto = new CircuitPersonnaliseDTO();
        dto.setNomClient("Doe");
        dto.setPrenomClient("Jane");
        dto.setEmailClient("pricing.tests@example.com");
        dto.setTelephoneClient("+22900000000");
        dto.setNombreJours(4);
        dto.setNombrePersonnes(2);
        dto.setDateVoyageSouhaitee(LocalDate.now().plusMonths(1));
        return dto;
    }

    @Test
    void transportGuideChauffeurPensionArePricedPerBillableDay() {
        CircuitPersonnaliseDTO dto = baseDemande();
        dto.setAvecTransport(true);
        dto.setTypeTransport("Minibus familial");
        dto.setAvecGuide(true);
        dto.setAvecChauffeur(true);
        dto.setPensionComplete(true);

        CircuitPersonnaliseDTO created = circuitPersonnaliseService.create(dto);

        // "familial" matche avant "minibus" dans resolveTransportRate (contains "famil") : 35/jour.
        assertEquals(0, BigDecimal.valueOf(140).compareTo(created.getPrixTransportEstime()),
                "4 jours * 35 EUR (tarif familial, priorite sur minibus car le libelle contient les deux mots)");
        assertEquals(0, BigDecimal.valueOf(60).compareTo(created.getPrixGuideEstime()),
                "4 jours * 15 EUR de guide");
        assertEquals(0, BigDecimal.valueOf(40).compareTo(created.getPrixChauffeurEstime()),
                "4 jours * 10 EUR de chauffeur");
        assertEquals(0, BigDecimal.valueOf(200).compareTo(created.getPrixPensionCompleteEstime()),
                "4 jours * 2 personnes * 25 EUR de pension complete");

        BigDecimal expectedTotal = BigDecimal.valueOf(140 + 60 + 40 + 200);
        assertEquals(0, expectedTotal.compareTo(created.getPrixEstime()),
                "Le prix estime total doit etre la somme de tous les postes calcules");
    }

    @Test
    void optionsNotSelectedContributeZeroToThePrice() {
        CircuitPersonnaliseDTO dto = baseDemande();
        // Aucune option (transport/guide/chauffeur/pension) activee.

        CircuitPersonnaliseDTO created = circuitPersonnaliseService.create(dto);

        assertEquals(0, BigDecimal.ZERO.compareTo(created.getPrixTransportEstime()));
        assertEquals(0, BigDecimal.ZERO.compareTo(created.getPrixGuideEstime()));
        assertEquals(0, BigDecimal.ZERO.compareTo(created.getPrixChauffeurEstime()));
        assertEquals(0, BigDecimal.ZERO.compareTo(created.getPrixPensionCompleteEstime()));
        assertEquals(0, BigDecimal.ZERO.compareTo(created.getPrixEstime()));
    }

    @Test
    void busTransportIsPricedAtTheBusRateWhenLabelHasNoOtherMatch() {
        CircuitPersonnaliseDTO dto = baseDemande();
        dto.setAvecTransport(true);
        dto.setTypeTransport("Grand Bus");

        CircuitPersonnaliseDTO created = circuitPersonnaliseService.create(dto);

        assertEquals(0, BigDecimal.valueOf(320).compareTo(created.getPrixTransportEstime()),
                "4 jours * 80 EUR (tarif bus)");
    }

    @Test
    void unknownTransportLabelIsPricedAtZero() {
        CircuitPersonnaliseDTO dto = baseDemande();
        dto.setAvecTransport(true);
        dto.setTypeTransport("Pirogue traditionnelle");

        CircuitPersonnaliseDTO created = circuitPersonnaliseService.create(dto);

        assertEquals(0, BigDecimal.ZERO.compareTo(created.getPrixTransportEstime()),
                "Un type de transport qui ne correspond a aucun tarif connu doit couter 0, pas lever d'erreur");
    }

    @Test
    void hebergementDatesInThePastAreRejected() {
        CircuitPersonnaliseDTO dto = baseDemande();
        // hebergementId volontairement absent de la fixture de test ; on verifie ici que la
        // validation des dates s'execute avant meme la recherche de l'hebergement.
        dto.setHebergementId(999999L);
        dto.setDateArriveeHebergement(LocalDate.now().minusDays(1));
        dto.setDateDepartHebergement(LocalDate.now().plusDays(2));

        assertThrows(BadRequestException.class, () -> circuitPersonnaliseService.create(dto));
    }

    @Test
    void hebergementDepartDateBeforeArriveeIsRejected() {
        CircuitPersonnaliseDTO dto = baseDemande();
        dto.setHebergementId(999999L);
        LocalDate arrivee = LocalDate.now().plusDays(10);
        dto.setDateArriveeHebergement(arrivee);
        dto.setDateDepartHebergement(arrivee.minusDays(1));

        assertThrows(BadRequestException.class, () -> circuitPersonnaliseService.create(dto));
    }
}
