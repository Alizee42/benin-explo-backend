package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ParametresSiteDTO;
import com.beninexplo.backend.service.ParametresSiteService;
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

import java.util.List;

@RestController
@RequestMapping("/api/parametres-site")
public class ParametresSiteController {

    private final ParametresSiteService service;

    public ParametresSiteController(ParametresSiteService service) {
        this.service = service;
    }

    @GetMapping
    public List<ParametresSiteDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParametresSiteDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping
    public ResponseEntity<ParametresSiteDTO> save(@Valid @RequestBody ParametresSiteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveOrUpdate(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParametresSiteDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody ParametresSiteDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(service.saveOrUpdate(dto));
    }
}
