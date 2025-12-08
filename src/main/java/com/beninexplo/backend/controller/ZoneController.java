package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ZoneDTO;
import com.beninexplo.backend.service.ZoneService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@CrossOrigin(origins = "*")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @PostMapping
    public ResponseEntity<ZoneDTO> create(@RequestBody ZoneDTO dto) {
        ZoneDTO created = zoneService.createZone(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public List<ZoneDTO> getAll() {
        return zoneService.getAllZones();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneDTO> getById(@PathVariable Long id) {
        ZoneDTO dto = zoneService.getZoneById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneDTO> update(@PathVariable Long id, @RequestBody ZoneDTO dto) {
        try {
            ZoneDTO updated = zoneService.updateZone(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }
}
