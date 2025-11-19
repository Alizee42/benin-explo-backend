package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.TombolaParticipantDTO;
import com.beninexplo.backend.entity.TombolaParticipant;
import com.beninexplo.backend.repository.TombolaParticipantRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TombolaParticipantService {

    private final TombolaParticipantRepository repo;

    public TombolaParticipantService(TombolaParticipantRepository repo) {
        this.repo = repo;
    }

    private TombolaParticipantDTO toDTO(TombolaParticipant p) {
        TombolaParticipantDTO dto = new TombolaParticipantDTO();

        dto.setId(p.getIdParticipant());
        dto.setNom(p.getNom());
        dto.setPrenom(p.getPrenom());
        dto.setEmail(p.getEmail());
        dto.setTelephone(p.getTelephone());
        dto.setDateParticipation(p.getDateParticipation().toString());
        dto.setStatut(p.getStatut());
        dto.setCodeUnique(p.getCodeUnique());

        return dto;
    }

    private TombolaParticipant fromDTO(TombolaParticipantDTO dto) {
        TombolaParticipant p = new TombolaParticipant();

        p.setIdParticipant(dto.getId());
        p.setNom(dto.getNom());
        p.setPrenom(dto.getPrenom());
        p.setEmail(dto.getEmail());
        p.setTelephone(dto.getTelephone());
        p.setStatut(dto.getStatut());
        p.setCodeUnique(dto.getCodeUnique());

        return p;
    }

    public List<TombolaParticipantDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TombolaParticipantDTO get(Long id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    public TombolaParticipantDTO create(TombolaParticipantDTO dto) {

        // Génération automatique d’un code unique
        String codeUnique = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        TombolaParticipant p = new TombolaParticipant();
        p.setNom(dto.getNom());
        p.setPrenom(dto.getPrenom());
        p.setEmail(dto.getEmail());
        p.setTelephone(dto.getTelephone());
        p.setCodeUnique(codeUnique);

        repo.save(p);

        return toDTO(p);
    }

    public TombolaParticipantDTO update(Long id, TombolaParticipantDTO dto) {
        dto.setId(id);
        return toDTO(repo.save(fromDTO(dto)));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
