package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ZoneDTO;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZoneService {

    @Autowired
    private ZoneRepository zoneRepository;

    public ZoneDTO toDTO(Zone zone) {
        return new ZoneDTO(
                zone.getIdZone(),
                zone.getNom(),
                zone.getDescription()
        );
    }

    public ZoneDTO createZone(ZoneDTO dto) {
        Zone z = new Zone();
        z.setNom(dto.getNom());
        z.setDescription(dto.getDescription());
        zoneRepository.save(z);
        return toDTO(z);
    }

    public List<ZoneDTO> getAllZones() {
        return zoneRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ZoneDTO getZoneById(Long id) {
        Zone z = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone introuvable"));
        return toDTO(z);
    }

    public ZoneDTO updateZone(Long id, ZoneDTO dto) {
        Zone z = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone introuvable"));

        z.setNom(dto.getNom());
        z.setDescription(dto.getDescription());
        zoneRepository.save(z);

        return toDTO(z);
    }

    public void deleteZone(Long id) {
        zoneRepository.deleteById(id);
    }
}
