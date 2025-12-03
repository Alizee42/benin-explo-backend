package com.beninexplo.backend.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileController {

    private final Path uploadPath = Paths.get("uploads");

    // Accepte les sous-dossiers (ex: src/uploads/avis/image.jpg)
    @GetMapping({"/images/{folder}/{filename:.+}", "/images/{filename:.+}"})
    public ResponseEntity<Resource> getImage(
            @PathVariable(required = false) String folder,
            @PathVariable String filename) {
        try {
            Path file = (folder == null || folder.isBlank()) ? uploadPath.resolve(filename) : uploadPath.resolve(folder).resolve(filename);
            file = file.normalize();

            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(file);

            return ResponseEntity.ok()
                    .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                    .body(resource);

        } catch (Exception e) {
            System.err.println("Erreur accès fichier: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
