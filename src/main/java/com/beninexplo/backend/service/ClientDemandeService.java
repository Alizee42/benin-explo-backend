package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ClientDemandeDTO;
import com.beninexplo.backend.entity.ClientDemande;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.repository.ClientDemandeRepository;
import com.beninexplo.backend.repository.CircuitRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientDemandeService {

    private final ClientDemandeRepository repo;
    private final CircuitRepository circuitRepo;

    public ClientDemandeService(ClientDemandeRepository repo,
                                CircuitRepository circuitRepo) {
        this.repo = repo;
        this.circuitRepo = circuitRepo;
    }

    private ClientDemandeDTO toDTO(ClientDemande d) {

        Long circuitId = d.getCircuit() != null ? d.getCircuit().getIdCircuit() : null;

        return new ClientDemandeDTO(
                d.getIdDemande(),
                d.getNom(),
                d.getPrenom(),
                d.getEmail(),
                d.getTelephone(),
                d.getMessage(),
                circuitId
        );
    }

    private ClientDemande fromDTO(ClientDemandeDTO dto) {

        ClientDemande d = new ClientDemande();

        d.setIdDemande(dto.getId());
        d.setNom(dto.getNom());
        d.setPrenom(dto.getPrenom());
        d.setEmail(dto.getEmail());
        d.setTelephone(dto.getTelephone());
        d.setMessage(dto.getMessage());

        // Circuit sélectionné par le client (facultatif)
        if (dto.getCircuitId() != null) {
            Circuit circuit = circuitRepo.findById(dto.getCircuitId())
                    .orElseThrow(() -> new RuntimeException("Circuit non trouvé"));
            d.setCircuit(circuit);
        }

        return d;
    }

    public List<ClientDemandeDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ClientDemandeDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public ClientDemandeDTO create(ClientDemandeDTO dto) {
        ClientDemande saved = repo.save(fromDTO(dto));
        return toDTO(saved);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
