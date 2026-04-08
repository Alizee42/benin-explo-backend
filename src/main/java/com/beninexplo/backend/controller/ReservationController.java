package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ReservationRequestDTO;
import com.beninexplo.backend.dto.ReservationResponseDTO;
import com.beninexplo.backend.service.ReservationService;
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
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> create(@Valid @RequestBody ReservationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping("/me")
    public List<ReservationResponseDTO> getMine() {
        return service.getMine();
    }

    @GetMapping("/me/{id}")
    public ReservationResponseDTO getMineById(@PathVariable Long id) {
        return service.getMineById(id);
    }

    @GetMapping
    public List<ReservationResponseDTO> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public ReservationResponseDTO update(@PathVariable Long id, @Valid @RequestBody ReservationRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
