package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.CircuitDTO;
import com.beninexplo.backend.service.CircuitService;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/circuits")
@CrossOrigin(origins = "*")
public class CircuitController {

    private final CircuitService service;

    public CircuitController(CircuitService service) {
        this.service = service;
    }

    // ---------------------------------------
    // CRUD
    // ---------------------------------------

    @GetMapping
    public List<CircuitDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CircuitDTO> get(@PathVariable Long id) {
        CircuitDTO dto = service.getById(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<CircuitDTO> create(@RequestBody CircuitDTO dto) {
        CircuitDTO created = service.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CircuitDTO> update(@PathVariable Long id, @RequestBody CircuitDTO dto) {
        try {
            CircuitDTO updated = service.update(id, dto);
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
    // FILTRES & RECHERCHES
    // ---------------------------------------

    @GetMapping("/actifs")
    public List<CircuitDTO> getActifs() {
        return service.getActifs();
    }

    @GetMapping("/zone/{zoneId}")
    public List<CircuitDTO> getByZone(@PathVariable Long zoneId) {
        return service.getByZone(zoneId);
    }

    @GetMapping("/search")
    public List<CircuitDTO> search(@RequestParam String nom) {
        return service.searchByNom(nom);
    }
}
