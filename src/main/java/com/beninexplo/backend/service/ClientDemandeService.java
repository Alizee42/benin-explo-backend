package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ClientDemandeDTO;
import com.beninexplo.backend.entity.ClientDemande;
import com.beninexplo.backend.entity.Devis;
import com.beninexplo.backend.repository.ClientDemandeRepository;
import com.beninexplo.backend.repository.DevisRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
public class ClientDemandeService {

    private final ClientDemandeRepository repo;
    private final DevisRepository devisRepo;

    public ClientDemandeService(ClientDemandeRepository repo, DevisRepository devisRepo) {
        this.repo = repo;
        this.devisRepo = devisRepo;
    }

    private ClientDemandeDTO toDTO(ClientDemande c) {
        ClientDemandeDTO dto = new ClientDemandeDTO();

        dto.setId(c.getIdClientDemande());
        dto.setNom(c.getNom());
        dto.setPrenom(c.getPrenom());
        dto.setEmail(c.getEmail());
        dto.setTelephone(c.getTelephone());
        dto.setMessage(c.getMessage());

        if (c.getDevis() != null) {
            dto.setDevisId(c.getDevis().getIdDevis());
        }

        return dto;
    }

    private ClientDemande fromDTO(ClientDemandeDTO dto) {
        ClientDemande c = new ClientDemande();

        c.setIdClientDemande(dto.getId());
        c.setNom(dto.getNom());
        c.setPrenom(dto.getPrenom());
        c.setEmail(dto.getEmail());
        c.setTelephone(dto.getTelephone());
        c.setMessage(dto.getMessage());

        if (dto.getDevisId() != null) {
            Long devisId = dto.getDevisId();
            Objects.requireNonNull(devisId);
            Devis d = devisRepo.findById(devisId).orElse(null);
            c.setDevis(d);
        }

        return c;
    }

    public List<ClientDemandeDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ClientDemandeDTO get(Long id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public ClientDemandeDTO create(ClientDemandeDTO dto) {
        return toDTO(repo.save(fromDTO(dto)));
    }

    public ClientDemandeDTO update(Long id, ClientDemandeDTO dto) {
        dto.setId(id);
        return toDTO(repo.save(fromDTO(dto)));
    }

    public void delete(Long id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        repo.deleteById(id);
    }
}
