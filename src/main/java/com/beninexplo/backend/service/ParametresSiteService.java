package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ParametresSiteDTO;
import com.beninexplo.backend.entity.ParametresSite;
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

    private ParametresSiteDTO toDTO(ParametresSite p) {
        return new ParametresSiteDTO(
                p.getIdParametres(),
                p.getEmailContact(),
                p.getTelephoneContact(),
                p.getAdresseAgence()
        );
    }

    private ParametresSite fromDTO(ParametresSiteDTO dto) {
        ParametresSite p = new ParametresSite();

        p.setIdParametres(dto.getId());
        p.setEmailContact(dto.getEmailContact());
        p.setTelephoneContact(dto.getTelephoneContact());
        p.setAdresseAgence(dto.getAdresseAgence());

        return p;
    }

    public List<ParametresSiteDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ParametresSiteDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public ParametresSiteDTO saveOrUpdate(ParametresSiteDTO dto) {
        ParametresSite saved = repo.save(fromDTO(dto));
        return toDTO(saved);
    }
}
