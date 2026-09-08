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

    private static final long MAX_IMAGE_UPLOAD_SIZE_BYTES = 10 * 1024 * 1024;
    private static final long MAX_VIDEO_UPLOAD_SIZE_BYTES = 100 * 1024 * 1024;

    private boolean isValidImageMagicBytes(byte[] header) {
        if (header == null || header.length < 4) return false;
        // JPEG: FF D8 FF
        if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF) return true;
        // PNG: 89 50 4E 47
        if (header[0] == (byte) 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47) return true;
        // GIF: 47 49 46 38
        if (header[0] == 0x47 && header[1] == 0x49 && header[2] == 0x46 && header[3] == 0x38) return true;
        // WebP: RIFF....WEBP (bytes 0-3 = RIFF, bytes 8-11 = WEBP)
        if (header.length >= 12
                && header[0] == 0x52 && header[1] == 0x49 && header[2] == 0x46 && header[3] == 0x46
                && header[8] == 0x57 && header[9] == 0x45 && header[10] == 0x42 && header[11] == 0x50) return true;
        return false;
    }

    private boolean isValidVideoMagicBytes(byte[] header) {
        if (header == null || header.length < 4) return false;
        // WebM / Matroska (EBML header): 1A 45 DF A3
        if (header[0] == 0x1A && header[1] == 0x45 && header[2] == (byte) 0xDF && header[3] == (byte) 0xA3) return true;
        // MP4/MOV family: 'ftyp' box starting at offset 4
        if (header.length >= 8
                && header[4] == 0x66 && header[5] == 0x74 && header[6] == 0x79 && header[7] == 0x70) return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    public MediaDTO uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Le fichier est obligatoire.");
        }

        String contentType = file.getContentType();
        boolean isImage = contentType != null && contentType.startsWith("image/");
        boolean isVideo = contentType != null && contentType.startsWith("video/");
        if (!isImage && !isVideo) {
            throw new BadRequestException("Seuls les fichiers image ou video sont autorises.");
        }

        long maxSize = isVideo ? MAX_VIDEO_UPLOAD_SIZE_BYTES : MAX_IMAGE_UPLOAD_SIZE_BYTES;
        if (file.getSize() > maxSize) {
            throw new BadRequestException(isVideo
                    ? "Le fichier video depasse la taille maximale de 100 Mo."
                    : "Le fichier image depasse la taille maximale de 10 Mo.");
        }

        try {
            byte[] header = file.getInputStream().readNBytes(12);
            boolean validContent = isVideo ? isValidVideoMagicBytes(header) : isValidImageMagicBytes(header);
            if (!validContent) {
                throw new BadRequestException(isVideo
                        ? "Le contenu du fichier ne correspond pas a une video valide."
                        : "Le contenu du fichier ne correspond pas a une image valide.");
            }
        } catch (IOException e) {
            throw new BadRequestException("Impossible de lire le fichier.");
        }

        String resourceType = isVideo ? "video" : "image";
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder",        cloudinaryFolder,
                            "resource_type", resourceType,
                            "overwrite",     false
                    )
            );

            String secureUrl = (String) uploadResult.get("secure_url");

            Media media = new Media();
            media.setUrl(secureUrl);
            media.setType(resourceType);
            media.setDescription(file.getOriginalFilename());
            return toDTO(repo.save(media));

        } catch (IOException e) {
            throw new IllegalStateException("Erreur lors de l upload sur Cloudinary.", e);
        }
    }
}
