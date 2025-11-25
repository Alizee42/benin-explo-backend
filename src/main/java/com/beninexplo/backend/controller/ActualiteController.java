package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ActualiteDTO;
import com.beninexplo.backend.service.ActualiteService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/actualites")
@CrossOrigin("*")
public class ActualiteController {

    private final ActualiteService service;

    public ActualiteController(ActualiteService service) {
        this.service = service;
    }

    @GetMapping
    public List<ActualiteDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ActualiteDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ActualiteDTO create(@RequestBody ActualiteDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ActualiteDTO update(@PathVariable Long id, @RequestBody ActualiteDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
