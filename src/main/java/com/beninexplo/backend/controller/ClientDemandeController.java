package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ClientDemandeDTO;
import com.beninexplo.backend.service.ClientDemandeService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/client-demandes")

public class ClientDemandeController {

    private final ClientDemandeService service;

    public ClientDemandeController(ClientDemandeService service) {
        this.service = service;
    }

    @GetMapping
    public List<ClientDemandeDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ClientDemandeDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ClientDemandeDTO create(@RequestBody ClientDemandeDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ClientDemandeDTO update(@PathVariable Long id, @RequestBody ClientDemandeDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
