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
        // prefer the linked Ville entity name if present
        if (entity.getVilleEntity() != null) {
            dto.setVille(entity.getVilleEntity().getNom());
            dto.setVilleId(entity.getVilleEntity().getIdVille());
        } else {
            dto.setVille(entity.getVille());
            dto.setVilleId(null);
        }
        dto.setDureeInterne(entity.getDureeInterne());
        dto.setPoids(entity.getPoids());
        dto.setDifficulte(entity.getDifficulte());
        dto.setZoneId(zoneId);
        dto.setImagePrincipaleId(imagePrincipaleId);
        // also include the media URL when available to avoid extra round-trips from frontend
        if (imagePrincipaleId != null) {
            String url = mediaRepo.findById(imagePrincipaleId).map(m -> m.getUrl()).orElse(null);
            dto.setImagePrincipaleUrl(url);
        } else {
            dto.setImagePrincipaleUrl(null);
        }

        return dto;
    }

    // ----------------------------------------------------
    // MAPPER DTO -> ENTITY
    // ----------------------------------------------------
    private Activite fromDTO(ActiviteDTO dto, Activite entity) {

        entity.setNom(dto.getNom());
        entity.setDescription(dto.getDescription());
        // keep the textual ville for backwards compatibility
        entity.setVille(dto.getVille());
        // attempt to resolve Ville entity from dto.villeId or dto.ville
        Ville ville = null;
        if (dto.getVilleId() != null) {
            ville = villeRepo.findById(dto.getVilleId()).orElse(null);
        } else if (dto.getVille() != null && !dto.getVille().trim().isEmpty()) {
            // try to find by name ignoring case
            ville = villeRepo.findByNomIgnoreCase(dto.getVille().trim()).orElse(null);
        }
        if (ville != null) {
            entity.setVilleEntity(ville);
            // if ville has a zone, propagate it to activite.zone
            if (ville.getZone() != null) {
                entity.setZone(ville.getZone());
            }
        } else {
            entity.setVilleEntity(null);
        }
        entity.setDureeInterne(dto.getDureeInterne());
        entity.setPoids(dto.getPoids());
        entity.setDifficulte(dto.getDifficulte());

        // zone is either already set from ville resolution above, or explicit zoneId in DTO overrides
        if (dto.getZoneId() != null) {
            Zone zone = zoneRepo.findById(dto.getZoneId()).orElse(null);
            entity.setZone(zone);
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
