package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.CircuitDTO;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.entity.Media;
import com.beninexplo.backend.repository.CircuitRepository;
import com.beninexplo.backend.repository.ZoneRepository;
import com.beninexplo.backend.repository.MediaRepository;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CircuitService {

    private final CircuitRepository repo;
    private final ZoneRepository zoneRepo;
    private final MediaRepository mediaRepo;

    public CircuitService(CircuitRepository repo, ZoneRepository zoneRepo, MediaRepository mediaRepo) {
        this.repo = repo;
        this.zoneRepo = zoneRepo;
        this.mediaRepo = mediaRepo;
    }

    /* -------------------- MAPPER -------------------- */

    private CircuitDTO toDTO(Circuit c) {
        CircuitDTO dto = new CircuitDTO();
        dto.setId(c.getIdCircuit());
        dto.setNom(c.getNom());
        dto.setDescription(c.getDescription());
        dto.setDureeIndicative(c.getDureeIndicative());
        dto.setPrixIndicatif(c.getPrixIndicatif());
        dto.setFormuleProposee(c.getFormuleProposee());
        dto.setNiveau(c.getNiveau());
        dto.setActif(c.isActif());

        if (c.getZone() != null) dto.setZoneId(c.getZone().getIdZone());
        if (c.getImagePrincipale() != null) dto.setImagePrincipaleId(c.getImagePrincipale().getIdMedia());

        return dto;
    }

    /* -------------------- CRUD -------------------- */

    public List<CircuitDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CircuitDTO get(Long id) {
        return repo.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public CircuitDTO create(CircuitDTO dto) {

        Circuit c = new Circuit();
        updateEntityWithDTO(c, dto);

        repo.save(c);
        return toDTO(c);
    }

    public CircuitDTO update(Long id, CircuitDTO dto) {

        Circuit c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Circuit non trouvé"));

        updateEntityWithDTO(c, dto); // modification sur l'existant

        repo.save(c);
        return toDTO(c);
    }

    private void updateEntityWithDTO(Circuit c, CircuitDTO dto) {

        c.setNom(dto.getNom());
        c.setDescription(dto.getDescription());
        c.setDureeIndicative(dto.getDureeIndicative());
        c.setPrixIndicatif(dto.getPrixIndicatif());
        c.setFormuleProposee(dto.getFormuleProposee());
        c.setNiveau(dto.getNiveau());
        c.setActif(dto.isActif());

        // Zone
        if (dto.getZoneId() != null) {
            Zone z = zoneRepo.findById(dto.getZoneId())
                    .orElseThrow(() -> new RuntimeException("Zone introuvable"));
            c.setZone(z);
        }

        // Image principale
        if (dto.getImagePrincipaleId() != null) {
            Media img = mediaRepo.findById(dto.getImagePrincipaleId())
                    .orElseThrow(() -> new RuntimeException("Image introuvable"));
            c.setImagePrincipale(img);
        }
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
