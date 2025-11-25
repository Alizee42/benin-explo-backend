package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ActiviteDTO;
import com.beninexplo.backend.entity.Activite;
import com.beninexplo.backend.entity.Media;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.repository.ActiviteRepository;
import com.beninexplo.backend.repository.MediaRepository;
import com.beninexplo.backend.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActiviteService {

    private final ActiviteRepository activiteRepo;
    private final ZoneRepository zoneRepo;
    private final MediaRepository mediaRepo;

    public ActiviteService(ActiviteRepository activiteRepo,
                           ZoneRepository zoneRepo,
                           MediaRepository mediaRepo) {
        this.activiteRepo = activiteRepo;
        this.zoneRepo = zoneRepo;
        this.mediaRepo = mediaRepo;
    }

    // ----------------------------------------------------
    // MAPPER ENTITY -> DTO
    // ----------------------------------------------------
    private ActiviteDTO toDTO(Activite entity) {
        if (entity == null) return null;

        Long zoneId = (entity.getZone() != null) 
                ? entity.getZone().getIdZone() 
                : null;

        Long imagePrincipaleId = (entity.getImagePrincipale() != null)
                ? entity.getImagePrincipale().getIdMedia()
                : null;

        ActiviteDTO dto = new ActiviteDTO();
        dto.setId(entity.getIdActivite());
        dto.setNom(entity.getNom());
        dto.setDescription(entity.getDescription());
        dto.setVille(entity.getVille());
        dto.setDureeInterne(entity.getDureeInterne());
        dto.setDistanceDepuisCotonou(entity.getDistanceDepuisCotonou());
        dto.setPoids(entity.getPoids());
        dto.setDifficulte(entity.getDifficulte());
        dto.setZoneId(zoneId);
        dto.setImagePrincipaleId(imagePrincipaleId);

        return dto;
    }

    // ----------------------------------------------------
    // MAPPER DTO -> ENTITY
    // ----------------------------------------------------
    private Activite fromDTO(ActiviteDTO dto, Activite entity) {

        entity.setNom(dto.getNom());
        entity.setDescription(dto.getDescription());
        entity.setVille(dto.getVille());
        entity.setDureeInterne(dto.getDureeInterne());
        entity.setDistanceDepuisCotonou(dto.getDistanceDepuisCotonou());
        entity.setPoids(dto.getPoids());
        entity.setDifficulte(dto.getDifficulte());

        if (dto.getZoneId() != null) {
            Zone zone = zoneRepo.findById(dto.getZoneId()).orElse(null);
            entity.setZone(zone);
        } else {
            entity.setZone(null);
        }

        if (dto.getImagePrincipaleId() != null) {
            Media media = mediaRepo.findById(dto.getImagePrincipaleId()).orElse(null);
            entity.setImagePrincipale(media);
        } else {
            entity.setImagePrincipale(null);
        }

        return entity;
    }

    // ----------------------------------------------------
    // CRUD
    // ----------------------------------------------------

    public List<ActiviteDTO> getAll() {
        return activiteRepo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ActiviteDTO get(Long id) {
        return activiteRepo.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public ActiviteDTO create(ActiviteDTO dto) {
        Activite entity = new Activite();
        Activite filled = fromDTO(dto, entity);
        Activite saved = activiteRepo.save(filled);
        return toDTO(saved);
    }

    public ActiviteDTO update(Long id, ActiviteDTO dto) {
        Activite existing = activiteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Activité introuvable"));

        Activite updated = fromDTO(dto, existing);
        Activite saved = activiteRepo.save(updated);

        return toDTO(saved);
    }

    public void delete(Long id) {
        activiteRepo.deleteById(id);
    }
}
