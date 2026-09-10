package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.VilleDTO;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.exception.ConflictException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.ActiviteRepository;
import com.beninexplo.backend.repository.CircuitRepository;
import com.beninexplo.backend.repository.VilleRepository;
import com.beninexplo.backend.repository.ZoneRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class VilleService {

    private final VilleRepository villeRepo;
    private final ZoneRepository zoneRepo;
    private final ActiviteRepository activiteRepo;
    private final CircuitRepository circuitRepo;

    public VilleService(VilleRepository villeRepo, ZoneRepository zoneRepo,
                        ActiviteRepository activiteRepo, CircuitRepository circuitRepo) {
        this.villeRepo = villeRepo;
        this.zoneRepo = zoneRepo;
        this.activiteRepo = activiteRepo;
        this.circuitRepo = circuitRepo;
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

    @Cacheable("villes")
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

    @CacheEvict(value = "villes", allEntries = true)
    public VilleDTO create(VilleDTO dto) {
        Ville ville = toEntity(dto);
        ville.setIdVille(null);
        return toDTO(villeRepo.save(ville));
    }

    @CacheEvict(value = "villes", allEntries = true)
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

    @CacheEvict(value = "villes", allEntries = true)
    public void delete(Long id) {
        if (!villeRepo.existsById(id)) {
            throw new ResourceNotFoundException("Ville introuvable.");
        }

        List<String> usages = new ArrayList<>();
        long activitesCount = activiteRepo.countByVille_IdVille(id);
        if (activitesCount > 0) {
            usages.add(activitesCount + " activite(s)");
        }
        long circuitsCount = circuitRepo.countByVille_IdVille(id);
        if (circuitsCount > 0) {
            usages.add(circuitsCount + " circuit(s)");
        }
        if (!usages.isEmpty()) {
            throw new ConflictException(
                    "Cette ville est utilisee par " + String.join(" et ", usages) + ". Retirez-les ou reassignez-les avant de supprimer la ville.");
        }

        villeRepo.deleteById(id);
    }

    public List<VilleDTO> getByZone(Long zoneId) {
        return villeRepo.findByZoneIdZone(zoneId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
