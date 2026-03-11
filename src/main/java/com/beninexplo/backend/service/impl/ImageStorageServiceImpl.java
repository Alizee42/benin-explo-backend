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
