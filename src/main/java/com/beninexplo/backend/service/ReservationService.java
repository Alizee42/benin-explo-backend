package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ReservationRequestDTO;
import com.beninexplo.backend.dto.ReservationResponseDTO;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.Reservation;
import com.beninexplo.backend.entity.Utilisateur;
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
    private final AuthenticatedUserService authenticatedUserService;

    public ReservationService(ReservationRepository repo,
                              CircuitRepository circuitRepo,
                              AuthenticatedUserService authenticatedUserService) {
        this.repo = repo;
        this.circuitRepo = circuitRepo;
        this.authenticatedUserService = authenticatedUserService;
    }

    private ReservationResponseDTO toDTO(Reservation reservation) {
        ReservationResponseDTO dto = new ReservationResponseDTO(
                reservation.getIdReservation(),
                reservation.getNom(),
                reservation.getPrenom(),
                reservation.getEmail(),
                reservation.getTelephone(),
                reservation.getDateReservation(),
                reservation.getCircuit().getIdCircuit()
        );
        dto.setCircuitNom(reservation.getCircuit().getNom());
        dto.setStatut(reservation.getStatut());
        dto.setNombrePersonnes(reservation.getNombrePersonnes());
        dto.setCommentaires(reservation.getCommentaires());
        if (reservation.getUtilisateur() != null) {
            dto.setUtilisateurId(reservation.getUtilisateur().getId());
        }
        return dto;
    }

    private Reservation fromDTO(ReservationRequestDTO dto, Utilisateur utilisateur) {
        if (dto.getCircuitId() == null) {
            throw new BadRequestException("Le circuit est obligatoire.");
        }

        Circuit circuit = circuitRepo.findById(dto.getCircuitId())
                .orElseThrow(() -> new ResourceNotFoundException("Circuit non trouve."));

        Reservation reservation = new Reservation();
        reservation.setNom(resolveRequiredText(dto.getNom(), utilisateur != null ? utilisateur.getNom() : null,
                "Le nom est obligatoire."));
        reservation.setPrenom(resolveRequiredText(dto.getPrenom(), utilisateur != null ? utilisateur.getPrenom() : null,
                "Le prenom est obligatoire."));
        reservation.setEmail(utilisateur == null
                ? resolveRequiredText(dto.getEmail(), null, "L'email est obligatoire.")
                : resolveRequiredText(utilisateur.getEmail(), dto.getEmail(), "L'email du compte est obligatoire."));
        reservation.setTelephone(resolveRequiredText(dto.getTelephone(), utilisateur != null ? utilisateur.getTelephone() : null,
                "Le telephone est obligatoire."));
        reservation.setDateReservation(dto.getDateReservation());
        reservation.setCircuit(circuit);
        reservation.setUtilisateur(utilisateur);
        if (dto.getNombrePersonnes() != null) reservation.setNombrePersonnes(dto.getNombrePersonnes());
        if (dto.getCommentaires() != null) reservation.setCommentaires(dto.getCommentaires());
        return reservation;
    }

    public ReservationResponseDTO create(ReservationRequestDTO dto) {
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        return toDTO(repo.save(fromDTO(dto, currentUser)));
    }

    public ReservationResponseDTO update(Long id, ReservationRequestDTO dto) {
        Reservation existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation non trouvee."));
        existing.setNom(dto.getNom());
        existing.setPrenom(dto.getPrenom());
        existing.setEmail(existing.getUtilisateur() != null
                ? existing.getUtilisateur().getEmail()
                : dto.getEmail());
        existing.setTelephone(dto.getTelephone());
        existing.setDateReservation(dto.getDateReservation());
        if (dto.getStatut() != null) existing.setStatut(dto.getStatut());
        if (dto.getNombrePersonnes() != null) existing.setNombrePersonnes(dto.getNombrePersonnes());
        existing.setCommentaires(dto.getCommentaires());
        return toDTO(repo.save(existing));
    }

    public List<ReservationResponseDTO> getAll() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ReservationResponseDTO> getMine() {
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        return repo.findByUtilisateurIdOrderByDateReservationDesc(currentUser.getId()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    private String resolveRequiredText(String preferredValue, String fallbackValue, String errorMessage) {
        if (preferredValue != null && !preferredValue.trim().isEmpty()) {
            return preferredValue.trim();
        }
        if (fallbackValue != null && !fallbackValue.trim().isEmpty()) {
            return fallbackValue.trim();
        }
        throw new BadRequestException(errorMessage);
    }
}
