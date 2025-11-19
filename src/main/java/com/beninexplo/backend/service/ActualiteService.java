package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ActualiteDTO;
import com.beninexplo.backend.entity.Actualite;
import com.beninexplo.backend.repository.ActualiteRepository;
import com.beninexplo.backend.repository.MediaRepository;
import com.beninexplo.backend.repository.UtilisateurRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActualiteService {

    private final ActualiteRepository repo;
    private final MediaRepository mediaRepo;
    private final UtilisateurRepository utilisateurRepo;

    public ActualiteService(
            ActualiteRepository repo,
            MediaRepository mediaRepo,
            UtilisateurRepository utilisateurRepo
    ) {
        this.repo = repo;
        this.mediaRepo = mediaRepo;
        this.utilisateurRepo = utilisateurRepo;
    }

    private ActualiteDTO toDTO(Actualite a) {
        ActualiteDTO dto = new ActualiteDTO();
        dto.setId(a.getIdActualite());
        dto.setTitre(a.getTitre());
        dto.setContenu(a.getContenu());
        dto.setDatePublication(a.getDatePublication().toString());

        if (a.getImagePrincipale() != null)
            dto.setImagePrincipaleId(a.getImagePrincipale().getIdMedia());

        if (a.getAuteur() != null)
            dto.setAuteurId(a.getAuteur().getIdUtilisateur());

        return dto;
    }

    private Actualite fromDTO(ActualiteDTO dto) {
        Actualite a = new Actualite();
        a.setIdActualite(dto.getId());
        a.setTitre(dto.getTitre());
        a.setContenu(dto.getContenu());

        if (dto.getImagePrincipaleId() != null)
            a.setImagePrincipale(mediaRepo.findById(dto.getImagePrincipaleId()).orElse(null));

        if (dto.getAuteurId() != null)
            a.setAuteur(utilisateurRepo.findById(dto.getAuteurId()).orElse(null));

        return a;
    }

    public List<ActualiteDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ActualiteDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public ActualiteDTO create(ActualiteDTO dto) {
        Actualite a = fromDTO(dto);
        return toDTO(repo.save(a));
    }

    public ActualiteDTO update(Long id, ActualiteDTO dto) {
        dto.setId(id);
        return toDTO(repo.save(fromDTO(dto)));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
