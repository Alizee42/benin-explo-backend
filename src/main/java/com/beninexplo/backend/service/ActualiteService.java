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

    // Taille de l'extrait de contenu renvoye dans la liste publique (au lieu du contenu complet,
    // jusqu'a 20000 caracteres) : seul un resume court est affiche dans la liste, le contenu
    // integral n'est utile que sur la page detail. Trouve en audit (gaspillage de bande passante).
    private static final int CONTENU_EXCERPT_LENGTH = 300;

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

    private void fillEntity(Actualite actualite, ActualiteDTO dto, boolean isCreate) {
        actualite.setTitre(dto.getTitre());
        actualite.setContenu(dto.getContenu());
        actualite.setResume(dto.getResume() == null || dto.getResume().isBlank() ? null : dto.getResume().trim());

        if (dto.getDatePublication() != null && !dto.getDatePublication().isBlank()) {
            actualite.setDatePublication(LocalDateTime.parse(dto.getDatePublication()));
        } else if (isCreate) {
            // A la creation, une date vide signifie "publier immediatement".
            actualite.setDatePublication(LocalDateTime.now());
        }
        // En modification, une date vide ne doit pas ecraser silencieusement la date existante
        // (bug trouve en audit) : on laisse actualite.datePublication inchangee.
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
        // Petite marge de tolerance : une actualite tout juste creee/publiee (datePublication
        // par defaut = now() au moment du save) ne doit pas risquer d'etre exclue a cause d'un
        // ecart de precision entre l'horloge Java et le stockage du timestamp en base.
        LocalDateTime now = LocalDateTime.now().plusSeconds(1);
        return repo.findAllPublishedOrdered(now).stream()
                .map(this::toDTO)
                .map(this::withExcerptContenu)
                .collect(Collectors.toList());
    }

    private ActualiteDTO withExcerptContenu(ActualiteDTO dto) {
        String contenu = dto.getContenu();
        if (contenu != null && contenu.length() > CONTENU_EXCERPT_LENGTH) {
            dto.setContenu(contenu.substring(0, CONTENU_EXCERPT_LENGTH).trim() + "...");
        }
        return dto;
    }

    public ActualiteDTO getPublished(Long id) {
        return repo.findPublishedById(id, LocalDateTime.now().plusSeconds(1))
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Actualite introuvable."));
    }

    public ActualiteDTO create(ActualiteDTO dto) {
        Actualite actualite = new Actualite();
        fillEntity(actualite, dto, true);
        return toDTO(repo.save(actualite));
    }

    public ActualiteDTO update(Long id, ActualiteDTO dto) {
        Actualite existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actualite introuvable."));
        fillEntity(existing, dto, false);
        return toDTO(repo.save(existing));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
