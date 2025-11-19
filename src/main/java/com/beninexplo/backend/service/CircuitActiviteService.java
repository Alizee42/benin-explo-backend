package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.CircuitActiviteDTO;
import com.beninexplo.backend.entity.*;
import com.beninexplo.backend.repository.*;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CircuitActiviteService {

    private final CircuitActiviteRepository repo;
    private final CircuitRepository circuitRepo;
    private final ActiviteRepository activiteRepo;

    public CircuitActiviteService(
            CircuitActiviteRepository repo,
            CircuitRepository circuitRepo,
            ActiviteRepository activiteRepo
    ) {
        this.repo = repo;
        this.circuitRepo = circuitRepo;
        this.activiteRepo = activiteRepo;
    }

    private CircuitActiviteDTO toDTO(CircuitActivite ca) {
        CircuitActiviteDTO dto = new CircuitActiviteDTO();
        dto.setId(ca.getIdCircuitActivite());
        dto.setCircuitId(ca.getCircuit().getIdCircuit());
        dto.setActiviteId(ca.getActivite().getIdActivite());
        dto.setOrdre(ca.getOrdre());
        dto.setJourIndicatif(ca.getJourIndicatif());
        return dto;
    }

    private CircuitActivite fromDTO(CircuitActiviteDTO dto) {
        CircuitActivite ca = new CircuitActivite();
        ca.setIdCircuitActivite(dto.getId());
        ca.setOrdre(dto.getOrdre());
        ca.setJourIndicatif(dto.getJourIndicatif());

        if (dto.getCircuitId() != null) {
            ca.setCircuit(circuitRepo.findById(dto.getCircuitId()).orElse(null));
        }

        if (dto.getActiviteId() != null) {
            ca.setActivite(activiteRepo.findById(dto.getActiviteId()).orElse(null));
        }

        return ca;
    }

    public List<CircuitActiviteDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CircuitActiviteDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public CircuitActiviteDTO create(CircuitActiviteDTO dto) {
        return toDTO(repo.save(fromDTO(dto)));
    }

    public CircuitActiviteDTO update(Long id, CircuitActiviteDTO dto) {
        dto.setId(id);
        return toDTO(repo.save(fromDTO(dto)));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
