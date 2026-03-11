package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.VilleDTO;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.VilleRepository;
import com.beninexplo.backend.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VilleService {

    private final VilleRepository villeRepo;
    private final ZoneRepository zoneRepo;

    public VilleService(VilleRepository villeRepo, ZoneRepository zoneRepo) {
        this.villeRepo = villeRepo;
        this.zoneRepo = zoneRepo;
    }

    private VilleDTO toDTO(Ville ville) {
        VilleDTO dto = new VilleDTO();
        dto.setId(ville.getIdVille());
        dto.setNom(ville.getNom());
        if (ville.getZone() != null) {
            dto.setZoneId(ville.getZone().getIdZone());
            dto.setZoneNom(ville.getZone().getNom());
        }
        return dto;
    }

    private Ville toEntity(VilleDTO dto) {
        Ville ville = new Ville();
        ville.setIdVille(dto.getId());
        ville.setNom(dto.getNom());
        if (dto.getZoneId() != null) {
            Zone zone = zoneRepo.findById(dto.getZoneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Zone introuvable."));
            ville.setZone(zone);
        }
        return ville;
    }

    public List<VilleDTO> getAll() {
        return villeRepo.findAllByOrderByNomAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public VilleDTO getById(Long id) {
        return villeRepo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable."));
    }

    public VilleDTO create(VilleDTO dto) {
        Ville ville = toEntity(dto);
        ville.setIdVille(null);
        return toDTO(villeRepo.save(ville));
    }

    public VilleDTO update(Long id, VilleDTO dto) {
        Ville existing = villeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable."));

        existing.setNom(dto.getNom());
        if (dto.getZoneId() != null) {
            Zone zone = zoneRepo.findById(dto.getZoneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Zone introuvable."));
            existing.setZone(zone);
        } else {
            existing.setZone(null);
        }

        return toDTO(villeRepo.save(existing));
    }

    public void delete(Long id) {
        villeRepo.deleteById(id);
    }

    public List<VilleDTO> getByZone(Long zoneId) {
        return villeRepo.findByZoneIdZone(zoneId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
