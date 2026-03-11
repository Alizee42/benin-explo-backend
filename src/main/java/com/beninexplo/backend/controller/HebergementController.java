package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.HebergementDTO;
import com.beninexplo.backend.service.HebergementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/api/hebergements")
public class HebergementController {

    private static final Logger log = LoggerFactory.getLogger(HebergementController.class);

    private final HebergementService service;

    public HebergementController(HebergementService service) {
        this.service = service;
    }

    @GetMapping
    public List<HebergementDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HebergementDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping
    public ResponseEntity<HebergementDTO> create(@Valid @RequestBody HebergementDTO dto) {
        log.debug("Creation hebergement: nom={}, type={}, localisation={}",
                dto.getNom(), dto.getType(), dto.getLocalisation());

        dto.setId(null);
        HebergementDTO created = service.create(dto);
        log.info("Hebergement cree, ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HebergementDTO> update(@PathVariable Long id, @Valid @RequestBody HebergementDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
