package com.beninexplo.backend;

import com.beninexplo.backend.dto.TarifsCircuitPersonnaliseDTO;
import com.beninexplo.backend.repository.TarifsCircuitPersonnaliseRepository;
import com.beninexplo.backend.service.TarifsCircuitPersonnaliseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TarifsCircuitPersonnaliseService gere un unique enregistrement de tarifs (findTopByOrderByIdAsc),
 * utilise par CircuitPersonnaliseService pour tout le calcul de prix (deja teste en isolation
 * avec des tarifs poses explicitement). Ici, on verifie le comportement du service lui-meme :
 * le caractere singleton (jamais deux enregistrements crees), et la normalisation defensive des
 * montants et de la devise.
 */
@SpringBootTest
@Transactional
class TarifsCircuitPersonnaliseServiceTests {

    @Autowired
    private TarifsCircuitPersonnaliseService tarifsService;

    @Autowired
    private TarifsCircuitPersonnaliseRepository tarifsRepository;

    private TarifsCircuitPersonnaliseDTO newDto(BigDecimal guideParJour) {
        TarifsCircuitPersonnaliseDTO dto = new TarifsCircuitPersonnaliseDTO();
        dto.setDevise("eur");
        dto.setTransportCompactParJour(BigDecimal.valueOf(10));
        dto.setTransportFamilialParJour(BigDecimal.valueOf(20));
        dto.setTransportMinibusParJour(BigDecimal.valueOf(30));
        dto.setTransportBusParJour(BigDecimal.valueOf(40));
        dto.setGuideParJour(guideParJour);
        dto.setChauffeurParJour(BigDecimal.valueOf(10));
        dto.setPensionCompleteParPersonneParJour(BigDecimal.valueOf(15));
        return dto;
    }

    @Test
    void savingTwiceUpdatesTheSameRecordInsteadOfCreatingASecondOne() {
        long countBefore = tarifsRepository.count();

        tarifsService.saveOrUpdate(newDto(BigDecimal.valueOf(15)));
        tarifsService.saveOrUpdate(newDto(BigDecimal.valueOf(25)));

        long countAfter = tarifsRepository.count();
        assertEquals(countBefore == 0 ? 1 : countBefore, countAfter,
                "saveOrUpdate() appele deux fois ne doit jamais creer un second enregistrement de tarifs");
    }

    @Test
    void secondSaveOverwritesThePreviousValues() {
        tarifsService.saveOrUpdate(newDto(BigDecimal.valueOf(15)));
        tarifsService.saveOrUpdate(newDto(BigDecimal.valueOf(25)));

        TarifsCircuitPersonnaliseDTO current = tarifsService.getCurrent();

        assertEquals(0, BigDecimal.valueOf(25).compareTo(current.getGuideParJour()));
    }

    @Test
    void deviseIsNormalizedToUppercase() {
        tarifsService.saveOrUpdate(newDto(BigDecimal.valueOf(15)));

        TarifsCircuitPersonnaliseDTO current = tarifsService.getCurrent();

        assertEquals("EUR", current.getDevise());
    }

    @Test
    void blankDeviseDefaultsToEur() {
        TarifsCircuitPersonnaliseDTO dto = newDto(BigDecimal.valueOf(15));
        dto.setDevise("");

        tarifsService.saveOrUpdate(dto);

        assertEquals("EUR", tarifsService.getCurrent().getDevise());
    }

    @Test
    void negativeAmountsAreClampedToZero() {
        TarifsCircuitPersonnaliseDTO dto = newDto(BigDecimal.valueOf(-50));

        tarifsService.saveOrUpdate(dto);

        assertEquals(0, BigDecimal.ZERO.compareTo(tarifsService.getCurrent().getGuideParJour()),
                "Un tarif negatif ne doit jamais etre persiste tel quel, il doit etre ramene a zero");
    }

    @Test
    void getCurrentReturnsSafeDefaultsWhenNoTarifsEverConfigured() {
        // Aucun saveOrUpdate() appele dans ce test : la table est vide.
        TarifsCircuitPersonnaliseDTO current = tarifsService.getCurrent();

        assertEquals("EUR", current.getDevise());
        assertEquals(0, BigDecimal.ZERO.compareTo(current.getGuideParJour()));
    }
}
