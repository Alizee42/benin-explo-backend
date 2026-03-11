package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ZoneDTO;
import com.beninexplo.backend.service.ZoneService;
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
@RequestMapping("/api/zones")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @PostMapping
    public ResponseEntity<ZoneDTO> create(@Valid @RequestBody ZoneDTO dto) {
        ZoneDTO created = zoneService.createZone(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public List<ZoneDTO> getAll() {
        return zoneService.getAllZones();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(zoneService.getZoneById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneDTO> update(@PathVariable Long id, @Valid @RequestBody ZoneDTO dto) {
        ZoneDTO updated = zoneService.updateZone(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }
}
