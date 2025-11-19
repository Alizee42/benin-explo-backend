package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.CircuitDTO;
import com.beninexplo.backend.service.CircuitService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/circuits")
public class CircuitController {

    private final CircuitService service;

    public CircuitController(CircuitService service) {
        this.service = service;
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<CircuitDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<CircuitDTO> get(@PathVariable Long id) {
        CircuitDTO dto = service.get(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    // CREATE
    @PostMapping
    public ResponseEntity<CircuitDTO> create(@RequestBody CircuitDTO dto) {
        CircuitDTO created = service.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<CircuitDTO> update(@PathVariable Long id, @RequestBody CircuitDTO dto) {
        try {
            CircuitDTO updated = service.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
