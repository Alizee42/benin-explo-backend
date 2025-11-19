package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.CategorieActiviteDTO;
import com.beninexplo.backend.service.CategorieActiviteService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories-activites")

public class CategorieActiviteController {

    private final CategorieActiviteService service;

    public CategorieActiviteController(CategorieActiviteService service) {
        this.service = service;
    }

    @GetMapping
    public List<CategorieActiviteDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CategorieActiviteDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public CategorieActiviteDTO create(@RequestBody CategorieActiviteDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CategorieActiviteDTO update(@PathVariable Long id, @RequestBody CategorieActiviteDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
