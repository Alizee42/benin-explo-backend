package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.UtilisateurCreateDTO;
import com.beninexplo.backend.dto.UtilisateurDTO;
import com.beninexplo.backend.service.UtilisateurService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService service;

    public UtilisateurController(UtilisateurService service) {
        this.service = service;
    }

    @GetMapping
    public List<UtilisateurDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UtilisateurDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurDTO create(@RequestBody UtilisateurCreateDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public UtilisateurDTO update(@PathVariable Long id, @RequestBody UtilisateurDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

