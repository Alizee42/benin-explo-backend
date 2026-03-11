package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.CircuitDTO;
import com.beninexplo.backend.service.CircuitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/circuits")
public class CircuitController {

    private final CircuitService service;

    public CircuitController(CircuitService service) {
        this.service = service;
    }

    @GetMapping
    public List<CircuitDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CircuitDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<CircuitDTO> create(@Valid @RequestBody CircuitDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CircuitDTO> update(@PathVariable Long id, @Valid @RequestBody CircuitDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

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
