package com.beninexplo.backend.controller;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.beninexplo.backend.dto.HebergementDTO;
import com.beninexplo.backend.service.HebergementService;

import java.util.List;

@RestController
@RequestMapping("/api/hebergements")
@CrossOrigin(origins = "*")
public class HebergementController {

    private static final Logger log = LoggerFactory.getLogger(HebergementController.class);

    private final HebergementService service;

    public HebergementController(HebergementService service) {
        this.service = service;
    }

    @GetMapping
    public List<HebergementDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HebergementDTO> get(@PathVariable Long id) {
        HebergementDTO dto = service.get(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<HebergementDTO> create(@RequestBody HebergementDTO dto) {
        try {
            log.debug("Création hébergement: nom={}, type={}, localisation={}", 
                dto.getNom(), dto.getType(), dto.getLocalisation());

            // Ignorer tout ID éventuellement envoyé par le client pour une création
            dto.setId(null);

            HebergementDTO created = service.create(dto);
            log.info("✅ Hébergement créé, ID: {}", created.getId());
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la création de l'hébergement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<HebergementDTO> update(@PathVariable Long id, @RequestBody HebergementDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
