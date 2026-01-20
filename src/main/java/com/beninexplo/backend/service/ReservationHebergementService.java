package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ReservationHebergementDTO;
import com.beninexplo.backend.entity.ReservationHebergement;
import com.beninexplo.backend.entity.Hebergement;
import com.beninexplo.backend.repository.ReservationHebergementRepository;
import com.beninexplo.backend.repository.HebergementRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationHebergementService {

    private final ReservationHebergementRepository reservationRepo;
    private final HebergementRepository hebergementRepo;

    public ReservationHebergementService(ReservationHebergementRepository reservationRepo,
                                       HebergementRepository hebergementRepo) {
        this.reservationRepo = reservationRepo;
        this.hebergementRepo = hebergementRepo;
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
                .orElseThrow(() -> new RuntimeException("Hébergement non trouvé"));

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

        if (dto.getStatut() != null) {
            reservation.setStatut(dto.getStatut());
        }

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
                .orElse(null);
    }

    public ReservationHebergementDTO create(ReservationHebergementDTO dto) {
        // Vérifier la disponibilité des dates
        if (!isAvailable(dto.getHebergementId(), dto.getDateArrivee(), dto.getDateDepart())) {
            throw new RuntimeException("L'hébergement n'est pas disponible pour ces dates");
        }

        ReservationHebergement saved = reservationRepo.save(fromDTO(dto));
        return toDTO(saved);
    }

    public ReservationHebergementDTO update(Long id, ReservationHebergementDTO dto) {
        ReservationHebergement existing = reservationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        // Mettre à jour les champs
        existing.setNomClient(dto.getNomClient());
        existing.setPrenomClient(dto.getPrenomClient());
        existing.setEmailClient(dto.getEmailClient());
        existing.setTelephoneClient(dto.getTelephoneClient());
        existing.setDateArrivee(dto.getDateArrivee());
        existing.setDateDepart(dto.getDateDepart());
        existing.setNombrePersonnes(dto.getNombrePersonnes());
        existing.setCommentaires(dto.getCommentaires());
        existing.setStatut(dto.getStatut());

        // Recalculer le prix et les nuits
        existing.setNombreNuits((int) java.time.temporal.ChronoUnit.DAYS.between(dto.getDateArrivee(), dto.getDateDepart()));
        existing.setPrixTotal(existing.getNombreNuits() * existing.getHebergement().getPrixParNuit());

        return toDTO(reservationRepo.save(existing));
    }

    public void delete(Long id) {
        reservationRepo.deleteById(id);
    }

    public boolean isAvailable(Long hebergementId, LocalDate dateArrivee, LocalDate dateDepart) {
        List<ReservationHebergement> reservations = reservationRepo.findByHebergementIdHebergement(hebergementId);

        for (ReservationHebergement reservation : reservations) {
            if (reservation.getStatut().equals("ANNULE")) {
                continue; // Ignorer les réservations annulées
            }

            // Vérifier si les périodes se chevauchent
            if (!(dateDepart.isBefore(reservation.getDateArrivee()) ||
                  dateArrivee.isAfter(reservation.getDateDepart()))) {
                return false; // Chevauchement trouvé
            }
        }

        return true; // Disponible
    }

    public List<ReservationHebergementDTO> getByHebergement(Long hebergementId) {
        return reservationRepo.findByHebergementIdHebergement(hebergementId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationHebergementDTO> getByStatut(String statut) {
        return reservationRepo.findByStatut(statut).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}