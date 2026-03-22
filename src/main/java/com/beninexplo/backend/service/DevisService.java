package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.DevisRequestDTO;
import com.beninexplo.backend.dto.DevisResponseDTO;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.Devis;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.CircuitRepository;
import com.beninexplo.backend.repository.DevisRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class DevisService {

    private final DevisRepository repo;
    private final CircuitRepository circuitRepo;

    public DevisService(DevisRepository repo, CircuitRepository circuitRepo) {
        this.repo = repo;
        this.circuitRepo = circuitRepo;
    }

    private DevisResponseDTO toDTO(Devis devis) {
        Long circuitId = devis.getCircuit() != null ? devis.getCircuit().getIdCircuit() : null;
        return new DevisResponseDTO(
                devis.getIdDevis(),
                devis.getNom(),
                devis.getPrenom(),
                devis.getEmail(),
                devis.getTelephone(),
                devis.getMessage(),
                circuitId
        );
    }

    private Devis fromDTO(DevisRequestDTO dto) {
        Devis devis = new Devis();
        devis.setNom(dto.getNom());
        devis.setPrenom(dto.getPrenom());
        devis.setEmail(dto.getEmail());
        devis.setTelephone(dto.getTelephone());
        devis.setMessage(dto.getMessage());

        if (dto.getCircuitId() != null) {
            Circuit circuit = circuitRepo.findById(dto.getCircuitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Circuit non trouve."));
            devis.setCircuit(circuit);
        }

        return devis;
    }

    public DevisResponseDTO create(DevisRequestDTO dto) {
        return toDTO(repo.save(fromDTO(dto)));
    }

    public List<DevisResponseDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
