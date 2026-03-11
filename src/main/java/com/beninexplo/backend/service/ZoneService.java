package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ZoneDTO;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZoneService {

    private final ZoneRepository zoneRepository;

    public ZoneService(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    public ZoneDTO toDTO(Zone zone) {
        return new ZoneDTO(
                zone.getIdZone(),
                zone.getNom(),
                zone.getDescription()
        );
    }

    public ZoneDTO createZone(ZoneDTO dto) {
        Zone zone = new Zone();
        zone.setNom(dto.getNom());
        zone.setDescription(dto.getDescription());
        zoneRepository.save(zone);
        return toDTO(zone);
    }

    public List<ZoneDTO> getAllZones() {
        return zoneRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ZoneDTO getZoneById(Long id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone introuvable"));
        return toDTO(zone);
    }

    public ZoneDTO updateZone(Long id, ZoneDTO dto) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone introuvable"));

        zone.setNom(dto.getNom());
        zone.setDescription(dto.getDescription());
        zoneRepository.save(zone);

        return toDTO(zone);
    }

    public void deleteZone(Long id) {
        zoneRepository.deleteById(id);
    }
}
