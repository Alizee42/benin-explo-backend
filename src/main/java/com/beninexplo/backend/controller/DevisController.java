package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.DevisRequestDTO;
import com.beninexplo.backend.dto.DevisResponseDTO;
import com.beninexplo.backend.service.DevisService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devis")
@CrossOrigin // garde-le si tu veux autoriser Next.js
public class DevisController {

    private final DevisService service;

    public DevisController(DevisService service) {
        this.service = service;
    }

    // ---------------------------------------
    // GET : tous les devis
    // ---------------------------------------
    @GetMapping
    public ResponseEntity<List<DevisResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ---------------------------------------
    // GET : un devis par son ID
    // ---------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<DevisResponseDTO> get(@PathVariable Long id) {
        DevisResponseDTO devis = service.get(id);

        if (devis == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(devis);
    }

    // ---------------------------------------
    // POST : création d’un devis
    // ---------------------------------------
    @PostMapping
    public ResponseEntity<DevisResponseDTO> create(@RequestBody DevisRequestDTO dto) {
        DevisResponseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ---------------------------------------
    // DELETE : suppression d’un devis
    // ---------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
