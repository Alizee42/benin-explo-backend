package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.VilleDTO;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.entity.Zone;
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

    // ---------------------------------------
    // DTO MAPPING
    // ---------------------------------------

    private VilleDTO toDTO(Ville v) {
        if (v == null) return null;

        VilleDTO dto = new VilleDTO();
        dto.setId(v.getIdVille());
        dto.setNom(v.getNom());
        if (v.getZone() != null) {
            dto.setZoneId(v.getZone().getIdZone());
            dto.setZoneNom(v.getZone().getNom());
        }
        return dto;
    }

    private Ville toEntity(VilleDTO dto) {
        Ville v = new Ville();
        v.setIdVille(dto.getId());
        v.setNom(dto.getNom());
        if (dto.getZoneId() != null) {
            Zone zone = zoneRepo.findById(dto.getZoneId()).orElse(null);
            v.setZone(zone);
        }
        return v;
    }

    // ---------------------------------------
    // CRUD
    // ---------------------------------------

    public List<VilleDTO> getAll() {
        return villeRepo.findAllByOrderByNomAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public VilleDTO getById(Long id) {
        return villeRepo.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public VilleDTO create(VilleDTO dto) {
        Ville ville = toEntity(dto);
        ville.setIdVille(null); // Pour création
        Ville saved = villeRepo.save(ville);
        return toDTO(saved);
    }

    public VilleDTO update(Long id, VilleDTO dto) {
        Ville existing = villeRepo.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setNom(dto.getNom());
        if (dto.getZoneId() != null) {
            Zone zone = zoneRepo.findById(dto.getZoneId()).orElse(null);
            existing.setZone(zone);
        }

        Ville saved = villeRepo.save(existing);
        return toDTO(saved);
    }

    public void delete(Long id) {
        villeRepo.deleteById(id);
    }

    // ---------------------------------------
    // FILTRES
    // ---------------------------------------

    public List<VilleDTO> getByZone(Long zoneId) {
        return villeRepo.findByZoneIdZone(zoneId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}