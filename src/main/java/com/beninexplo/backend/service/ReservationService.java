package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ReservationRequestDTO;
import com.beninexplo.backend.dto.ReservationResponseDTO;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.Reservation;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.CircuitRepository;
import com.beninexplo.backend.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class ReservationService {

    private final ReservationRepository repo;
    private final CircuitRepository circuitRepo;

    public ReservationService(ReservationRepository repo, CircuitRepository circuitRepo) {
        this.repo = repo;
        this.circuitRepo = circuitRepo;
    }

    private ReservationResponseDTO toDTO(Reservation reservation) {
        return new ReservationResponseDTO(
                reservation.getIdReservation(),
                reservation.getNom(),
                reservation.getPrenom(),
                reservation.getEmail(),
                reservation.getTelephone(),
                reservation.getDateReservation(),
                reservation.getCircuit().getIdCircuit()
        );
    }

    private Reservation fromDTO(ReservationRequestDTO dto) {
        if (dto.getCircuitId() == null) {
            throw new BadRequestException("Le circuit est obligatoire.");
        }

        Circuit circuit = circuitRepo.findById(dto.getCircuitId())
                .orElseThrow(() -> new ResourceNotFoundException("Circuit non trouve."));

        Reservation reservation = new Reservation();
        reservation.setNom(dto.getNom());
        reservation.setPrenom(dto.getPrenom());
        reservation.setEmail(dto.getEmail());
        reservation.setTelephone(dto.getTelephone());
        reservation.setDateReservation(dto.getDateReservation());
        reservation.setCircuit(circuit);
        return reservation;
    }

    public ReservationResponseDTO create(ReservationRequestDTO dto) {
        return toDTO(repo.save(fromDTO(dto)));
    }

    public List<ReservationResponseDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
