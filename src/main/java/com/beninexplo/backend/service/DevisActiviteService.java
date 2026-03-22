package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.DevisActiviteDTO;
import com.beninexplo.backend.entity.Activite;
import com.beninexplo.backend.entity.Devis;
import com.beninexplo.backend.entity.DevisActivite;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.ActiviteRepository;
import com.beninexplo.backend.repository.DevisActiviteRepository;
import com.beninexplo.backend.repository.DevisRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class DevisActiviteService {

    private final DevisActiviteRepository repo;
    private final DevisRepository devisRepo;
    private final ActiviteRepository activiteRepo;

    public DevisActiviteService(DevisActiviteRepository repo,
                                DevisRepository devisRepo,
                                ActiviteRepository activiteRepo) {
        this.repo = repo;
        this.devisRepo = devisRepo;
        this.activiteRepo = activiteRepo;
    }

    private DevisActiviteDTO toDTO(DevisActivite devisActivite) {
        return new DevisActiviteDTO(
                devisActivite.getId(),
                devisActivite.getDevis().getIdDevis(),
                devisActivite.getActivite().getIdActivite(),
                devisActivite.getQuantite()
        );
    }

    private DevisActivite fromDTO(DevisActiviteDTO dto) {
        Devis devis = devisRepo.findById(dto.getDevisId())
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouve."));
        Activite activite = activiteRepo.findById(dto.getActiviteId())
                .orElseThrow(() -> new ResourceNotFoundException("Activite non trouvee."));

        DevisActivite devisActivite = new DevisActivite();
        devisActivite.setId(dto.getId());
        devisActivite.setQuantite(dto.getQuantite());
        devisActivite.setDevis(devis);
        devisActivite.setActivite(activite);
        return devisActivite;
    }

    public DevisActiviteDTO create(DevisActiviteDTO dto) {
        return toDTO(repo.save(fromDTO(dto)));
    }

    public List<DevisActiviteDTO> getByDevis(Long devisId) {
        return repo.findByDevis_IdDevis(devisId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
