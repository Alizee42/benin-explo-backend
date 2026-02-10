package com.beninexplo.backend.controller;

import com.beninexplo.backend.service.ImageStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    private final ImageStorageService storageService;

    public ImageController(ImageStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "folder", required = false) String folder) {
        try {
            log.debug("Upload image: nom={}, taille={} octets, folder={}", 
                file.getOriginalFilename(), file.getSize(), folder);

            String filename = storageService.store(file, folder == null ? "" : folder);
            String urlFolder = (folder == null || folder.isBlank()) ? "" : folder + "/";
            String url = "/images/" + urlFolder + filename;
            Map<String, String> body = new HashMap<>();
            body.put("filename", filename);
            body.put("url", url);
            
            log.info("✅ Image uploadée: {}", url);
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'upload d'image", e);
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }
}
