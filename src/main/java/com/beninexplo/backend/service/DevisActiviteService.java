package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.DevisActiviteDTO;
import com.beninexplo.backend.entity.Activite;
import com.beninexplo.backend.entity.Devis;
import com.beninexplo.backend.entity.DevisActivite;
import com.beninexplo.backend.repository.ActiviteRepository;
import com.beninexplo.backend.repository.DevisRepository;
import com.beninexplo.backend.repository.DevisActiviteRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DevisActiviteService {

    private final DevisActiviteRepository repo;
    private final DevisRepository devisRepo;
    private final ActiviteRepository activiteRepo;

    public DevisActiviteService(DevisActiviteRepository repo, DevisRepository devisRepo, ActiviteRepository activiteRepo) {
        this.repo = repo;
        this.devisRepo = devisRepo;
        this.activiteRepo = activiteRepo;
    }

    private DevisActiviteDTO toDTO(DevisActivite da) {
        return new DevisActiviteDTO(
                da.getId(),
                da.getDevis().getIdDevis(),
                da.getActivite().getIdActivite(),
                da.getQuantite()
        );
    }

    private DevisActivite fromDTO(DevisActiviteDTO dto) {
        DevisActivite da = new DevisActivite();

        da.setId(dto.getId());
        da.setQuantite(dto.getQuantite());

        Devis devis = devisRepo.findById(dto.getDevisId())
                .orElseThrow(() -> new RuntimeException("Devis non trouvé"));
        da.setDevis(devis);

        Activite activite = activiteRepo.findById(dto.getActiviteId())
                .orElseThrow(() -> new RuntimeException("Activité non trouvée"));
        da.setActivite(activite);

        return da;
    }

    public DevisActiviteDTO create(DevisActiviteDTO dto) {
        DevisActivite saved = repo.save(fromDTO(dto));
        return toDTO(saved);
    }

    public List<DevisActiviteDTO> getByDevis(Long devisId) {
        return repo.findByDevis_IdDevis(devisId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
