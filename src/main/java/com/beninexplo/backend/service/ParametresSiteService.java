package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ParametresSiteDTO;
import com.beninexplo.backend.entity.ParametresSite;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.ParametresSiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParametresSiteService {

    private final ParametresSiteRepository repo;

    public ParametresSiteService(ParametresSiteRepository repo) {
        this.repo = repo;
    }

    private ParametresSiteDTO toDTO(ParametresSite parametresSite) {
        return new ParametresSiteDTO(
                parametresSite.getIdParametres(),
                parametresSite.getEmailContact(),
                parametresSite.getTelephoneContact(),
                parametresSite.getAdresseAgence()
        );
    }

    private ParametresSite fromDTO(ParametresSiteDTO dto) {
        ParametresSite parametresSite = new ParametresSite();
        parametresSite.setIdParametres(dto.getId());
        parametresSite.setEmailContact(dto.getEmailContact());
        parametresSite.setTelephoneContact(dto.getTelephoneContact());
        parametresSite.setAdresseAgence(dto.getAdresseAgence());
        return parametresSite;
    }

    public List<ParametresSiteDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ParametresSiteDTO get(Long id) {
        return repo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Parametres du site introuvables."));
    }

    public ParametresSiteDTO saveOrUpdate(ParametresSiteDTO dto) {
        return toDTO(repo.save(fromDTO(dto)));
    }
}
