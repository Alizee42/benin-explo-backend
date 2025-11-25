package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ReservationRequestDTO;
import com.beninexplo.backend.dto.ReservationResponseDTO;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.Reservation;
import com.beninexplo.backend.repository.CircuitRepository;
import com.beninexplo.backend.repository.ReservationRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository repo;
    private final CircuitRepository circuitRepo;

    public ReservationService(ReservationRepository repo, CircuitRepository circuitRepo) {
        this.repo = repo;
        this.circuitRepo = circuitRepo;
    }

    private ReservationResponseDTO toDTO(Reservation r) {
        return new ReservationResponseDTO(
                r.getIdReservation(),
                r.getNom(),
                r.getPrenom(),
                r.getEmail(),
                r.getTelephone(),
                r.getDateReservation(),
                r.getCircuit().getIdCircuit()
        );
    }

    private Reservation fromDTO(ReservationRequestDTO dto) {

        Reservation r = new Reservation();

        r.setNom(dto.getNom());
        r.setPrenom(dto.getPrenom());
        r.setEmail(dto.getEmail());
        r.setTelephone(dto.getTelephone());
        r.setDateReservation(dto.getDateReservation());

        Circuit c = circuitRepo.findById(dto.getCircuitId())
                .orElseThrow(() -> new RuntimeException("Circuit non trouvé"));
        r.setCircuit(c);

        return r;
    }

    public ReservationResponseDTO create(ReservationRequestDTO dto) {
        Reservation saved = repo.save(fromDTO(dto));
        return toDTO(saved);
    }

    public List<ReservationResponseDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
