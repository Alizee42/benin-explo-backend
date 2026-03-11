package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.ReservationHebergementDTO;
import com.beninexplo.backend.service.ReservationHebergementService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations-hebergement")
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
        return ResponseEntity.ok(reservationService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ReservationHebergementDTO> create(@Valid @RequestBody ReservationHebergementDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationHebergementDTO> update(@PathVariable Long id,
                                                            @Valid @RequestBody ReservationHebergementDTO dto) {
        return ResponseEntity.ok(reservationService.update(id, dto));
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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateArrivee,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDepart) {
        return ResponseEntity.ok(reservationService.isAvailable(hebergementId, dateArrivee, dateDepart));
    }
}
