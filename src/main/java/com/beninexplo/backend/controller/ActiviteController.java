package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ActiviteDTO;
import com.beninexplo.backend.service.ActiviteService;
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
@RequestMapping("/api/activites")
public class ActiviteController {

    private final ActiviteService service;

    public ActiviteController(ActiviteService service) {
        this.service = service;
    }

    @GetMapping
    public List<ActiviteDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ActiviteDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<ActiviteDTO> create(@Valid @RequestBody ActiviteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ActiviteDTO update(@PathVariable Long id, @Valid @RequestBody ActiviteDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/zone/{zoneId}")
    public List<ActiviteDTO> getByZone(@PathVariable Long zoneId) {
        return service.getByZone(zoneId);
    }

    @GetMapping("/ville/{villeId}")
    public List<ActiviteDTO> getByVille(@PathVariable Long villeId) {
        return service.getByVille(villeId);
    }
}
