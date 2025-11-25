package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.HebergementDTO;
import com.beninexplo.backend.entity.Hebergement;
import com.beninexplo.backend.repository.HebergementRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HebergementService {

    private final HebergementRepository repo;

    public HebergementService(HebergementRepository repo) {
        this.repo = repo;
    }

    private HebergementDTO toDTO(Hebergement h) {
        return new HebergementDTO(
                h.getIdHebergement(),
                h.getNom(),
                h.getType(),
                h.getLocalisation(),
                h.getDescription(),
                h.getPrixParNuit()
        );
    }

    private Hebergement fromDTO(HebergementDTO dto) {
        Hebergement h = new Hebergement();

        h.setIdHebergement(dto.getId());
        h.setNom(dto.getNom());
        h.setType(dto.getType());
        h.setLocalisation(dto.getLocalisation());
        h.setDescription(dto.getDescription());
        h.setPrixParNuit(dto.getPrixParNuit());

        return h;
    }

    public List<HebergementDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public HebergementDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public HebergementDTO create(HebergementDTO dto) {
        Hebergement saved = repo.save(fromDTO(dto));
        return toDTO(saved);
    }

    public HebergementDTO update(Long id, HebergementDTO dto) {
        Hebergement existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Hébergement non trouvé"));

        existing.setNom(dto.getNom());
        existing.setType(dto.getType());
        existing.setLocalisation(dto.getLocalisation());
        existing.setDescription(dto.getDescription());
        existing.setPrixParNuit(dto.getPrixParNuit());

        return toDTO(repo.save(existing));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
