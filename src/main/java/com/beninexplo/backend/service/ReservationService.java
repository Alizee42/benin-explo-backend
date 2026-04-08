package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ReservationRequestDTO;
import com.beninexplo.backend.dto.ReservationResponseDTO;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.PaiementReservationCircuit;
import com.beninexplo.backend.entity.Reservation;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.CircuitRepository;
import com.beninexplo.backend.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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
        dto.setReferenceReservation(resolveReference(reservation));
        dto.setPrixTotal(reservation.getCircuit().getPrixIndicatif());
        if (reservation.getUtilisateur() != null) {
            dto.setUtilisateurId(reservation.getUtilisateur().getId());
        }

        PaiementReservationCircuit paiement = reservation.getPaiement();
        if (paiement != null) {
            dto.setStatutPaiement(normalizePaymentStatus(paiement.getStatut()));
            dto.setMontantPaye(paiement.getMontant());
            dto.setDevisePaiement(paiement.getDevise());
            dto.setPaypalOrderId(paiement.getPaypalOrderId());
            dto.setPaypalCaptureId(paiement.getPaypalCaptureId());
            dto.setDatePaiement(paiement.getDatePaiement());
        } else {
            dto.setStatutPaiement("A_PAYER");
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
        if (dto.getNombrePersonnes() != null) {
            reservation.setNombrePersonnes(dto.getNombrePersonnes());
        }
        if (dto.getCommentaires() != null) {
            reservation.setCommentaires(dto.getCommentaires());
        }
        return reservation;
    }

    public ReservationResponseDTO create(ReservationRequestDTO dto) {
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        Reservation reservation = fromDTO(dto, currentUser);
        // Temporary unique reference to satisfy NOT NULL constraint before ID is known
        reservation.setReferenceReservation(java.util.UUID.randomUUID().toString().substring(0, 20));
        Reservation saved = repo.save(reservation);
        // Replace with the proper ID-based reference
        saved.setReferenceReservation(buildReference(saved.getIdReservation()));
        return toDTO(repo.save(saved));
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
        if (dto.getStatut() != null) {
            existing.setStatut(dto.getStatut());
        }
        if (dto.getNombrePersonnes() != null) {
            existing.setNombrePersonnes(dto.getNombrePersonnes());
        }
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

    public ReservationResponseDTO getMineById(Long id) {
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        Reservation reservation = repo.findByIdReservationAndUtilisateurId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation circuit introuvable pour ce compte."));
        return toDTO(reservation);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    private Reservation ensureReference(Reservation reservation) {
        if (reservation.getIdReservation() == null) {
            return reservation;
        }
        if (StringUtils.hasText(reservation.getReferenceReservation())) {
            return reservation;
        }
        reservation.setReferenceReservation(buildReference(reservation.getIdReservation()));
        return repo.save(reservation);
    }

    private String resolveReference(Reservation reservation) {
        if (StringUtils.hasText(reservation.getReferenceReservation())) {
            return reservation.getReferenceReservation().trim();
        }
        if (reservation.getIdReservation() == null) {
            return null;
        }
        return buildReference(reservation.getIdReservation());
    }

    private String buildReference(Long reservationId) {
        return "CIR-" + String.format("%06d", reservationId);
    }

    private String normalizePaymentStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return "A_PAYER";
        }
        return status.trim().toUpperCase();
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
