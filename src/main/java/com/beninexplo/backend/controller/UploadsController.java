package com.beninexplo.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/uploads")
public class UploadsController {

    @Value("${image.storage.location:file:uploads/}")
    private String storageLocation;

    private Path getBase() {
        String loc = storageLocation;
        if (loc.startsWith("file:")) loc = loc.substring(5);
        return Paths.get(loc).toAbsolutePath().normalize().resolve("documents").normalize();
    }

    @GetMapping(value = "/documents/{filename:.+}", produces = MediaType.ALL_VALUE)
    public ResponseEntity<Resource> getDocument(@PathVariable String filename) {
        try {
            Path base = getBase();
            Path file = base.resolve(filename).normalize();

            if (!file.startsWith(base) || !Files.exists(file)) {
                return ResponseEntity.notFound().build();
            }

            String mime = Files.probeContentType(file);
            if (mime == null) mime = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            UrlResource resource = new UrlResource(file.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mime))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + file.getFileName().toString().replace("\"", "") + "\"")
                    .cacheControl(CacheControl.noCache())
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
