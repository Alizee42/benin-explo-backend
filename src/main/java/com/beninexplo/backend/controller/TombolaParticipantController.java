package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.TombolaParticipantDTO;
import com.beninexplo.backend.service.TombolaParticipantService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tombola")

public class TombolaParticipantController {

    private final TombolaParticipantService service;

    public TombolaParticipantController(TombolaParticipantService service) {
        this.service = service;
    }

    @GetMapping
    public List<TombolaParticipantDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TombolaParticipantDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public TombolaParticipantDTO create(@RequestBody TombolaParticipantDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public TombolaParticipantDTO update(@PathVariable Long id, @RequestBody TombolaParticipantDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
