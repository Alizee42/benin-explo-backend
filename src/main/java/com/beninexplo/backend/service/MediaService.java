package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.MediaDTO;
import com.beninexplo.backend.entity.Media;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.MediaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
public class MediaService {

    private final MediaRepository repo;

    public MediaService(MediaRepository repo) {
        this.repo = repo;
    }

    private MediaDTO toDTO(Media media) {
        return new MediaDTO(
                media.getIdMedia(),
                media.getUrl(),
                media.getType(),
                media.getDescription()
        );
    }

    private Media fromDTO(MediaDTO dto) {
        Media media = new Media();
        media.setIdMedia(dto.getId());
        media.setUrl(dto.getUrl());
        media.setType(dto.getType());
        media.setDescription(dto.getDescription());
        return media;
    }

    public List<MediaDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MediaDTO get(Long id) {
        return repo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Media non trouve."));
    }

    public MediaDTO create(MediaDTO dto) {
        return toDTO(repo.save(fromDTO(dto)));
    }

    public MediaDTO update(Long id, MediaDTO dto) {
        Media existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media non trouve."));

        existing.setUrl(dto.getUrl());
        existing.setType(dto.getType());
        existing.setDescription(dto.getDescription());
        return toDTO(repo.save(existing));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public MediaDTO uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Le fichier image est obligatoire.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Seuls les fichiers image sont autorises.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String uniqueFilename = UUID.randomUUID() + extension;

        Path uploadDir = Paths.get("uploads");
        try {
            Files.createDirectories(uploadDir);
            Files.copy(file.getInputStream(), uploadDir.resolve(uniqueFilename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de sauvegarder le fichier image.", e);
        }

        Media media = new Media();
        media.setUrl("/uploads/" + uniqueFilename);
        media.setType("image");
        media.setDescription(originalFilename);
        return toDTO(repo.save(media));
    }
}
