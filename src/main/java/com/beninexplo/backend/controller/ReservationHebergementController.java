package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ReservationHebergementDTO;
import com.beninexplo.backend.service.ReservationHebergementService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations-hebergement")
@CrossOrigin(origins = "*")
public class ReservationHebergementController {

    private final ReservationHebergementService reservationService;

    public ReservationHebergementController(ReservationHebergementService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationHebergementDTO>> getAll() {
        return ResponseEntity.ok(reservationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationHebergementDTO> getById(@PathVariable Long id) {
        ReservationHebergementDTO reservation = reservationService.getById(id);
        if (reservation != null) {
            return ResponseEntity.ok(reservation);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ReservationHebergementDTO dto) {
        try {
            ReservationHebergementDTO created = reservationService.create(dto);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ReservationHebergementDTO dto) {
        try {
            ReservationHebergementDTO updated = reservationService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hebergement/{hebergementId}")
    public ResponseEntity<List<ReservationHebergementDTO>> getByHebergement(@PathVariable Long hebergementId) {
        return ResponseEntity.ok(reservationService.getByHebergement(hebergementId));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<ReservationHebergementDTO>> getByStatut(@PathVariable String statut) {
        return ResponseEntity.ok(reservationService.getByStatut(statut));
    }

    @GetMapping("/disponibilite")
    public ResponseEntity<Boolean> checkDisponibilite(
            @RequestParam Long hebergementId,
            @RequestParam String dateArrivee,
            @RequestParam String dateDepart) {
        try {
            boolean disponible = reservationService.isAvailable(
                    hebergementId,
                    java.time.LocalDate.parse(dateArrivee),
                    java.time.LocalDate.parse(dateDepart));
            return ResponseEntity.ok(disponible);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}