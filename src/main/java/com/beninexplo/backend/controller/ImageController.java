package com.beninexplo.backend.controller;

import com.beninexplo.backend.service.ImageStorageService;
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

    private final ImageStorageService storageService;

    public ImageController(ImageStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "folder", required = false) String folder) {
        try {
            System.out.println("[ImageController] Reçu une requête d'upload d'image");
            System.out.println("[ImageController] Nom original du fichier : " + file.getOriginalFilename());
            System.out.println("[ImageController] Taille du fichier : " + file.getSize() + " octets");
            System.out.println("[ImageController] Paramètre folder : " + folder);

            String filename = storageService.store(file, folder == null ? "" : folder);
            String urlFolder = (folder == null || folder.isBlank()) ? "" : folder + "/";
            String url = "/images/" + urlFolder + filename;
            Map<String, String> body = new HashMap<>();
            body.put("filename", filename);
            body.put("url", url);
            System.out.println("[ImageController] Fichier stocké avec succès sous le nom : " + filename);
            System.out.println("[ImageController] URL retournée : " + url);
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } catch (Exception e) {
            System.out.println("[ImageController] Erreur lors de l'upload : " + e.getMessage());
            e.printStackTrace();
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }
}
