package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ReservationHebergementDTO;
import com.beninexplo.backend.entity.Hebergement;
import com.beninexplo.backend.entity.ReservationHebergement;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.HebergementRepository;
import com.beninexplo.backend.repository.ReservationHebergementRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class ReservationHebergementService {

    private final ReservationHebergementRepository reservationRepo;
    private final HebergementRepository hebergementRepo;
    private final ReservationHebergementNotificationService notificationService;

    public ReservationHebergementService(ReservationHebergementRepository reservationRepo,
                                         HebergementRepository hebergementRepo,
                                         ReservationHebergementNotificationService notificationService) {
        this.reservationRepo = reservationRepo;
        this.hebergementRepo = hebergementRepo;
        this.notificationService = notificationService;
    }

    private ReservationHebergementDTO toDTO(ReservationHebergement reservation) {
        return new ReservationHebergementDTO(
                reservation.getIdReservation(),
                reservation.getHebergement().getIdHebergement(),
                reservation.getHebergement().getNom(),
                reservation.getNomClient(),
                reservation.getPrenomClient(),
                reservation.getEmailClient(),
                reservation.getTelephoneClient(),
                reservation.getDateArrivee(),
                reservation.getDateDepart(),
                reservation.getNombreNuits(),
                reservation.getNombrePersonnes(),
                reservation.getPrixTotal(),
                reservation.getStatut(),
                reservation.getCommentaires(),
                reservation.getDateCreation()
        );
    }

    private ReservationHebergement fromDTO(ReservationHebergementDTO dto) {
        Hebergement hebergement = hebergementRepo.findById(dto.getHebergementId())
                .orElseThrow(() -> new ResourceNotFoundException("Hebergement non trouve"));

        ReservationHebergement reservation = new ReservationHebergement(
                hebergement,
                dto.getNomClient(),
                dto.getPrenomClient(),
                dto.getEmailClient(),
                dto.getTelephoneClient(),
                dto.getDateArrivee(),
                dto.getDateDepart(),
                dto.getNombrePersonnes(),
                dto.getCommentaires()
        );

        if (dto.getId() != null) {
            reservation.setIdReservation(dto.getId());
        }
        reservation.setStatut(normalizeStatus(dto.getStatut()));

        return reservation;
    }

    public List<ReservationHebergementDTO> getAll() {
        return reservationRepo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ReservationHebergementDTO getById(Long id) {
        return reservationRepo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation non trouvee"));
    }

    public ReservationHebergementDTO create(ReservationHebergementDTO dto) {
        validateStayDatesForCreate(dto.getDateArrivee(), dto.getDateDepart());
        if (!isAvailable(dto.getHebergementId(), dto.getDateArrivee(), dto.getDateDepart())) {
            throw new BadRequestException("L'hebergement n'est pas disponible pour ces dates");
        }
        ReservationHebergement saved = reservationRepo.save(fromDTO(dto));
        notificationService.sendCreationConfirmation(saved);
        return toDTO(saved);
    }

    public ReservationHebergementDTO update(Long id, ReservationHebergementDTO dto) {
        ReservationHebergement existing = reservationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation non trouvee"));

        validateStayDatesForUpdate(dto.getDateArrivee(), dto.getDateDepart());
        boolean datesChanged = !existing.getDateArrivee().equals(dto.getDateArrivee())
                || !existing.getDateDepart().equals(dto.getDateDepart());
        if (datesChanged && !isAvailableForUpdate(existing, dto.getDateArrivee(), dto.getDateDepart())) {
            throw new BadRequestException("L'hebergement n'est pas disponible pour ces dates");
        }

        String previousStatus = normalizeStatus(existing.getStatut());
        existing.setNomClient(dto.getNomClient());
        existing.setPrenomClient(dto.getPrenomClient());
        existing.setEmailClient(dto.getEmailClient());
        existing.setTelephoneClient(dto.getTelephoneClient());
        existing.setDateArrivee(dto.getDateArrivee());
        existing.setDateDepart(dto.getDateDepart());
        existing.setNombrePersonnes(dto.getNombrePersonnes());
        existing.setCommentaires(dto.getCommentaires());
        existing.setStatut(normalizeStatus(dto.getStatut()));

        int nights = (int) ChronoUnit.DAYS.between(dto.getDateArrivee(), dto.getDateDepart());
        existing.setNombreNuits(nights);
        existing.setPrixTotal(BigDecimal.valueOf(nights).multiply(existing.getHebergement().getPrixParNuit()));

        ReservationHebergement saved = reservationRepo.save(existing);
        if (!previousStatus.equals(normalizeStatus(saved.getStatut()))) {
            notificationService.sendStatusUpdate(saved);
        }
        return toDTO(saved);
    }

    public void delete(Long id) {
        reservationRepo.deleteById(id);
    }

    public boolean isAvailable(Long hebergementId, LocalDate dateArrivee, LocalDate dateDepart) {
        validateStayDatesForUpdate(dateArrivee, dateDepart);
        List<ReservationHebergement> reservations = reservationRepo.findByHebergementIdHebergement(hebergementId);

        for (ReservationHebergement reservation : reservations) {
            if (isCancelledStatus(reservation.getStatut())) {
                continue;
            }
            if (hasOverlap(dateArrivee, dateDepart, reservation.getDateArrivee(), reservation.getDateDepart())) {
                return false;
            }
        }
        return true;
    }

    public List<ReservationHebergementDTO> getByHebergement(Long hebergementId) {
        return reservationRepo.findByHebergementIdHebergement(hebergementId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationHebergementDTO> getByStatut(String statut) {
        return reservationRepo.findByStatut(normalizeStatus(statut)).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private boolean isAvailableForUpdate(ReservationHebergement existing, LocalDate dateArrivee, LocalDate dateDepart) {
        List<ReservationHebergement> reservations =
                reservationRepo.findByHebergementIdHebergement(existing.getHebergement().getIdHebergement());

        for (ReservationHebergement reservation : reservations) {
            if (reservation.getIdReservation().equals(existing.getIdReservation())) {
                continue;
            }
            if (isCancelledStatus(reservation.getStatut())) {
                continue;
            }
            if (hasOverlap(dateArrivee, dateDepart, reservation.getDateArrivee(), reservation.getDateDepart())) {
                return false;
            }
        }
        return true;
    }

    private void validateStayDatesForCreate(LocalDate dateArrivee, LocalDate dateDepart) {
        validateStayDatesForUpdate(dateArrivee, dateDepart);
        if (dateArrivee.isBefore(LocalDate.now())) {
            throw new BadRequestException("La date d'arrivee ne peut pas etre dans le passe");
        }
    }

    private void validateStayDatesForUpdate(LocalDate dateArrivee, LocalDate dateDepart) {
        if (dateArrivee == null || dateDepart == null) {
            throw new BadRequestException("Les dates d'arrivee et de depart sont obligatoires");
        }
        if (!dateDepart.isAfter(dateArrivee)) {
            throw new BadRequestException("La date de depart doit etre strictement apres la date d'arrivee");
        }
    }

    private boolean hasOverlap(LocalDate startA, LocalDate endA, LocalDate startB, LocalDate endB) {
        return startA.isBefore(endB) && endA.isAfter(startB);
    }

    private boolean isCancelledStatus(String statut) {
        String normalized = normalizeStatus(statut);
        return "ANNULEE".equals(normalized) || "ANNULE".equals(normalized);
    }

    private String normalizeStatus(String statut) {
        if (statut == null || statut.trim().isEmpty()) {
            return "EN_ATTENTE";
        }
        String normalized = statut.trim().toUpperCase();
        if ("ANNULE".equals(normalized)) {
            return "ANNULEE";
        }
        return normalized;
    }
}
