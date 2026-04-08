package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ActualiteDTO;
import com.beninexplo.backend.entity.Actualite;
import com.beninexplo.backend.entity.Media;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.ActualiteRepository;
import com.beninexplo.backend.repository.MediaRepository;
import com.beninexplo.backend.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
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

    private ActualiteDTO toDTO(Actualite actualite) {
        String imageUrl = actualite.getImagePrincipale() != null
                ? actualite.getImagePrincipale().getUrl()
                : actualite.getImageUrl();
        String auteurNom = actualite.getAuteur() != null
                ? (actualite.getAuteur().getPrenom() + " " + actualite.getAuteur().getNom()).trim()
                : null;

        return new ActualiteDTO(
                actualite.getIdActualite(),
                actualite.getTitre(),
                actualite.getContenu(),
                actualite.getResume(),
                actualite.getDatePublication() != null ? actualite.getDatePublication().toString() : null,
                actualite.isALaUne(),
                actualite.isPubliee(),
                actualite.getImagePrincipale() != null ? actualite.getImagePrincipale().getIdMedia() : null,
                imageUrl,
                actualite.getAuteur() != null ? actualite.getAuteur().getId() : null,
                auteurNom
        );
    }

    private void fillEntity(Actualite actualite, ActualiteDTO dto) {
        actualite.setTitre(dto.getTitre());
        actualite.setContenu(dto.getContenu());
        actualite.setResume(dto.getResume() == null || dto.getResume().isBlank() ? null : dto.getResume().trim());
        actualite.setDatePublication(dto.getDatePublication() == null || dto.getDatePublication().isBlank()
                ? LocalDateTime.now()
                : LocalDateTime.parse(dto.getDatePublication()));
        actualite.setALaUne(dto.isALaUne());
        actualite.setPubliee(dto.isPubliee());
        actualite.setImageUrl(dto.getImageUrl() == null || dto.getImageUrl().isBlank() ? null : dto.getImageUrl().trim());

        if (dto.getImagePrincipaleId() != null) {
            Media media = mediaRepo.findById(dto.getImagePrincipaleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Media introuvable."));
            actualite.setImagePrincipale(media);
        } else {
            actualite.setImagePrincipale(null);
        }

        if (dto.getAuteurId() != null) {
            Utilisateur auteur = utilisateurRepo.findById(dto.getAuteurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Auteur introuvable."));
            actualite.setAuteur(auteur);
        } else {
            actualite.setAuteur(null);
        }
    }

    public List<ActualiteDTO> getAllAdmin() {
        return repo.findAllOrderedForAdmin().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ActualiteDTO getAdmin(Long id) {
        return repo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Actualite introuvable."));
    }

    public List<ActualiteDTO> getPublished() {
        return repo.findAllPublishedOrdered().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ActualiteDTO getPublished(Long id) {
        return repo.findPublishedById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Actualite introuvable."));
    }

    public ActualiteDTO create(ActualiteDTO dto) {
        Actualite actualite = new Actualite();
        fillEntity(actualite, dto);
        return toDTO(repo.save(actualite));
    }

    public ActualiteDTO update(Long id, ActualiteDTO dto) {
        Actualite existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actualite introuvable."));
        fillEntity(existing, dto);
        return toDTO(repo.save(existing));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
