package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.HebergementDTO;
import com.beninexplo.backend.service.HebergementService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hebergements")
public class HebergementController {

    private final HebergementService service;

    public HebergementController(HebergementService service) {
        this.service = service;
    }

    @GetMapping
    public List<HebergementDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public HebergementDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public HebergementDTO create(@RequestBody HebergementDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public HebergementDTO update(@PathVariable Long id, @RequestBody HebergementDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
