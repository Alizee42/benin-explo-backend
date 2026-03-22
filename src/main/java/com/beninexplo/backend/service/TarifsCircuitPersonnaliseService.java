package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.TarifsCircuitPersonnaliseDTO;
import com.beninexplo.backend.entity.TarifsCircuitPersonnalise;
import com.beninexplo.backend.repository.TarifsCircuitPersonnaliseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Transactional
@Service
public class TarifsCircuitPersonnaliseService {

    private final TarifsCircuitPersonnaliseRepository repository;

    public TarifsCircuitPersonnaliseService(TarifsCircuitPersonnaliseRepository repository) {
        this.repository = repository;
    }

    public TarifsCircuitPersonnaliseDTO getCurrent() {
        return toDTO(repository.findTopByOrderByIdAsc().orElseGet(this::buildDefaultEntity));
    }

    public TarifsCircuitPersonnaliseDTO saveOrUpdate(TarifsCircuitPersonnaliseDTO dto) {
        TarifsCircuitPersonnalise entity = repository.findTopByOrderByIdAsc().orElseGet(this::buildDefaultEntity);

        entity.setDevise(normalizeDevise(dto.getDevise()));
        entity.setTransportCompactParJour(defaultAmount(dto.getTransportCompactParJour()));
        entity.setTransportFamilialParJour(defaultAmount(dto.getTransportFamilialParJour()));
        entity.setTransportMinibusParJour(defaultAmount(dto.getTransportMinibusParJour()));
        entity.setTransportBusParJour(defaultAmount(dto.getTransportBusParJour()));
        entity.setGuideParJour(defaultAmount(dto.getGuideParJour()));
        entity.setChauffeurParJour(defaultAmount(dto.getChauffeurParJour()));
        entity.setPensionCompleteParPersonneParJour(defaultAmount(dto.getPensionCompleteParPersonneParJour()));

        return toDTO(repository.save(entity));
    }

    private TarifsCircuitPersonnaliseDTO toDTO(TarifsCircuitPersonnalise entity) {
        TarifsCircuitPersonnaliseDTO dto = new TarifsCircuitPersonnaliseDTO();
        dto.setId(entity.getId());
        dto.setDevise(normalizeDevise(entity.getDevise()));
        dto.setTransportCompactParJour(defaultAmount(entity.getTransportCompactParJour()));
        dto.setTransportFamilialParJour(defaultAmount(entity.getTransportFamilialParJour()));
        dto.setTransportMinibusParJour(defaultAmount(entity.getTransportMinibusParJour()));
        dto.setTransportBusParJour(defaultAmount(entity.getTransportBusParJour()));
        dto.setGuideParJour(defaultAmount(entity.getGuideParJour()));
        dto.setChauffeurParJour(defaultAmount(entity.getChauffeurParJour()));
        dto.setPensionCompleteParPersonneParJour(defaultAmount(entity.getPensionCompleteParPersonneParJour()));
        return dto;
    }

    private TarifsCircuitPersonnalise buildDefaultEntity() {
        TarifsCircuitPersonnalise entity = new TarifsCircuitPersonnalise();
        entity.setDevise("EUR");
        entity.setTransportCompactParJour(BigDecimal.ZERO);
        entity.setTransportFamilialParJour(BigDecimal.ZERO);
        entity.setTransportMinibusParJour(BigDecimal.ZERO);
        entity.setTransportBusParJour(BigDecimal.ZERO);
        entity.setGuideParJour(BigDecimal.ZERO);
        entity.setChauffeurParJour(BigDecimal.ZERO);
        entity.setPensionCompleteParPersonneParJour(BigDecimal.ZERO);
        return entity;
    }

    private BigDecimal defaultAmount(BigDecimal value) {
        return value != null ? value.max(BigDecimal.ZERO) : BigDecimal.ZERO;
    }

    private String normalizeDevise(String devise) {
        if (devise == null || devise.isBlank()) {
            return "EUR";
        }
        return devise.trim().toUpperCase();
    }
}
