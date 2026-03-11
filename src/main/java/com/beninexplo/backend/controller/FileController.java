package com.beninexplo.backend.controller;

import com.beninexplo.backend.exception.ResourceNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileController {

    private final Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();

    @GetMapping({"/images/{folder}/{filename:.+}", "/images/{filename:.+}"})
    public ResponseEntity<Resource> getImage(@PathVariable(required = false) String folder,
                                             @PathVariable String filename) {
        Path file = (folder == null || folder.isBlank())
                ? uploadPath.resolve(filename)
                : uploadPath.resolve(folder).resolve(filename);
        file = file.toAbsolutePath().normalize();

        if (!file.startsWith(uploadPath)) {
            throw new ResourceNotFoundException("Image introuvable.");
        }

        try {
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Image introuvable.");
            }

            String contentType = Files.probeContentType(file);
            return ResponseEntity.ok()
                    .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                    .body(resource);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire l'image demandee.", e);
        }
    }
}
