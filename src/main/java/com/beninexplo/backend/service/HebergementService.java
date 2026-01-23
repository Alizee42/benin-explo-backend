package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.HebergementDTO;
import com.beninexplo.backend.entity.Hebergement;
import com.beninexplo.backend.repository.HebergementRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HebergementService {

    private final HebergementRepository repo;

    public HebergementService(HebergementRepository repo) {
        this.repo = repo;
    }

    private HebergementDTO toDTO(Hebergement h) {
        return new HebergementDTO(
                h.getIdHebergement(),
                h.getNom(),
                h.getType(),
                h.getLocalisation(),
                h.getQuartier(),
                h.getDescription(),
                h.getPrixParNuit(),
                h.getImageUrls()
        );
    }

    private Hebergement fromDTO(HebergementDTO dto) {
        Hebergement h = new Hebergement();

        System.out.println("[HebergementService#fromDTO] DTO en entrée : id=" + dto.getId());

        // Si l'ID est null ou 0, on le laisse à null pour indiquer une nouvelle entité
        if (dto.getId() != null && dto.getId() > 0) {
            h.setIdHebergement(dto.getId());
            System.out.println("[HebergementService#fromDTO] ID copié vers entité : " + dto.getId());
        } else {
            h.setIdHebergement(null);
            System.out.println("[HebergementService#fromDTO] ID du DTO null ou <=0, entité.idHebergement=null");
        }
        h.setNom(dto.getNom());
        h.setType(dto.getType());
        h.setLocalisation(dto.getLocalisation());
        h.setQuartier(dto.getQuartier());
        h.setDescription(dto.getDescription());
        h.setPrixParNuit(dto.getPrixParNuit());
        h.setImageUrls(dto.getImageUrls() != null ? dto.getImageUrls() : new ArrayList<>());

        return h;
    }

    public List<HebergementDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public HebergementDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public HebergementDTO create(HebergementDTO dto) {
        System.out.println("[HebergementService#create] DTO initial pour création : id=" + dto.getId());

        // On force systématiquement l'ID du DTO à null pour une création
        dto.setId(null);
        System.out.println("[HebergementService#create] ID du DTO après setId(null) : " + dto.getId());

        // Construction de l'entité à partir du DTO en s'assurant que l'ID est bien null
        Hebergement h = fromDTO(dto);
        System.out.println("[HebergementService#create] Entité construite avant forçage id : idHebergement=" + h.getIdHebergement());
        h.setIdHebergement(null);
        System.out.println("[HebergementService#create] Entité après forçage idHebergement=null");

        Hebergement saved = repo.save(h);
        System.out.println("[HebergementService#create] Entité sauvegardée, idHebergement généré=" + saved.getIdHebergement());
        return toDTO(saved);
    }

    public HebergementDTO update(Long id, HebergementDTO dto) {
        Hebergement existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Hébergement non trouvé"));

        existing.setNom(dto.getNom());
        existing.setType(dto.getType());
        existing.setLocalisation(dto.getLocalisation());
        existing.setQuartier(dto.getQuartier());
        existing.setDescription(dto.getDescription());
        existing.setPrixParNuit(dto.getPrixParNuit());
        existing.setImageUrls(dto.getImageUrls());

        return toDTO(repo.save(existing));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
