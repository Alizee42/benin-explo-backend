package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.TarifsCircuitPersonnaliseDTO;
import com.beninexplo.backend.service.TarifsCircuitPersonnaliseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tarifs-circuit-personnalise")
public class TarifsCircuitPersonnaliseController {

    private final TarifsCircuitPersonnaliseService service;

    public TarifsCircuitPersonnaliseController(TarifsCircuitPersonnaliseService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<TarifsCircuitPersonnaliseDTO> getCurrent() {
        return ResponseEntity.ok(service.getCurrent());
    }

    @PostMapping
    public ResponseEntity<TarifsCircuitPersonnaliseDTO> create(@Valid @RequestBody TarifsCircuitPersonnaliseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveOrUpdate(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarifsCircuitPersonnaliseDTO> update(@PathVariable Long id,
                                                               @Valid @RequestBody TarifsCircuitPersonnaliseDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(service.saveOrUpdate(dto));
    }
}
