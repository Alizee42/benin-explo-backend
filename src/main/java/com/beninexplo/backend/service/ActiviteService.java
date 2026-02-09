package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ActiviteDTO;
import com.beninexplo.backend.entity.Activite;
import com.beninexplo.backend.entity.Media;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.repository.VilleRepository;
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

    // ----------------------------------------------------
    // MAPPER ENTITY -> DTO
    // ----------------------------------------------------
    private ActiviteDTO toDTO(Activite entity) {
        if (entity == null) return null;

        ActiviteDTO dto = new ActiviteDTO();
        dto.setId(entity.getIdActivite());
        dto.setNom(entity.getNom());
        dto.setDescription(entity.getDescription());
        
        // Localisation via Ville (source unique)
        if (entity.getVille() != null) {
            dto.setVilleId(entity.getVille().getIdVille());
            dto.setVilleNom(entity.getVille().getNom());
            
            // Zone via ville.getZone()
            Zone zone = entity.getVille().getZone();
            if (zone != null) {
                dto.setZoneId(zone.getIdZone());
                dto.setZoneNom(zone.getNom());
            }
        }
        
        dto.setDureeInterne(entity.getDureeInterne());
        dto.setPoids(entity.getPoids());
        dto.setDifficulte(entity.getDifficulte());
        
        // Image principale
        if (entity.getImagePrincipale() != null) {
            dto.setImagePrincipaleId(entity.getImagePrincipale().getIdMedia());
            dto.setImagePrincipaleUrl(entity.getImagePrincipale().getUrl());
        }

        return dto;
    }

    // ----------------------------------------------------
    // MAPPER DTO -> ENTITY
    // ----------------------------------------------------
    private Activite fromDTO(ActiviteDTO dto, Activite entity) {
        entity.setNom(dto.getNom());
        entity.setDescription(dto.getDescription());
        
        // Résoudre la Ville (OBLIGATOIRE)
        if (dto.getVilleId() != null && dto.getVilleId() > 0) {
            Ville ville = villeRepo.findById(dto.getVilleId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Ville introuvable avec l'ID : " + dto.getVilleId()
                ));
            entity.setVille(ville);
        } else {
            throw new IllegalArgumentException("Une activité doit être associée à une ville (villeId requis)");
        }
        
        entity.setDureeInterne(dto.getDureeInterne());
        entity.setPoids(dto.getPoids());
        entity.setDifficulte(dto.getDifficulte());

        // Image principale (optionnel)
        if (dto.getImagePrincipaleId() != null && dto.getImagePrincipaleId() > 0) {
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
