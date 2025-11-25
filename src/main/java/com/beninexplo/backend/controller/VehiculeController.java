package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.VehiculeDTO;
import com.beninexplo.backend.service.VehiculeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/vehicules")
@CrossOrigin("*")
public class VehiculeController {

    @Autowired
    private VehiculeService vehiculeService;

    @PostMapping
    public VehiculeDTO create(@RequestBody VehiculeDTO dto) {
        return vehiculeService.create(dto);
    }

    @GetMapping
    public List<VehiculeDTO> getAll() {
        return vehiculeService.getAll();
    }

    @GetMapping("/{id}")
    public VehiculeDTO getById(@PathVariable Long id) {
        return vehiculeService.getById(id);
    }

    @PutMapping("/{id}")
    public VehiculeDTO update(@PathVariable Long id, @RequestBody VehiculeDTO dto) {
        return vehiculeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        vehiculeService.delete(id);
        return "Véhicule supprimé avec succès.";
    }
}
