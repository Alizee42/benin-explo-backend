package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.CircuitActiviteDTO;
import com.beninexplo.backend.service.CircuitActiviteService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/circuit-activites")
public class CircuitActiviteController {

    private final CircuitActiviteService service;

    public CircuitActiviteController(CircuitActiviteService service) {
        this.service = service;
    }

    @GetMapping
    public List<CircuitActiviteDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CircuitActiviteDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<CircuitActiviteDTO> create(@Valid @RequestBody CircuitActiviteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public CircuitActiviteDTO update(@PathVariable Long id, @Valid @RequestBody CircuitActiviteDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/circuit/{circuitId}")
    public List<CircuitActiviteDTO> getByCircuit(@PathVariable Long circuitId) {
        return service.getByCircuit(circuitId);
    }
}
