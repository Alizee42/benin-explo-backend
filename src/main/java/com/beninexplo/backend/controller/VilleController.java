package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.VilleDTO;
import com.beninexplo.backend.service.VilleService;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/villes")
@CrossOrigin(origins = "*")
public class VilleController {

    private final VilleService service;

    public VilleController(VilleService service) {
        this.service = service;
    }

    // ---------------------------------------
    // CRUD
    // ---------------------------------------

    @GetMapping
    public List<VilleDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VilleDTO> get(@PathVariable Long id) {
        VilleDTO dto = service.getById(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<VilleDTO> create(@RequestBody VilleDTO dto) {
        VilleDTO created = service.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VilleDTO> update(@PathVariable Long id, @RequestBody VilleDTO dto) {
        try {
            VilleDTO updated = service.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------------------------------
    // FILTRES
    // ---------------------------------------

    @GetMapping("/zone/{zoneId}")
    public List<VilleDTO> getByZone(@PathVariable Long zoneId) {
        return service.getByZone(zoneId);
    }
}