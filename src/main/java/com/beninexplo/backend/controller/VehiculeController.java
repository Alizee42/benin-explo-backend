package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.VehiculeDTO;
import com.beninexplo.backend.service.VehiculeService;
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
@RequestMapping("/admin/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @PostMapping
    public ResponseEntity<VehiculeDTO> create(@Valid @RequestBody VehiculeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculeService.create(dto));
    }

    @GetMapping
    public List<VehiculeDTO> getAll() {
        return vehiculeService.getAll();
    }

    @GetMapping("/{id}")
    public VehiculeDTO getById(@PathVariable Long id) {
        return vehiculeService.getById(id);
    }

    @PutMapping("/{id}")
    public VehiculeDTO update(@PathVariable Long id, @Valid @RequestBody VehiculeDTO dto) {
        return vehiculeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehiculeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
