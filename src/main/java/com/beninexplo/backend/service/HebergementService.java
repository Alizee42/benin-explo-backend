package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.HebergementDTO;
import com.beninexplo.backend.entity.Hebergement;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.HebergementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class HebergementService {

    private static final Logger log = LoggerFactory.getLogger(HebergementService.class);

    private final HebergementRepository repo;

    public HebergementService(HebergementRepository repo) {
        this.repo = repo;
    }

    private HebergementDTO toDTO(Hebergement hebergement) {
        return new HebergementDTO(
                hebergement.getIdHebergement(),
                hebergement.getNom(),
                hebergement.getType(),
                hebergement.getLocalisation(),
                hebergement.getQuartier(),
                hebergement.getDescription(),
                hebergement.getPrixParNuit(),
                hebergement.getImageUrls()
        );
    }

    private Hebergement fromDTO(HebergementDTO dto) {
        Hebergement hebergement = new Hebergement();
        if (dto.getId() != null && dto.getId() > 0) {
            hebergement.setIdHebergement(dto.getId());
        }
        hebergement.setNom(dto.getNom());
        hebergement.setType(dto.getType());
        hebergement.setLocalisation(dto.getLocalisation());
        hebergement.setQuartier(dto.getQuartier());
        hebergement.setDescription(dto.getDescription());
        hebergement.setPrixParNuit(dto.getPrixParNuit());
        hebergement.setImageUrls(dto.getImageUrls() != null ? dto.getImageUrls() : new ArrayList<>());
        return hebergement;
    }

    public List<HebergementDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public HebergementDTO get(Long id) {
        return repo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Hebergement non trouve."));
    }

    public HebergementDTO create(HebergementDTO dto) {
        log.debug("Creation hebergement, ID initial du DTO: {}", dto.getId());
        dto.setId(null);

        Hebergement hebergement = fromDTO(dto);
        hebergement.setIdHebergement(null);
        Hebergement saved = repo.save(hebergement);

        log.info("Hebergement cree avec succes, ID: {}", saved.getIdHebergement());
        return toDTO(saved);
    }

    public HebergementDTO update(Long id, HebergementDTO dto) {
        Hebergement existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hebergement non trouve."));

        existing.setNom(dto.getNom());
        existing.setType(dto.getType());
        existing.setLocalisation(dto.getLocalisation());
        existing.setQuartier(dto.getQuartier());
        existing.setDescription(dto.getDescription());
        existing.setPrixParNuit(dto.getPrixParNuit());
        existing.setImageUrls(dto.getImageUrls());

        return toDTO(repo.save(existing));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
