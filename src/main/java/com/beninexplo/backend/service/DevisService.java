package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.DevisRequestDTO;
import com.beninexplo.backend.dto.DevisResponseDTO;
import com.beninexplo.backend.entity.Devis;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.repository.DevisRepository;
import com.beninexplo.backend.repository.UtilisateurRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
public class DevisService {

    private final DevisRepository repo;
    private final UtilisateurRepository utilisateurRepo;

    public DevisService(DevisRepository repo, UtilisateurRepository utilisateurRepo) {
        this.repo = repo;
        this.utilisateurRepo = utilisateurRepo;
    }

    // Convert Entity → DTO
    private DevisResponseDTO toDTO(Devis d) {
        DevisResponseDTO dto = new DevisResponseDTO();

        dto.setId(d.getIdDevis());
        dto.setFormule(d.getFormule());
        dto.setDureeCircuit(d.getDureeCircuit());

        if (d.getDateDebutCircuit() != null)
            dto.setDateDebutCircuit(d.getDateDebutCircuit().toString());

        if (d.getDateFinCircuit() != null)
            dto.setDateFinCircuit(d.getDateFinCircuit().toString());

        dto.setNbAdultes(d.getNbAdultes());
        dto.setNbEnfants(d.getNbEnfants());
        dto.setNbParticipants(d.getNbParticipants());

        dto.setStatut(d.getStatut());
        dto.setDateDemande(d.getDateDemande().toString());

        if (d.getUtilisateur() != null)
            dto.setUtilisateurId(d.getUtilisateur().getIdUtilisateur());

        return dto;
    }

    // Création d’un devis
    public DevisResponseDTO create(DevisRequestDTO dto) {

        Devis d = new Devis();

        // utilisateur (optionnel)
        if (dto.getUtilisateurId() != null) {
            Long utilisateurId = dto.getUtilisateurId();
            Objects.requireNonNull(utilisateurId);
            Utilisateur u = utilisateurRepo.findById(utilisateurId).orElse(null);
            d.setUtilisateur(u);
        }

        d.setFormule(dto.getFormule());
        d.setDureeCircuit(dto.getDureeCircuit());
        d.setDateDebutCircuit(LocalDate.parse(dto.getDateDebutCircuit()));

        // date de fin calculée automatiquement
        d.setDateFinCircuit(
                d.getDateDebutCircuit().plusDays(d.getDureeCircuit())
        );

        d.setNbAdultes(dto.getNbAdultes());
        d.setNbEnfants(dto.getNbEnfants());
        d.setNbParticipants(dto.getNbAdultes() + dto.getNbEnfants());

        repo.save(d);

        return toDTO(d);
    }

    public List<DevisResponseDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DevisResponseDTO get(Long id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public void delete(Long id) {
        Objects.requireNonNull(id, "id ne doit pas être null");
        repo.deleteById(id);
    }
}
