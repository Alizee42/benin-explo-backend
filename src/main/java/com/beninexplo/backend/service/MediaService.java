package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.MediaDTO;
import com.beninexplo.backend.entity.Media;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.MediaRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
public class MediaService {

    private final MediaRepository repo;
    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:benin-explo}")
    private String cloudinaryFolder;

    public MediaService(MediaRepository repo, Cloudinary cloudinary) {
        this.repo = repo;
        this.cloudinary = cloudinary;
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

    @SuppressWarnings("unchecked")
    public MediaDTO uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Le fichier image est obligatoire.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Seuls les fichiers image sont autorises.");
        }

        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder",        cloudinaryFolder,
                            "resource_type", "image",
                            "overwrite",     false
                    )
            );

            String secureUrl = (String) uploadResult.get("secure_url");

            Media media = new Media();
            media.setUrl(secureUrl);
            media.setType("image");
            media.setDescription(file.getOriginalFilename());
            return toDTO(repo.save(media));

        } catch (IOException e) {
            throw new IllegalStateException("Erreur lors de l upload sur Cloudinary.", e);
        }
    }
}
