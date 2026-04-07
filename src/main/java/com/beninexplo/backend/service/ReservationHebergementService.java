package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ReservationHebergementDTO;
import com.beninexplo.backend.dto.ReservationHebergementIndisponibiliteDTO;
import com.beninexplo.backend.entity.Hebergement;
import com.beninexplo.backend.entity.PaiementReservationHebergement;
import com.beninexplo.backend.entity.ReservationHebergement;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.HebergementRepository;
import com.beninexplo.backend.repository.PaiementReservationHebergementRepository;
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
    private final PaiementReservationHebergementRepository paymentRepository;
    private final ReservationHebergementNotificationService notificationService;
    private final AuthenticatedUserService authenticatedUserService;

    public ReservationHebergementService(ReservationHebergementRepository reservationRepo,
                                         HebergementRepository hebergementRepo,
                                         PaiementReservationHebergementRepository paymentRepository,
                                         ReservationHebergementNotificationService notificationService,
                                         AuthenticatedUserService authenticatedUserService) {
        this.reservationRepo = reservationRepo;
        this.hebergementRepo = hebergementRepo;
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
        this.authenticatedUserService = authenticatedUserService;
    }

    private ReservationHebergementDTO toDTO(ReservationHebergement reservation) {
        ReservationHebergementDTO dto = new ReservationHebergementDTO(
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
        if (reservation.getUtilisateur() != null) {
            dto.setUtilisateurId(reservation.getUtilisateur().getId());
        }
        PaiementReservationHebergement payment = reservation.getPaiement();
        if (payment != null) {
            dto.setStatutPaiement(payment.getStatut());
            dto.setMontantPaye(payment.getMontant());
            dto.setDevisePaiement(payment.getDevise());
            dto.setPaypalOrderId(payment.getPaypalOrderId());
            dto.setPaypalCaptureId(payment.getPaypalCaptureId());
            dto.setDatePaiement(payment.getDatePaiement());
        }
        return dto;
    }

    private ReservationHebergement fromDTO(ReservationHebergementDTO dto, Utilisateur utilisateur) {
        Hebergement hebergement = hebergementRepo.findById(dto.getHebergementId())
                .orElseThrow(() -> new ResourceNotFoundException("Hebergement non trouve"));

        ReservationHebergement reservation = new ReservationHebergement(
                hebergement,
                resolveRequiredText(dto.getNomClient(), utilisateur != null ? utilisateur.getNom() : null,
                        "Le nom du client est obligatoire."),
                resolveRequiredText(dto.getPrenomClient(), utilisateur != null ? utilisateur.getPrenom() : null,
                        "Le prenom du client est obligatoire."),
                utilisateur == null
                        ? resolveRequiredText(dto.getEmailClient(), null, "L'email du client est obligatoire.")
                        : resolveRequiredText(utilisateur.getEmail(), dto.getEmailClient(),
                        "L'email du compte est obligatoire."),
                resolveRequiredText(dto.getTelephoneClient(), utilisateur != null ? utilisateur.getTelephone() : null,
                        "Le telephone du client est obligatoire."),
                dto.getDateArrivee(),
                dto.getDateDepart(),
                dto.getNombrePersonnes(),
                dto.getCommentaires()
        );

        if (dto.getId() != null) {
            reservation.setIdReservation(dto.getId());
        }
        reservation.setStatut(normalizeStatus(dto.getStatut()));
        reservation.setUtilisateur(utilisateur);

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
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        validateStayDatesForCreate(dto.getDateArrivee(), dto.getDateDepart());
        if (!isAvailable(dto.getHebergementId(), dto.getDateArrivee(), dto.getDateDepart())) {
            throw new BadRequestException("L'hebergement n'est pas disponible pour ces dates");
        }
        ReservationHebergement saved = reservationRepo.save(fromDTO(dto, currentUser));
        ensurePaymentPlaceholder(saved);
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
        existing.setEmailClient(existing.getUtilisateur() != null
                ? existing.getUtilisateur().getEmail()
                : dto.getEmailClient());
        existing.setTelephoneClient(dto.getTelephoneClient());
        existing.setDateArrivee(dto.getDateArrivee());
        existing.setDateDepart(dto.getDateDepart());
        existing.setNombrePersonnes(dto.getNombrePersonnes());
        existing.setCommentaires(dto.getCommentaires());
        existing.setStatut(normalizeStatus(dto.getStatut()));

        int nights = (int) ChronoUnit.DAYS.between(dto.getDateArrivee(), dto.getDateDepart());
        existing.setNombreNuits(nights);
        existing.setPrixTotal(BigDecimal.valueOf(nights).multiply(existing.getHebergement().getPrixParNuit()));
        syncPaymentAmount(existing);

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

    public List<ReservationHebergementIndisponibiliteDTO> getBookedRangesByHebergement(Long hebergementId) {
        return reservationRepo.findByHebergementIdHebergement(hebergementId).stream()
                .filter(reservation -> !isCancelledStatus(reservation.getStatut()))
                .map(reservation -> new ReservationHebergementIndisponibiliteDTO(
                        reservation.getDateArrivee(),
                        reservation.getDateDepart()
                ))
                .collect(Collectors.toList());
    }

    public List<ReservationHebergementDTO> getByStatut(String statut) {
        return reservationRepo.findByStatut(normalizeStatus(statut)).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationHebergementDTO> getByEmail(String email) {
        return reservationRepo.findByEmailClient(email).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationHebergementDTO> getMine() {
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        return reservationRepo.findByUtilisateurIdOrderByDateCreationDesc(currentUser.getId()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ReservationHebergementDTO getMineById(Long id) {
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        return reservationRepo.findByIdReservationAndUtilisateurId(id, currentUser.getId())
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation non trouvee pour ce compte."));
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

    private String resolveRequiredText(String preferredValue, String fallbackValue, String errorMessage) {
        if (preferredValue != null && !preferredValue.trim().isEmpty()) {
            return preferredValue.trim();
        }
        if (fallbackValue != null && !fallbackValue.trim().isEmpty()) {
            return fallbackValue.trim();
        }
        throw new BadRequestException(errorMessage);
    }

    private void ensurePaymentPlaceholder(ReservationHebergement reservation) {
        if (reservation.getPrixTotal() == null || reservation.getPrixTotal().signum() <= 0) {
            return;
        }
        if (paymentRepository.findByReservationHebergementIdReservation(reservation.getIdReservation()).isPresent()) {
            return;
        }

        PaiementReservationHebergement payment = new PaiementReservationHebergement();
        payment.setReservationHebergement(reservation);
        payment.setProvider("PAYPAL");
        payment.setStatut("A_PAYER");
        payment.setMontant(reservation.getPrixTotal());
        payment.setDevise("EUR");
        paymentRepository.save(payment);
        reservation.setPaiement(payment);
    }

    private void syncPaymentAmount(ReservationHebergement reservation) {
        PaiementReservationHebergement payment = reservation.getPaiement();
        if (payment == null) {
            ensurePaymentPlaceholder(reservation);
            return;
        }
        String paymentStatus = payment.getStatut() == null ? "" : payment.getStatut().trim().toUpperCase();
        if (!"PAYE".equals(paymentStatus) && !"REMBOURSE".equals(paymentStatus)) {
            payment.setMontant(reservation.getPrixTotal());
            if (payment.getDevise() == null || payment.getDevise().isBlank()) {
                payment.setDevise("EUR");
            }
            paymentRepository.save(payment);
        }
    }
}
