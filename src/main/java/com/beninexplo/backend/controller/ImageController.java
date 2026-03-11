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
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file,
                                                      @RequestParam(value = "folder", required = false) String folder) {
        log.debug("Upload image: nom={}, taille={} octets, folder={}",
                file.getOriginalFilename(), file.getSize(), folder);

        String normalizedFolder = folder == null ? "" : folder.trim();
        String filename = storageService.store(file, normalizedFolder);
        String urlFolder = normalizedFolder.isBlank() ? "" : normalizedFolder + "/";
        String url = "/images/" + urlFolder + filename;

        log.info("Image uploadee: {}", url);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("filename", filename, "url", url));
    }
}
