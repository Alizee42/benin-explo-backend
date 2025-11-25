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

    /* ---------------------- DTO MAPPER ---------------------- */

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

        // Circuit obligatoire
        Circuit circuit = circuitRepo.findById(dto.getCircuitId())
                .orElseThrow(() -> new RuntimeException("Circuit non trouvé"));
        ca.setCircuit(circuit);

        // Activité obligatoire
        Activite activite = activiteRepo.findById(dto.getActiviteId())
                .orElseThrow(() -> new RuntimeException("Activité non trouvée"));
        ca.setActivite(activite);

        return ca;
    }

    /* ---------------------- CRUD ---------------------- */

    public List<CircuitActiviteDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CircuitActiviteDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public CircuitActiviteDTO create(CircuitActiviteDTO dto) {
        CircuitActivite saved = repo.save(fromDTO(dto));
        return toDTO(saved);
    }

    public CircuitActiviteDTO update(Long id, CircuitActiviteDTO dto) {

        CircuitActivite existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Association circuit-activité non trouvée"));

        existing.setOrdre(dto.getOrdre());
        existing.setJourIndicatif(dto.getJourIndicatif());

        if (dto.getCircuitId() != null) {
            existing.setCircuit(
                    circuitRepo.findById(dto.getCircuitId())
                            .orElseThrow(() -> new RuntimeException("Circuit non trouvé")));
        }

        if (dto.getActiviteId() != null) {
            existing.setActivite(
                    activiteRepo.findById(dto.getActiviteId())
                            .orElseThrow(() -> new RuntimeException("Activité non trouvée")));
        }

        return toDTO(repo.save(existing));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    /* ---------------------- FILTRE ---------------------- */

    public List<CircuitActiviteDTO> getByCircuit(Long circuitId) {
        return repo.findByCircuit_IdCircuitOrderByOrdreAsc(circuitId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
