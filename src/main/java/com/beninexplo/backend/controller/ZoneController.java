package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ZoneDTO;
import com.beninexplo.backend.service.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/zones")
@CrossOrigin("*")
public class ZoneController {

    @Autowired
    private ZoneService zoneService;

    @PostMapping
    public ZoneDTO create(@RequestBody ZoneDTO dto) {
        return zoneService.createZone(dto);
    }

    @GetMapping
    public List<ZoneDTO> getAll() {
        return zoneService.getAllZones();
    }

    @GetMapping("/{id}")
    public ZoneDTO getById(@PathVariable Long id) {
        return zoneService.getZoneById(id);
    }

    @PutMapping("/{id}")
    public ZoneDTO update(@PathVariable Long id, @RequestBody ZoneDTO dto) {
        return zoneService.updateZone(id, dto);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return "Zone supprimée avec succès.";
    }
}
