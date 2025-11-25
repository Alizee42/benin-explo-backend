package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ActiviteDTO;
import com.beninexplo.backend.service.ActiviteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activites")
@CrossOrigin(origins = "*") // à adapter selon ton front
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
    public ActiviteDTO create(@RequestBody ActiviteDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ActiviteDTO update(@PathVariable Long id, @RequestBody ActiviteDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
