package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.MediaDTO;
import com.beninexplo.backend.service.MediaService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/media")

public class MediaController {

    private final MediaService service;

    public MediaController(MediaService service) {
        this.service = service;
    }

    @GetMapping
    public List<MediaDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MediaDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public MediaDTO create(@RequestBody MediaDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public MediaDTO update(@PathVariable Long id, @RequestBody MediaDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
