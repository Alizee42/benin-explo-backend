package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.CircuitPersonnaliseDTO;
import com.beninexplo.backend.service.CircuitPersonnaliseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/circuits-personnalises")
@CrossOrigin(origins = "*")
public class CircuitPersonnaliseController {

    private final CircuitPersonnaliseService service;

    public CircuitPersonnaliseController(CircuitPersonnaliseService service) {
        this.service = service;
    }

    // ----------------------------------------------------
    // POST - Créer une nouvelle demande
    // ----------------------------------------------------
    @PostMapping
    public ResponseEntity<CircuitPersonnaliseDTO> create(@RequestBody CircuitPersonnaliseDTO dto) {
        try {
            CircuitPersonnaliseDTO created = service.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ----------------------------------------------------
    // GET - Liste toutes les demandes
    // ----------------------------------------------------
    @GetMapping
    public ResponseEntity<List<CircuitPersonnaliseDTO>> getAll(
            @RequestParam(required = false) String statut) {
        try {
            List<CircuitPersonnaliseDTO> circuits;
            if (statut != null && !statut.isEmpty()) {
                circuits = service.getByStatut(statut);
            } else {
                circuits = service.getAll();
            }
            return ResponseEntity.ok(circuits);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ----------------------------------------------------
    // GET - Récupérer une demande par ID
    // ----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<CircuitPersonnaliseDTO> getById(@PathVariable Long id) {
        try {
            CircuitPersonnaliseDTO circuit = service.getById(id);
            return ResponseEntity.ok(circuit);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ----------------------------------------------------
    // PATCH - Mettre à jour le statut
    // ----------------------------------------------------
    @PatchMapping("/{id}/statut")
    public ResponseEntity<CircuitPersonnaliseDTO> updateStatut(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        try {
            String nouveauStatut = (String) updates.get("statut");
            BigDecimal prixFinal = null;
            
            if (updates.containsKey("prixFinal")) {
                Object prixObj = updates.get("prixFinal");
                if (prixObj instanceof Number) {
                    prixFinal = BigDecimal.valueOf(((Number) prixObj).doubleValue());
                } else if (prixObj instanceof String) {
                    prixFinal = new BigDecimal((String) prixObj);
                }
            }
            
            CircuitPersonnaliseDTO updated = service.updateStatut(id, nouveauStatut, prixFinal);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ----------------------------------------------------
    // DELETE - Supprimer une demande
    // ----------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Circuit personnalisé supprimé avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
