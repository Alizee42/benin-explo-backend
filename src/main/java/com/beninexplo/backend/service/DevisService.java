package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.DevisRequestDTO;
import com.beninexplo.backend.dto.DevisResponseDTO;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.Devis;
import com.beninexplo.backend.repository.CircuitRepository;
import com.beninexplo.backend.repository.DevisRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DevisService {

    private final DevisRepository repo;
    private final CircuitRepository circuitRepo;

    public DevisService(DevisRepository repo, CircuitRepository circuitRepo) {
        this.repo = repo;
        this.circuitRepo = circuitRepo;
    }

    private DevisResponseDTO toDTO(Devis d) {
        Long circuitId = (d.getCircuit() != null) ? d.getCircuit().getIdCircuit() : null;

        return new DevisResponseDTO(
                d.getIdDevis(),
                d.getNom(),
                d.getPrenom(),
                d.getEmail(),
                d.getTelephone(),
                d.getMessage(),
                circuitId
        );
    }

    private Devis fromDTO(DevisRequestDTO dto) {

        Devis d = new Devis();
        d.setNom(dto.getNom());
        d.setPrenom(dto.getPrenom());
        d.setEmail(dto.getEmail());
        d.setTelephone(dto.getTelephone());
        d.setMessage(dto.getMessage());

        if (dto.getCircuitId() != null) {
            Circuit circuit = circuitRepo.findById(dto.getCircuitId())
                    .orElseThrow(() -> new RuntimeException("Circuit non trouvé"));
            d.setCircuit(circuit);
        }

        return d;
    }

    public DevisResponseDTO create(DevisRequestDTO dto) {
        Devis saved = repo.save(fromDTO(dto));
        return toDTO(saved);
    }

    public List<DevisResponseDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
