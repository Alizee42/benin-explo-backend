package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.DevisActiviteDTO;
import com.beninexplo.backend.entity.Devis;
import com.beninexplo.backend.entity.DevisActivite;
import com.beninexplo.backend.entity.Activite;
import com.beninexplo.backend.repository.DevisActiviteRepository;
import com.beninexplo.backend.repository.DevisRepository;
import com.beninexplo.backend.repository.ActiviteRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
public class DevisActiviteService {

    private final DevisActiviteRepository repo;
    private final DevisRepository devisRepo;
    private final ActiviteRepository activiteRepo;

    public DevisActiviteService(
            DevisActiviteRepository repo,
            DevisRepository devisRepo,
            ActiviteRepository activiteRepo
    ) {
        this.repo = repo;
        this.devisRepo = devisRepo;
        this.activiteRepo = activiteRepo;
    }

    // -------------------------------
    // Conversion Entity -> DTO
    // -------------------------------
    private DevisActiviteDTO toDTO(DevisActivite da) {
        DevisActiviteDTO dto = new DevisActiviteDTO();

        dto.setId(da.getIdDevisActivite());
        dto.setOrdre(da.getOrdre());

        if (da.getDevis() != null)
            dto.setDevisId(da.getDevis().getIdDevis());

        if (da.getActivite() != null)
            dto.setActiviteId(da.getActivite().getIdActivite());

        return dto;
    }

    // -------------------------------
    // Conversion DTO -> Entity
    // -------------------------------
    private DevisActivite fromDTO(DevisActiviteDTO dto) {

        DevisActivite da = new DevisActivite();

        da.setIdDevisActivite(dto.getId());
        da.setOrdre(dto.getOrdre());

        if (dto.getDevisId() != null) {
            Long devisId = dto.getDevisId();
            Objects.requireNonNull(devisId);
            Devis devis = devisRepo.findById(devisId)
                .orElseThrow(() -> new RuntimeException("Devis introuvable : " + devisId));
            da.setDevis(devis);
        }

        if (dto.getActiviteId() != null) {
            Long activiteId = dto.getActiviteId();
            Objects.requireNonNull(activiteId);
            Activite activite = activiteRepo.findById(activiteId)
                .orElseThrow(() -> new RuntimeException("Activité introuvable : " + activiteId));
            da.setActivite(activite);
        }

        return da;
    }

    // -------------------------------
    // CRUD
    // -------------------------------
    public List<DevisActiviteDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DevisActiviteDTO get(Long id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        return repo.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public List<DevisActiviteDTO> getByDevis(Long devisId) {
        return repo.findByDevis_IdDevis(devisId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DevisActiviteDTO create(DevisActiviteDTO dto) {
        DevisActivite saved = repo.save(fromDTO(dto));
        return toDTO(saved);
    }

    public DevisActiviteDTO update(Long id, DevisActiviteDTO dto) {

        if (!repo.existsById(id)) {
            throw new RuntimeException("DevisActivite introuvable : " + id);
        }

        dto.setId(id);

        DevisActivite updated = repo.save(fromDTO(dto));
        return toDTO(updated);
    }

    public void delete(Long id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        if (!repo.existsById(id))
            throw new RuntimeException("DevisActivite introuvable : " + id);

        repo.deleteById(id);
    }
}

