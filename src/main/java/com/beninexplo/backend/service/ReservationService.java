package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.ReservationRequestDTO;
import com.beninexplo.backend.dto.ReservationResponseDTO;
import com.beninexplo.backend.entity.Reservation;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.entity.Devis;
import com.beninexplo.backend.repository.ReservationRepository;
import com.beninexplo.backend.repository.UtilisateurRepository;
import com.beninexplo.backend.repository.DevisRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
public class ReservationService {

    private final ReservationRepository repo;
    private final UtilisateurRepository utilisateurRepo;
    private final DevisRepository devisRepo;

    public ReservationService(
            ReservationRepository repo,
            UtilisateurRepository utilisateurRepo,
            DevisRepository devisRepo
    ) {
        this.repo = repo;
        this.utilisateurRepo = utilisateurRepo;
        this.devisRepo = devisRepo;
    }

    private ReservationResponseDTO toDTO(Reservation r) {
        ReservationResponseDTO dto = new ReservationResponseDTO();

        dto.setId(r.getIdReservation());

        if (r.getUtilisateur() != null) {
            Long userId = r.getUtilisateur().getIdUtilisateur();
            dto.setUtilisateurId(userId);
        }

        if (r.getDevis() != null) {
            Long devisId = r.getDevis().getIdDevis();
            dto.setDevisId(devisId);
        }

        if (r.getDateDebut() != null)
            dto.setDateDebut(r.getDateDebut().toString());

        if (r.getDateFin() != null)
            dto.setDateFin(r.getDateFin().toString());

        if (r.getMontantTotal() != null)
            dto.setMontantTotal(r.getMontantTotal().toString());

        dto.setStatut(r.getStatut());
        dto.setDateCreation(r.getDateCreation().toString());

        return dto;
    }

    public ReservationResponseDTO create(ReservationRequestDTO dto) {

        Reservation r = new Reservation();

        // Utilisateur lié
        if (dto.getUtilisateurId() != null) {
            Long utilisateurId = dto.getUtilisateurId();
            Objects.requireNonNull(utilisateurId);
            Utilisateur u = utilisateurRepo.findById(utilisateurId).orElse(null);
            r.setUtilisateur(u);
        }

        // Devis lié
        if (dto.getDevisId() != null) {
            Long devisId = dto.getDevisId();
            Objects.requireNonNull(devisId);
            Devis d = devisRepo.findById(devisId).orElse(null);
            r.setDevis(d);
        }

        // Dates
        r.setDateDebut(LocalDate.parse(dto.getDateDebut()));
        r.setDateFin(LocalDate.parse(dto.getDateFin()));

        // Montant
        if (dto.getMontantTotal() != null) {
            r.setMontantTotal(new BigDecimal(dto.getMontantTotal()));
        }

        repo.save(r);

        return toDTO(r);
    }

    public List<ReservationResponseDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ReservationResponseDTO get(Long id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public void delete(Long id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        repo.deleteById(id);
    }
}
