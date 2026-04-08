package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.CircuitPersonnaliseDTO;
import com.beninexplo.backend.service.CircuitPersonnaliseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/circuits-personnalises")
public class CircuitPersonnaliseController {

    private final CircuitPersonnaliseService service;

    public CircuitPersonnaliseController(CircuitPersonnaliseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CircuitPersonnaliseDTO> create(@Valid @RequestBody CircuitPersonnaliseDTO dto) {
        CircuitPersonnaliseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<CircuitPersonnaliseDTO>> getAll(@RequestParam(required = false) String statut) {
        List<CircuitPersonnaliseDTO> circuits = (statut != null && !statut.isEmpty())
                ? service.getByStatut(statut)
                : service.getAll();
        return ResponseEntity.ok(circuits);
    }

    @GetMapping("/me")
    public ResponseEntity<List<CircuitPersonnaliseDTO>> getMine() {
        return ResponseEntity.ok(service.getMine());
    }

    @GetMapping("/me/{id}")
    public ResponseEntity<CircuitPersonnaliseDTO> getMineById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMineById(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CircuitPersonnaliseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<CircuitPersonnaliseDTO> updateStatut(@PathVariable Long id,
                                                               @RequestBody Map<String, Object> updates) {
        String nouveauStatut = (String) updates.get("statut");
        BigDecimal prixFinal = null;
        String commentaireAdmin = updates.get("commentaireAdmin") instanceof String value ? value : null;
        String motifRefus = updates.get("motifRefus") instanceof String value ? value : null;

        if (updates.containsKey("prixFinal")) {
            Object prixObj = updates.get("prixFinal");
            if (prixObj instanceof Number number) {
                prixFinal = BigDecimal.valueOf(number.doubleValue());
            } else if (prixObj instanceof String prixString && !prixString.isBlank()) {
                prixFinal = new BigDecimal(prixString);
            }
        }

        return ResponseEntity.ok(service.updateStatut(id, nouveauStatut, prixFinal, commentaireAdmin, motifRefus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
