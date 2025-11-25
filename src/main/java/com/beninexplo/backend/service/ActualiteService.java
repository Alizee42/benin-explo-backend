package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ActualiteDTO;
import com.beninexplo.backend.entity.Actualite;
import com.beninexplo.backend.entity.Media;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.repository.ActualiteRepository;
import com.beninexplo.backend.repository.MediaRepository;
import com.beninexplo.backend.repository.UtilisateurRepository;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActualiteService {

    private final ActualiteRepository repo;
    private final MediaRepository mediaRepo;
    private final UtilisateurRepository utilisateurRepo;

    public ActualiteService(ActualiteRepository repo,
                            MediaRepository mediaRepo,
                            UtilisateurRepository utilisateurRepo) {
        this.repo = repo;
        this.mediaRepo = mediaRepo;
        this.utilisateurRepo = utilisateurRepo;
    }

    // ---------------------- DTO MAPPER ----------------------

    private ActualiteDTO toDTO(Actualite a) {
        return new ActualiteDTO(
                a.getIdActualite(),
                a.getTitre(),
                a.getContenu(),
                a.getDatePublication() != null ? a.getDatePublication().toString() : null,
                a.getImagePrincipale() != null ? a.getImagePrincipale().getIdMedia() : null,
                a.getAuteur() != null ? a.getAuteur().getId() : null
        );
    }

    private void fillEntity(Actualite a, ActualiteDTO dto) {

        a.setTitre(dto.getTitre());
        a.setContenu(dto.getContenu());

        if (dto.getDatePublication() == null) {
            a.setDatePublication(LocalDateTime.now());
        } else {
            a.setDatePublication(LocalDateTime.parse(dto.getDatePublication()));
        }

        if (dto.getImagePrincipaleId() != null) {
            Media media = mediaRepo.findById(dto.getImagePrincipaleId()).orElse(null);
            a.setImagePrincipale(media);
        }

        if (dto.getAuteurId() != null) {
            Utilisateur auteur = utilisateurRepo.findById(dto.getAuteurId()).orElse(null);
            a.setAuteur(auteur);
        }
    }

    // ---------------------- CRUD ----------------------

    public List<ActualiteDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ActualiteDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public ActualiteDTO create(ActualiteDTO dto) {
        Actualite a = new Actualite();
        fillEntity(a, dto);
        return toDTO(repo.save(a));
    }

    public ActualiteDTO update(Long id, ActualiteDTO dto) {
        Actualite existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité introuvable"));

        fillEntity(existing, dto);
        return toDTO(repo.save(existing));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
