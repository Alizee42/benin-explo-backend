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
        ParametresSiteDTO dto = new ParametresSiteDTO();
        dto.setId(p.getIdParametres());
        dto.setNomAgence(p.getNomAgence());
        dto.setMessageAccueil(p.getMessageAccueil());
        dto.setEmailContact(p.getEmailContact());
        dto.setTelephoneContact(p.getTelephoneContact());
        dto.setAdresseAgence(p.getAdresseAgence());
        dto.setCouleurPrimaire(p.getCouleurPrimaire());
        dto.setCouleurSecondaire(p.getCouleurSecondaire());
        dto.setUrlFacebook(p.getUrlFacebook());
        dto.setUrlInstagram(p.getUrlInstagram());
        return dto;
    }

    private ParametresSite fromDTO(ParametresSiteDTO dto) {
        ParametresSite p = new ParametresSite();
        p.setIdParametres(dto.getId());
        p.setNomAgence(dto.getNomAgence());
        p.setMessageAccueil(dto.getMessageAccueil());
        p.setEmailContact(dto.getEmailContact());
        p.setTelephoneContact(dto.getTelephoneContact());
        p.setAdresseAgence(dto.getAdresseAgence());
        p.setCouleurPrimaire(dto.getCouleurPrimaire());
        p.setCouleurSecondaire(dto.getCouleurSecondaire());
        p.setUrlFacebook(dto.getUrlFacebook());
        p.setUrlInstagram(dto.getUrlInstagram());
        return p;
    }

    public List<ParametresSiteDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ParametresSiteDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public ParametresSiteDTO create(ParametresSiteDTO dto) {
        return toDTO(repo.save(fromDTO(dto)));
    }

    public ParametresSiteDTO update(Long id, ParametresSiteDTO dto) {
        dto.setId(id);
        return toDTO(repo.save(fromDTO(dto)));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
