package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ZoneDTO;
import com.beninexplo.backend.service.ZoneService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/zones")

public class ZoneController {

    private final ZoneService service;

    public ZoneController(ZoneService service) {
        this.service = service;
    }

    @GetMapping
    public List<ZoneDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ZoneDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ZoneDTO create(@RequestBody ZoneDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ZoneDTO update(@PathVariable Long id, @RequestBody ZoneDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
