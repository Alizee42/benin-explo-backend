package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ClientDemandeDTO;
import com.beninexplo.backend.service.ClientDemandeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "*")
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
    public ResponseEntity<ClientDemandeDTO> get(@PathVariable Long id) {
        ClientDemandeDTO dto = service.get(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ClientDemandeDTO> create(@RequestBody ClientDemandeDTO dto) {
        ClientDemandeDTO created = service.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
