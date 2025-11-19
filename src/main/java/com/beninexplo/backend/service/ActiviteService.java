package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ActiviteDTO;
import com.beninexplo.backend.entity.*;
import com.beninexplo.backend.repository.*;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActiviteService {

    private final ActiviteRepository activiteRepo;
    private final ZoneRepository zoneRepo;
    private final CategorieActiviteRepository categorieRepo;
    private final MediaRepository mediaRepo;

    public ActiviteService(
            ActiviteRepository activiteRepo,
            ZoneRepository zoneRepo,
            CategorieActiviteRepository categorieRepo,
            MediaRepository mediaRepo
    ) {
        this.activiteRepo = activiteRepo;
        this.zoneRepo = zoneRepo;
        this.categorieRepo = categorieRepo;
        this.mediaRepo = mediaRepo;
    }

    // Convert entity → DTO
    private ActiviteDTO toDTO(Activite a) {
        ActiviteDTO dto = new ActiviteDTO();
        dto.setId(a.getIdActivite());
        dto.setNom(a.getNom());
        dto.setDescription(a.getDescription());
        dto.setVille(a.getVille());
        dto.setDureeInterne(a.getDureeInterne());
        dto.setDistanceDepuisCotonou(a.getDistanceDepuisCotonou());
        dto.setPoids(a.getPoids());
        dto.setDifficulte(a.getDifficulte());
        dto.setActif(a.isActif());

        if (a.getCategorie() != null) dto.setCategorieId(a.getCategorie().getIdCategorie());
        if (a.getZone() != null) dto.setZoneId(a.getZone().getIdZone());
        if (a.getImagePrincipale() != null) dto.setImagePrincipaleId(a.getImagePrincipale().getIdMedia());

        return dto;
    }

    // Convert DTO → entity
    private Activite fromDTO(ActiviteDTO dto) {
        Activite a = new Activite();
        a.setIdActivite(dto.getId());
        a.setNom(dto.getNom());
        a.setDescription(dto.getDescription());
        a.setVille(dto.getVille());
        a.setDureeInterne(dto.getDureeInterne());
        a.setDistanceDepuisCotonou(dto.getDistanceDepuisCotonou());
        a.setPoids(dto.getPoids());
        a.setDifficulte(dto.getDifficulte());
        a.setActif(dto.isActif());

        if (dto.getCategorieId() != null) {
            a.setCategorie(categorieRepo.findById(dto.getCategorieId()).orElse(null));
        }

        if (dto.getZoneId() != null) {
            a.setZone(zoneRepo.findById(dto.getZoneId()).orElse(null));
        }

        if (dto.getImagePrincipaleId() != null) {
            a.setImagePrincipale(mediaRepo.findById(dto.getImagePrincipaleId()).orElse(null));
        }

        return a;
    }

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
        Activite a = fromDTO(dto);
        return toDTO(activiteRepo.save(a));
    }

    public ActiviteDTO update(Long id, ActiviteDTO dto) {
        Activite existing = activiteRepo.findById(id).orElse(null);
        if (existing == null) return null;

        dto.setId(id); // for update
        Activite updated = fromDTO(dto);

        return toDTO(activiteRepo.save(updated));
    }

    public void delete(Long id) {
        activiteRepo.deleteById(id);
    }
}
