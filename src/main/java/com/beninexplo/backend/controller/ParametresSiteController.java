package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ParametresSiteDTO;
import com.beninexplo.backend.service.ParametresSiteService;

import org.springframework.web.bind.annotation.*;

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
    public ParametresSiteDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ParametresSiteDTO create(@RequestBody ParametresSiteDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ParametresSiteDTO update(@PathVariable Long id, @RequestBody ParametresSiteDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
