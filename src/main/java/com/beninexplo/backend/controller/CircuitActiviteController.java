package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.CircuitActiviteDTO;
import com.beninexplo.backend.service.CircuitActiviteService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/circuit-activites")
@CrossOrigin(origins = "*")
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
    public CircuitActiviteDTO create(@RequestBody CircuitActiviteDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CircuitActiviteDTO update(@PathVariable Long id, @RequestBody CircuitActiviteDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    /* ---- Récupérer toutes les activités d’un circuit ---- */
    @GetMapping("/circuit/{circuitId}")
    public List<CircuitActiviteDTO> getByCircuit(@PathVariable Long circuitId) {
        return service.getByCircuit(circuitId);
    }
}
