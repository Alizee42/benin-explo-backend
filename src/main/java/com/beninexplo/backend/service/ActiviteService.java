package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ActiviteDTO;
import com.beninexplo.backend.entity.Activite;
import com.beninexplo.backend.entity.Media;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.ActiviteRepository;
import com.beninexplo.backend.repository.MediaRepository;
import com.beninexplo.backend.repository.VilleRepository;
import com.beninexplo.backend.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActiviteService {

    private final ActiviteRepository activiteRepo;
    private final ZoneRepository zoneRepo;
    private final MediaRepository mediaRepo;
    private final VilleRepository villeRepo;

    public ActiviteService(ActiviteRepository activiteRepo,
                           ZoneRepository zoneRepo,
                           MediaRepository mediaRepo,
                           VilleRepository villeRepo) {
        this.activiteRepo = activiteRepo;
        this.zoneRepo = zoneRepo;
        this.mediaRepo = mediaRepo;
        this.villeRepo = villeRepo;
    }

    private ActiviteDTO toDTO(Activite entity) {
        ActiviteDTO dto = new ActiviteDTO();
        dto.setId(entity.getIdActivite());
        dto.setNom(entity.getNom());
        dto.setDescription(entity.getDescription());

        if (entity.getVille() != null) {
            dto.setVilleId(entity.getVille().getIdVille());
            dto.setVilleNom(entity.getVille().getNom());
            Zone zone = entity.getVille().getZone();
            if (zone != null) {
                dto.setZoneId(zone.getIdZone());
                dto.setZoneNom(zone.getNom());
            }
        }

        dto.setDureeInterne(entity.getDureeInterne());
        dto.setPoids(entity.getPoids());
        dto.setDifficulte(entity.getDifficulte());

        if (entity.getImagePrincipale() != null) {
            dto.setImagePrincipaleId(entity.getImagePrincipale().getIdMedia());
            dto.setImagePrincipaleUrl(entity.getImagePrincipale().getUrl());
        }

        return dto;
    }

    private Activite fromDTO(ActiviteDTO dto, Activite entity) {
        entity.setNom(dto.getNom());
        entity.setDescription(dto.getDescription());

        if (dto.getVilleId() == null || dto.getVilleId() <= 0) {
            throw new IllegalArgumentException("Une activite doit etre associee a une ville (villeId requis).");
        }

        Ville ville = villeRepo.findById(dto.getVilleId())
                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable."));
        entity.setVille(ville);
        entity.setDureeInterne(dto.getDureeInterne());
        entity.setPoids(dto.getPoids());
        entity.setDifficulte(dto.getDifficulte());

        if (dto.getImagePrincipaleId() != null && dto.getImagePrincipaleId() > 0) {
            Media media = mediaRepo.findById(dto.getImagePrincipaleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Media introuvable."));
            entity.setImagePrincipale(media);
        } else {
            entity.setImagePrincipale(null);
        }

        return entity;
    }

    public List<ActiviteDTO> getAll() {
        return activiteRepo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ActiviteDTO get(Long id) {
        return activiteRepo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Activite introuvable."));
    }

    public ActiviteDTO create(ActiviteDTO dto) {
        return toDTO(activiteRepo.save(fromDTO(dto, new Activite())));
    }

    public ActiviteDTO update(Long id, ActiviteDTO dto) {
        Activite existing = activiteRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activite introuvable."));
        return toDTO(activiteRepo.save(fromDTO(dto, existing)));
    }

    public void delete(Long id) {
        activiteRepo.deleteById(id);
    }

    public List<ActiviteDTO> getByZone(Long zoneId) {
        zoneRepo.findById(zoneId).orElseThrow(() -> new ResourceNotFoundException("Zone introuvable."));
        return activiteRepo.findByVille_Zone_IdZone(zoneId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ActiviteDTO> getByVille(Long villeId) {
        villeRepo.findById(villeId).orElseThrow(() -> new ResourceNotFoundException("Ville introuvable."));
        return activiteRepo.findByVille_IdVille(villeId).stream().map(this::toDTO).collect(Collectors.toList());
    }
}
