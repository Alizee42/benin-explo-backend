package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.DevisActiviteDTO;
import com.beninexplo.backend.service.DevisActiviteService;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devis-activites")
@CrossOrigin(origins = "*")
public class DevisActiviteController {

    private final DevisActiviteService service;

    public DevisActiviteController(DevisActiviteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DevisActiviteDTO> create(@RequestBody DevisActiviteDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @GetMapping("/devis/{devisId}")
    public List<DevisActiviteDTO> getByDevis(@PathVariable Long devisId) {
        return service.getByDevis(devisId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
