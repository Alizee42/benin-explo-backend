package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.CategorieActiviteDTO;
import com.beninexplo.backend.entity.CategorieActivite;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.CategorieActiviteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CategorieActiviteService {

    private final CategorieActiviteRepository repo;

    public CategorieActiviteService(CategorieActiviteRepository repo) {
        this.repo = repo;
    }

    private CategorieActiviteDTO toDTO(CategorieActivite categorie) {
        return new CategorieActiviteDTO(
                categorie.getIdCategorie(),
                categorie.getNom(),
                categorie.getDescription()
        );
    }

    public List<CategorieActiviteDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CategorieActiviteDTO get(Long id) {
        return repo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Categorie d'activite introuvable."));
    }

    public CategorieActiviteDTO create(CategorieActiviteDTO dto) {
        CategorieActivite categorie = new CategorieActivite();
        categorie.setNom(dto.getNom());
        categorie.setDescription(dto.getDescription());
        return toDTO(repo.save(categorie));
    }

    public CategorieActiviteDTO update(Long id, CategorieActiviteDTO dto) {
        CategorieActivite existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categorie d'activite introuvable."));
        existing.setNom(dto.getNom());
        existing.setDescription(dto.getDescription());
        return toDTO(repo.save(existing));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
