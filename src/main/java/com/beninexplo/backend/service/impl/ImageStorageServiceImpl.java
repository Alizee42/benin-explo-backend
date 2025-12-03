package com.beninexplo.backend.service.impl;

import com.beninexplo.backend.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    @Value("${image.storage.location}")
    private String storageLocation; // expects a value like file:src/uploads/

    private Path getBasePath() {
        String loc = storageLocation;
        if (loc.startsWith("file:")) {
            loc = loc.substring(5);
        }
        return Paths.get(loc).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file, String folder) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // basic MIME check
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // limit size to 10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File too large (max 10MB)");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int idx = original.lastIndexOf('.');
        if (idx >= 0) {
            ext = original.substring(idx);
        }

        String filename = UUID.randomUUID().toString() + ext;

        Path folderPath = getBasePath().resolve(folder == null || folder.isBlank() ? "" : folder).normalize();
        try {
            Files.createDirectories(folderPath);
            Path target = folderPath.resolve(filename);
            Files.copy(file.getInputStream(), target);
            return filename;
        } catch (IOException e) {
            throw new IOException("Failed to store file", e);
        }
    }
}
