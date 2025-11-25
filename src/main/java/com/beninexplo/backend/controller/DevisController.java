package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.DevisRequestDTO;
import com.beninexplo.backend.dto.DevisResponseDTO;
import com.beninexplo.backend.service.DevisService;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devis")
@CrossOrigin(origins = "*")
public class DevisController {

    private final DevisService service;

    public DevisController(DevisService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DevisResponseDTO> create(@RequestBody DevisRequestDTO dto) {
        DevisResponseDTO created = service.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public List<DevisResponseDTO> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
