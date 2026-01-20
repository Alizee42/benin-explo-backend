package com.beninexplo.backend.controller;



import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.beninexplo.backend.dto.HebergementDTO;
import com.beninexplo.backend.service.HebergementService;

import java.util.List;

@RestController
@RequestMapping("/api/hebergements")
@CrossOrigin(origins = "*")
public class HebergementController {

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
            System.out.println("[HebergementController#create] DTO reçu pour création : id=" + dto.getId()
                    + ", nom=" + dto.getNom()
                    + ", type=" + dto.getType()
                    + ", localisation=" + dto.getLocalisation()
                    + ", quartier=" + dto.getQuartier()
                    + ", prixParNuit=" + dto.getPrixParNuit());

            // Ignorer tout ID éventuellement envoyé par le client pour une création
            dto.setId(null);
            System.out.println("[HebergementController#create] ID du DTO forcé à null avant service.create");

            HebergementDTO created = service.create(dto);
            System.out.println("[HebergementController#create] Création réussie, id généré=" + created.getId());
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
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
