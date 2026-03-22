package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.CircuitActiviteDTO;
import com.beninexplo.backend.entity.Activite;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.CircuitActivite;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.ActiviteRepository;
import com.beninexplo.backend.repository.CircuitActiviteRepository;
import com.beninexplo.backend.repository.CircuitRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CircuitActiviteService {

    private final CircuitActiviteRepository repo;
    private final CircuitRepository circuitRepo;
    private final ActiviteRepository activiteRepo;

    public CircuitActiviteService(CircuitActiviteRepository repo,
                                  CircuitRepository circuitRepo,
                                  ActiviteRepository activiteRepo) {
        this.repo = repo;
        this.circuitRepo = circuitRepo;
        this.activiteRepo = activiteRepo;
    }

    private CircuitActiviteDTO toDTO(CircuitActivite circuitActivite) {
        CircuitActiviteDTO dto = new CircuitActiviteDTO();
        dto.setId(circuitActivite.getIdCircuitActivite());
        dto.setCircuitId(circuitActivite.getCircuit().getIdCircuit());
        dto.setActiviteId(circuitActivite.getActivite().getIdActivite());
        dto.setOrdre(circuitActivite.getOrdre());
        dto.setJourIndicatif(circuitActivite.getJourIndicatif());
        return dto;
    }

    private CircuitActivite fromDTO(CircuitActiviteDTO dto) {
        Circuit circuit = circuitRepo.findById(dto.getCircuitId())
                .orElseThrow(() -> new ResourceNotFoundException("Circuit non trouve."));
        Activite activite = activiteRepo.findById(dto.getActiviteId())
                .orElseThrow(() -> new ResourceNotFoundException("Activite non trouvee."));

        CircuitActivite circuitActivite = new CircuitActivite();
        circuitActivite.setIdCircuitActivite(dto.getId());
        circuitActivite.setOrdre(dto.getOrdre());
        circuitActivite.setJourIndicatif(dto.getJourIndicatif());
        circuitActivite.setCircuit(circuit);
        circuitActivite.setActivite(activite);
        return circuitActivite;
    }

    public List<CircuitActiviteDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CircuitActiviteDTO get(Long id) {
        return repo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Association circuit-activite introuvable."));
    }

    public CircuitActiviteDTO create(CircuitActiviteDTO dto) {
        return toDTO(repo.save(fromDTO(dto)));
    }

    public CircuitActiviteDTO update(Long id, CircuitActiviteDTO dto) {
        CircuitActivite existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Association circuit-activite introuvable."));

        existing.setOrdre(dto.getOrdre());
        existing.setJourIndicatif(dto.getJourIndicatif());

        if (dto.getCircuitId() != null) {
            Circuit circuit = circuitRepo.findById(dto.getCircuitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Circuit non trouve."));
            existing.setCircuit(circuit);
        }

        if (dto.getActiviteId() != null) {
            Activite activite = activiteRepo.findById(dto.getActiviteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Activite non trouvee."));
            existing.setActivite(activite);
        }

        return toDTO(repo.save(existing));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<CircuitActiviteDTO> getByCircuit(Long circuitId) {
        return repo.findByCircuit_IdCircuitOrderByOrdreAsc(circuitId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
