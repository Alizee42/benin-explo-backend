package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.CategorieActiviteDTO;
import com.beninexplo.backend.entity.CategorieActivite;
import com.beninexplo.backend.repository.CategorieActiviteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategorieActiviteService {

    private final CategorieActiviteRepository repo;

    public CategorieActiviteService(CategorieActiviteRepository repo) {
        this.repo = repo;
    }

    // ----------------------------------------------------------
    // MAPPER ENTITY → DTO
    // ----------------------------------------------------------
    private CategorieActiviteDTO toDTO(CategorieActivite entity) {
        if (entity == null) return null;

        return new CategorieActiviteDTO(
                entity.getIdCategorie(),
                entity.getNom(),
                entity.getDescription()
        );
    }

    // ----------------------------------------------------------
    // MAPPER DTO → ENTITY
    // ----------------------------------------------------------
    private CategorieActivite fromDTO(CategorieActiviteDTO dto) {
        if (dto == null) return null;

        CategorieActivite c = new CategorieActivite();
        c.setIdCategorie(dto.getId());
        c.setNom(dto.getNom());
        c.setDescription(dto.getDescription());
        return c;
    }

    // ----------------------------------------------------------
    // MÉTHODES CRUD
    // ----------------------------------------------------------

    public List<CategorieActiviteDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CategorieActiviteDTO get(Long id) {
        return repo.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public CategorieActiviteDTO create(CategorieActiviteDTO dto) {
        CategorieActivite saved = repo.save(fromDTO(dto));
        return toDTO(saved);
    }

    public CategorieActiviteDTO update(Long id, CategorieActiviteDTO dto) {
        dto.setId(id);
        CategorieActivite saved = repo.save(fromDTO(dto));
        return toDTO(saved);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
