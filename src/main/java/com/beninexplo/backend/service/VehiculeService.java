package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.VehiculeDTO;
import com.beninexplo.backend.entity.Vehicule;
import com.beninexplo.backend.repository.VehiculeRepository;
import com.beninexplo.backend.repository.ZoneRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehiculeService {

    private final VehiculeRepository repo;
    private final ZoneRepository zoneRepo;

    public VehiculeService(VehiculeRepository repo, ZoneRepository zoneRepo) {
        this.repo = repo;
        this.zoneRepo = zoneRepo;
    }

    private VehiculeDTO toDTO(Vehicule v) {
        VehiculeDTO dto = new VehiculeDTO();

        dto.setId(v.getIdVehicule());
        dto.setModele(v.getModele());
        dto.setType(v.getType());

        if (v.getPrixParJour() != null)
            dto.setPrixParJour(v.getPrixParJour().toString());

        dto.setActif(v.isActif());

        if (v.getZone() != null)
            dto.setZoneId(v.getZone().getIdZone());

        return dto;
    }

    private Vehicule fromDTO(VehiculeDTO dto) {
        Vehicule v = new Vehicule();

        v.setIdVehicule(dto.getId());
        v.setModele(dto.getModele());
        v.setType(dto.getType());

        if (dto.getPrixParJour() != null)
            v.setPrixParJour(new BigDecimal(dto.getPrixParJour()));

        if (dto.getZoneId() != null)
            v.setZone(zoneRepo.findById(dto.getZoneId()).orElse(null));

        return v;
    }

    public List<VehiculeDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public VehiculeDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public VehiculeDTO create(VehiculeDTO dto) {
        return toDTO(repo.save(fromDTO(dto)));
    }

    public VehiculeDTO update(Long id, VehiculeDTO dto) {
        dto.setId(id);
        return toDTO(repo.save(fromDTO(dto)));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
