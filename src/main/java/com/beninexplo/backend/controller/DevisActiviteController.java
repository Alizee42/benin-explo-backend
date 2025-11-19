package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.DevisActiviteDTO;
import com.beninexplo.backend.service.DevisActiviteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devis-activites")
public class DevisActiviteController {

    private final DevisActiviteService service;

    public DevisActiviteController(DevisActiviteService service) {
        this.service = service;
    }

    // ---------------------------
    // GET : toutes les activités
    // ---------------------------
    @GetMapping
    public ResponseEntity<List<DevisActiviteDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ---------------------------
    // GET : activités d’un devis
    // ---------------------------
    @GetMapping("/devis/{devisId}")
    public ResponseEntity<List<DevisActiviteDTO>> getByDevis(@PathVariable Long devisId) {
        return ResponseEntity.ok(service.getByDevis(devisId));
    }

    // ---------------------------
    // GET : 1 activité du devis
    // ---------------------------
    @GetMapping("/{id}")
    public ResponseEntity<DevisActiviteDTO> get(@PathVariable Long id) {

        DevisActiviteDTO dto = service.get(id);

        if (dto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dto);
    }

    // ---------------------------
    // POST : création
    // ---------------------------
    @PostMapping
    public ResponseEntity<DevisActiviteDTO> create(@RequestBody DevisActiviteDTO dto) {

        DevisActiviteDTO created = service.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ---------------------------
    // PUT : mise à jour
    // ---------------------------
    @PutMapping("/{id}")
    public ResponseEntity<DevisActiviteDTO> update(
            @PathVariable Long id,
            @RequestBody DevisActiviteDTO dto
    ) {
        try {
            return ResponseEntity.ok(service.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ---------------------------
    // DELETE
    // ---------------------------
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
