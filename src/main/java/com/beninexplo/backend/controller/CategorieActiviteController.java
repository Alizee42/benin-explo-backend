package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.CategorieActiviteDTO;
import com.beninexplo.backend.service.CategorieActiviteService;
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
    public ResponseEntity<CategorieActiviteDTO> create(@Valid @RequestBody CategorieActiviteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public CategorieActiviteDTO update(@PathVariable Long id, @Valid @RequestBody CategorieActiviteDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
