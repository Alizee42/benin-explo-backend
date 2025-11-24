package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.HebergementDTO;
import com.beninexplo.backend.entity.Hebergement;
import com.beninexplo.backend.repository.HebergementRepository;
import com.beninexplo.backend.repository.ZoneRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HebergementService {

    private final HebergementRepository repo;
    private final ZoneRepository zoneRepo;

    public HebergementService(HebergementRepository repo, ZoneRepository zoneRepo) {
        this.repo = repo;
        this.zoneRepo = zoneRepo;
    }

    private HebergementDTO toDTO(Hebergement h) {
        HebergementDTO dto = new HebergementDTO();

        dto.setId(h.getIdHebergement());
        dto.setNom(h.getNom());
        dto.setType(h.getType());
        dto.setDescription(h.getDescription());

        if (h.getPrixParNuit() != null)
            dto.setPrixParNuit(h.getPrixParNuit().toString());

        dto.setActif(h.isActif());

        if (h.getZone() != null)
            dto.setZoneId(h.getZone().getIdZone());

        return dto;
    }

    private Hebergement fromDTO(HebergementDTO dto) {
        Hebergement h = new Hebergement();

        h.setIdHebergement(dto.getId());
        h.setNom(dto.getNom());
        h.setType(dto.getType());
        h.setDescription(dto.getDescription());

        if (dto.getPrixParNuit() != null)
            h.setPrixParNuit(new BigDecimal(dto.getPrixParNuit()));

        if (dto.getZoneId() != null)
            h.setZone(zoneRepo.findById(dto.getZoneId()).orElse(null));

        return h;
    }

    public List<HebergementDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public HebergementDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public HebergementDTO create(HebergementDTO dto) {
        return toDTO(repo.save(fromDTO(dto)));
    }

    public HebergementDTO update(Long id, HebergementDTO dto) {
        dto.setId(id);
        return toDTO(repo.save(fromDTO(dto)));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
