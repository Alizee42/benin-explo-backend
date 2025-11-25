package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.CircuitDTO;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.Media;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.repository.CircuitRepository;
import com.beninexplo.backend.repository.MediaRepository;
import com.beninexplo.backend.repository.ZoneRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CircuitService {

    private final CircuitRepository circuitRepo;
    private final ZoneRepository zoneRepo;
    private final MediaRepository mediaRepo;

    public CircuitService(CircuitRepository circuitRepo,
                          ZoneRepository zoneRepo,
                          MediaRepository mediaRepo) {
        this.circuitRepo = circuitRepo;
        this.zoneRepo = zoneRepo;
        this.mediaRepo = mediaRepo;
    }

    // ---------------------------------------
    // DTO MAPPING
    // ---------------------------------------

    private CircuitDTO toDTO(Circuit c) {
        if (c == null) return null;

        CircuitDTO dto = new CircuitDTO();

        dto.setId(c.getIdCircuit());
        dto.setNom(c.getNom());
        dto.setDescription(c.getDescription());
        dto.setDureeIndicative(c.getDureeIndicative());
        dto.setPrixIndicatif(c.getPrixIndicatif());
        dto.setFormuleProposee(c.getFormuleProposee());
        dto.setNiveau(c.getNiveau());
        dto.setActif(c.isActif());

        if (c.getZone() != null)
            dto.setZoneId(c.getZone().getIdZone());

        if (c.getImagePrincipale() != null)
            dto.setImagePrincipaleId(c.getImagePrincipale().getIdMedia());

        return dto;
    }

    private void updateEntity(Circuit c, CircuitDTO dto) {

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
                    .orElseThrow(() -> new RuntimeException("Zone introuvable !"));
            c.setZone(z);
        } else {
            c.setZone(null);
        }

        // Image
        if (dto.getImagePrincipaleId() != null) {
            Media img = mediaRepo.findById(dto.getImagePrincipaleId())
                    .orElseThrow(() -> new RuntimeException("Image introuvable !"));
            c.setImagePrincipale(img);
        } else {
            c.setImagePrincipale(null);
        }
    }

    // ---------------------------------------
    // CRUD
    // ---------------------------------------

    public List<CircuitDTO> getAll() {
        return circuitRepo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CircuitDTO getById(Long id) {
        return circuitRepo.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public CircuitDTO create(CircuitDTO dto) {
        Circuit circuit = new Circuit();
        updateEntity(circuit, dto);
        circuitRepo.save(circuit);
        return toDTO(circuit);
    }

    public CircuitDTO update(Long id, CircuitDTO dto) {
        Circuit circuit = circuitRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Circuit non trouvé"));
        updateEntity(circuit, dto);
        circuitRepo.save(circuit);
        return toDTO(circuit);
    }

    public void delete(Long id) {
        circuitRepo.deleteById(id);
    }

    // ---------------------------------------
    // FILTRES & RECHERCHES
    // ---------------------------------------

    public List<CircuitDTO> getActifs() {
        return circuitRepo.findByActifTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CircuitDTO> getByZone(Long zoneId) {
        return circuitRepo.findByZone_IdZone(zoneId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CircuitDTO> searchByNom(String nom) {
        return circuitRepo.findByNomContainingIgnoreCase(nom)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
