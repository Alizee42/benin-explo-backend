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

    private CategorieActiviteDTO toDTO(CategorieActivite c) {
        CategorieActiviteDTO dto = new CategorieActiviteDTO();
        dto.setId(c.getIdCategorie());
        dto.setNom(c.getNom());
        dto.setDescription(c.getDescription());
        return dto;
    }

    private CategorieActivite fromDTO(CategorieActiviteDTO dto) {
        CategorieActivite c = new CategorieActivite();
        c.setIdCategorie(dto.getId());
        c.setNom(dto.getNom());
        c.setDescription(dto.getDescription());
        return c;
    }

    public List<CategorieActiviteDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CategorieActiviteDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public CategorieActiviteDTO create(CategorieActiviteDTO dto) {
        return toDTO(repo.save(fromDTO(dto)));
    }

    public CategorieActiviteDTO update(Long id, CategorieActiviteDTO dto) {
        dto.setId(id);
        return toDTO(repo.save(fromDTO(dto)));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
