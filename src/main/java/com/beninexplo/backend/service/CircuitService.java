package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.CircuitDTO;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.CircuitRepository;
import com.beninexplo.backend.repository.VilleRepository;
import com.beninexplo.backend.repository.ZoneRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CircuitService {

    private final CircuitRepository circuitRepo;
    private final VilleRepository villeRepo;
    private final ZoneRepository zoneRepo;
    private final ObjectMapper objectMapper;

    public CircuitService(CircuitRepository circuitRepo,
                          VilleRepository villeRepo,
                          ZoneRepository zoneRepo) {
        this.circuitRepo = circuitRepo;
        this.villeRepo = villeRepo;
        this.zoneRepo = zoneRepo;
        this.objectMapper = new ObjectMapper();
    }

    private CircuitDTO toDTO(Circuit circuit) {
        CircuitDTO dto = new CircuitDTO();
        dto.setId(circuit.getIdCircuit());
        dto.setTitre(circuit.getNom());
        dto.setDescription(circuit.getDescription());
        dto.setResume(circuit.getResume());
        dto.setDureeIndicative(circuit.getDureeIndicative());
        dto.setPrixIndicatif(circuit.getPrixIndicatif());
        dto.setFormuleProposee(circuit.getFormuleProposee());
        dto.setActif(circuit.isActif());
        dto.setImg(circuit.getImg());
        dto.setGalerie(readStringList(circuit.getGalerie()));
        dto.setProgramme(readProgramme(circuit.getProgramme()));
        dto.setPointsForts(readPointForts(circuit.getPointsForts()));
        dto.setInclus(readStringList(circuit.getInclus()));
        dto.setNonInclus(readStringList(circuit.getNonInclus()));
        dto.setAventures(readStringList(circuit.getAventures()));

        if (circuit.getVille() != null) {
            dto.setVilleId(circuit.getVille().getIdVille());
            dto.setVilleNom(circuit.getVille().getNom());
            dto.setLocalisation(circuit.getVille().getNom());

            Zone zone = circuit.getVille().getZone();
            if (zone != null) {
                dto.setZoneId(zone.getIdZone());
                dto.setZoneNom(zone.getNom());
            }
        } else if (circuit.getZone() != null) {
            dto.setZoneId(circuit.getZone().getIdZone());
            dto.setZoneNom(circuit.getZone().getNom());
        }

        return dto;
    }

    private void updateEntity(Circuit circuit, CircuitDTO dto) {
        if (dto.getVilleId() == null) {
            throw new BadRequestException("La ville principale du circuit est obligatoire.");
        }

        Ville ville = villeRepo.findById(dto.getVilleId())
                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable."));

        circuit.setNom(dto.getTitre());
        circuit.setDescription(dto.getDescription());
        circuit.setResume(dto.getResume());
        circuit.setDureeIndicative(dto.getDureeIndicative());
        circuit.setPrixIndicatif(dto.getPrixIndicatif());
        circuit.setFormuleProposee(dto.getFormuleProposee());
        circuit.setActif(dto.isActif());
        circuit.setVille(ville);
        circuit.setImg(dto.getImg());
        circuit.setGalerie(writeJson(dto.getGalerie()));
        circuit.setProgramme(writeJson(dto.getProgramme()));
        circuit.setPointsForts(writeJson(dto.getPointsForts()));
        circuit.setInclus(writeJson(dto.getInclus()));
        circuit.setNonInclus(writeJson(dto.getNonInclus()));
        circuit.setAventures(writeJson(dto.getAventures()));
    }

    private List<String> readStringList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() { });
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    private List<CircuitDTO.PointFort> readPointForts(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<CircuitDTO.PointFort>>() { });
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    private List<CircuitDTO.ProgrammeDay> readProgramme(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<CircuitDTO.ProgrammeDay>>() { });
        } catch (JsonProcessingException firstError) {
            try {
                List<String> rawItems = objectMapper.readValue(json, new TypeReference<List<String>>() { });
                return rawItems.stream()
                        .map(item -> new CircuitDTO.ProgrammeDay(null, null, item, null, null, null))
                        .collect(Collectors.toList());
            } catch (JsonProcessingException secondError) {
                return Collections.emptyList();
            }
        }
    }

    private String writeJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Impossible de serialiser les donnees du circuit.", e);
        }
    }

    public List<CircuitDTO> getAll() {
        return circuitRepo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CircuitDTO getById(Long id) {
        return circuitRepo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Circuit non trouve."));
    }

    public CircuitDTO create(CircuitDTO dto) {
        Circuit circuit = new Circuit();
        updateEntity(circuit, dto);
        return toDTO(circuitRepo.save(circuit));
    }

    public CircuitDTO update(Long id, CircuitDTO dto) {
        Circuit circuit = circuitRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Circuit non trouve."));
        updateEntity(circuit, dto);
        return toDTO(circuitRepo.save(circuit));
    }

    public void delete(Long id) {
        circuitRepo.deleteById(id);
    }

    public List<CircuitDTO> getActifs() {
        return circuitRepo.findByActifTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CircuitDTO> getByZone(Long zoneId) {
        zoneRepo.findById(zoneId).orElseThrow(() -> new ResourceNotFoundException("Zone introuvable."));
        return circuitRepo.findByVille_Zone_IdZone(zoneId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CircuitDTO> searchByNom(String nom) {
        return circuitRepo.findByNomContainingIgnoreCase(nom).stream().map(this::toDTO).collect(Collectors.toList());
    }
}
