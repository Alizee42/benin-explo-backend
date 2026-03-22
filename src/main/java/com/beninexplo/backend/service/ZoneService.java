package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ZoneDTO;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.ZoneRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
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

    @CacheEvict(value = "zones", allEntries = true)
    public ZoneDTO createZone(ZoneDTO dto) {
        Zone zone = new Zone();
        zone.setNom(dto.getNom());
        zone.setDescription(dto.getDescription());
        zoneRepository.save(zone);
        return toDTO(zone);
    }

    @Cacheable("zones")
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

    @CacheEvict(value = "zones", allEntries = true)
    public ZoneDTO updateZone(Long id, ZoneDTO dto) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone introuvable"));

        zone.setNom(dto.getNom());
        zone.setDescription(dto.getDescription());
        zoneRepository.save(zone);

        return toDTO(zone);
    }

    @CacheEvict(value = "zones", allEntries = true)
    public void deleteZone(Long id) {
        zoneRepository.deleteById(id);
    }
}
