package com.beninexplo.backend.service.impl;

import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    @Value("${image.storage.location}")
    private String storageLocation;

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

    private Path getBasePath() {
        String location = storageLocation;
        if (location.startsWith("file:")) {
            location = location.substring(5);
        }
        return Paths.get(location).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Le fichier image est obligatoire.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Seuls les fichiers image sont autorises.");
        }

        try {
            byte[] header = file.getInputStream().readNBytes(12);
            if (!isValidImageMagicBytes(header)) {
                throw new BadRequestException("Le contenu du fichier ne correspond pas a une image valide.");
            }
        } catch (IOException e) {
            throw new BadRequestException("Impossible de lire le fichier image.");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BadRequestException("Le fichier image depasse la taille maximale de 10 Mo.");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int separator = original.lastIndexOf('.');
        if (separator >= 0) {
            extension = original.substring(separator);
        }

        String filename = UUID.randomUUID() + extension;
        Path folderPath = getBasePath().resolve(folder == null || folder.isBlank() ? "" : folder).normalize();

        try {
            Files.createDirectories(folderPath);
            Files.copy(file.getInputStream(), folderPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de sauvegarder l'image.", e);
        }
    }
}
