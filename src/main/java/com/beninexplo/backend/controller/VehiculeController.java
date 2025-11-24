package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.VehiculeDTO;
import com.beninexplo.backend.service.VehiculeService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final VehiculeService service;

    public VehiculeController(VehiculeService service) {
        this.service = service;
    }

    @GetMapping
    public List<VehiculeDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public VehiculeDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public VehiculeDTO create(@RequestBody VehiculeDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public VehiculeDTO update(@PathVariable Long id, @RequestBody VehiculeDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
