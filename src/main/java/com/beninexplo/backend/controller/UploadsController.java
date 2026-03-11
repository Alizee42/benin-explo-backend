package com.beninexplo.backend.controller;

import com.beninexplo.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/uploads")
public class UploadsController {

    @Value("${image.storage.location:file:uploads/}")
    private String storageLocation;

    private Path getBase() {
        String location = storageLocation;
        if (location.startsWith("file:")) {
            location = location.substring(5);
        }
        return Paths.get(location).toAbsolutePath().normalize().resolve("documents").normalize();
    }

    @GetMapping(value = "/documents/{filename:.+}", produces = MediaType.ALL_VALUE)
    public ResponseEntity<Resource> getDocument(@PathVariable String filename) {
        Path base = getBase();
        Path file = base.resolve(filename).normalize();

        if (!file.startsWith(base) || !Files.exists(file)) {
            throw new ResourceNotFoundException("Document introuvable.");
        }

        try {
            UrlResource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Document introuvable.");
            }

            String mime = Files.probeContentType(file);
            if (mime == null) {
                mime = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mime))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + file.getFileName().toString().replace("\"", "") + "\"")
                    .cacheControl(CacheControl.noCache())
                    .body(resource);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire le document demande.", e);
        }
    }
}
