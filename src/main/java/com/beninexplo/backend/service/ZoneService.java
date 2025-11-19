package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ZoneDTO;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.repository.ZoneRepository;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZoneService {

    private final ZoneRepository repo;

    public ZoneService(ZoneRepository repo) {
        this.repo = repo;
    }

    private ZoneDTO toDTO(Zone z) {
        ZoneDTO dto = new ZoneDTO();
        dto.setId(z.getIdZone());
        dto.setNom(z.getNom());
        dto.setDescription(z.getDescription());
        return dto;
    }

    private Zone fromDTO(ZoneDTO dto) {
        Zone z = new Zone();
        z.setIdZone(dto.getId());
        z.setNom(dto.getNom());
        z.setDescription(dto.getDescription());
        return z;
    }

    public List<ZoneDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ZoneDTO get(Long id) {
        return repo.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public ZoneDTO create(ZoneDTO dto) {
        return toDTO(repo.save(fromDTO(dto)));
    }

    public ZoneDTO update(Long id, ZoneDTO dto) {
        dto.setId(id);
        return toDTO(repo.save(fromDTO(dto)));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
