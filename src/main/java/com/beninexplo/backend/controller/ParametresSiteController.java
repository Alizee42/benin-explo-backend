package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ParametresSiteDTO;
import com.beninexplo.backend.service.ParametresSiteService;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parametres-site")
@CrossOrigin(origins = "*")
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
        ParametresSiteDTO dto = service.get(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ParametresSiteDTO> save(@RequestBody ParametresSiteDTO dto) {
        return new ResponseEntity<>(service.saveOrUpdate(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParametresSiteDTO> update(@PathVariable Long id,
                                                    @RequestBody ParametresSiteDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(service.saveOrUpdate(dto));
    }
}
