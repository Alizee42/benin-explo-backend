package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.CircuitDTO;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.repository.CircuitRepository;
import com.beninexplo.backend.repository.VilleRepository;
import com.beninexplo.backend.repository.ZoneRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CircuitService {

    private final CircuitRepository circuitRepo;
    private final VilleRepository villeRepo;
    private final ZoneRepository zoneRepo;

    public CircuitService(CircuitRepository circuitRepo,
                          VilleRepository villeRepo,
                          ZoneRepository zoneRepo) {
        this.circuitRepo = circuitRepo;
        this.villeRepo = villeRepo;
        this.zoneRepo = zoneRepo;
    }

    // ---------------------------------------
    // DTO MAPPING
    // ---------------------------------------

    private CircuitDTO toDTO(Circuit c) {
        if (c == null) return null;

        CircuitDTO dto = new CircuitDTO();

        dto.setId(c.getIdCircuit());
        dto.setTitre(c.getNom()); // Map nom to titre
        dto.setDescription(c.getDescription());
        dto.setDureeIndicative(c.getDureeIndicative());
        dto.setPrixIndicatif(c.getPrixIndicatif());
        dto.setFormuleProposee(c.getFormuleProposee());
        dto.setActif(c.isActif());

        // Ville (remplace localisation)
        if (c.getVille() != null) {
            dto.setVilleId(c.getVille().getIdVille());
            dto.setVilleNom(c.getVille().getNom());
            dto.setLocalisation(c.getVille().getNom()); // Pour compatibilité temporaire
        }

        // Image principale (URL ou base64) stockée en TEXT
        dto.setImg(c.getImg());

        // Galerie : stockée en JSON dans l'entité, convertie en List<String>
        if (c.getGalerie() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<String> gal = mapper.readValue(c.getGalerie(), new TypeReference<List<String>>(){});
                dto.setGalerie(gal);
            } catch (JsonProcessingException e) {
                dto.setGalerie(Collections.emptyList());
            }
        } else {
            dto.setGalerie(Collections.emptyList());
        }

        // Resume (court chapeau)
        dto.setResume(c.getResume());

        // Programme
        if (c.getProgramme() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<String> prog = mapper.readValue(c.getProgramme(), new TypeReference<List<String>>(){});
                dto.setProgramme(prog);
            } catch (JsonProcessingException e) {
                dto.setProgramme(Collections.emptyList());
            }
        } else {
            dto.setProgramme(Collections.emptyList());
        }

        // Points forts (liste d'objets)
        if (c.getPointsForts() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<CircuitDTO.PointFort> forts = mapper.readValue(c.getPointsForts(), new TypeReference<List<CircuitDTO.PointFort>>(){});
                dto.setPointsForts(forts);
            } catch (JsonProcessingException e) {
                dto.setPointsForts(Collections.emptyList());
            }
        } else {
            dto.setPointsForts(Collections.emptyList());
        }

        // Inclus / Non inclus
        if (c.getInclus() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<String> incl = mapper.readValue(c.getInclus(), new TypeReference<List<String>>(){});
                dto.setInclus(incl);
            } catch (JsonProcessingException e) {
                dto.setInclus(Collections.emptyList());
            }
        } else {
            dto.setInclus(Collections.emptyList());
        }

        if (c.getNonInclus() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<String> non = mapper.readValue(c.getNonInclus(), new TypeReference<List<String>>(){});
                dto.setNonInclus(non);
            } catch (JsonProcessingException e) {
                dto.setNonInclus(Collections.emptyList());
            }
        } else {
            dto.setNonInclus(Collections.emptyList());
        }

        // Tourisme / Aventures: non mappés pour l'instant (peuvent être ajoutés au DTO si besoin)

        if (c.getZone() != null)
            dto.setZoneId(c.getZone().getIdZone());

        // Pour l'instant, on ne mappe pas les nouveaux champs car l'entité ne les a pas
        // Ces champs seront gérés plus tard quand l'entité sera mise à jour

        return dto;
    }

    private void updateEntity(Circuit c, CircuitDTO dto) {

        c.setNom(dto.getTitre()); // Map titre to nom
        c.setDescription(dto.getDescription());
        c.setDureeIndicative(dto.getDureeIndicative());
        c.setPrixIndicatif(dto.getPrixIndicatif());
        c.setFormuleProposee(dto.getFormuleProposee());
        c.setActif(dto.isActif());

        // Ville (remplace localisation)
        if (dto.getVilleId() != null) {
            Ville ville = villeRepo.findById(dto.getVilleId()).orElse(null);
            c.setVille(ville);
        }

        // Image principale
        c.setImg(dto.getImg());

        // Galerie : sérialiser la liste en JSON
        if (dto.getGalerie() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                c.setGalerie(mapper.writeValueAsString(dto.getGalerie()));
            } catch (JsonProcessingException e) {
                c.setGalerie(null);
            }
        } else {
            c.setGalerie(null);
        }

        // Resume
        c.setResume(dto.getResume());

        // Programme
        if (dto.getProgramme() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                c.setProgramme(mapper.writeValueAsString(dto.getProgramme()));
            } catch (JsonProcessingException e) {
                c.setProgramme(null);
            }
        } else {
            c.setProgramme(null);
        }

        // Points forts
        if (dto.getPointsForts() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                c.setPointsForts(mapper.writeValueAsString(dto.getPointsForts()));
            } catch (JsonProcessingException e) {
                c.setPointsForts(null);
            }
        } else {
            c.setPointsForts(null);
        }

        // Inclus / NonInclus
        if (dto.getInclus() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                c.setInclus(mapper.writeValueAsString(dto.getInclus()));
            } catch (JsonProcessingException e) {
                c.setInclus(null);
            }
        } else {
            c.setInclus(null);
        }

        if (dto.getNonInclus() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                c.setNonInclus(mapper.writeValueAsString(dto.getNonInclus()));
            } catch (JsonProcessingException e) {
                c.setNonInclus(null);
            }
        } else {
            c.setNonInclus(null);
        }

        // Zone
        if (dto.getZoneId() != null) {
            Zone z = zoneRepo.findById(dto.getZoneId()).orElse(null);
            c.setZone(z);
            // Note: Si la zone n'existe pas, on ne plante pas, on la laisse à null
        } else {
            c.setZone(null);
        }

        // Pour l'instant, on ignore les nouveaux champs car l'entité ne les supporte pas
        // Ces champs seront gérés plus tard quand l'entité sera mise à jour
    }

    // ---------------------------------------
    // CRUD
    // ---------------------------------------

    public List<CircuitDTO> getAll() {
        return circuitRepo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CircuitDTO getById(Long id) {
        return circuitRepo.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public CircuitDTO create(CircuitDTO dto) {
        Circuit circuit = new Circuit();
        updateEntity(circuit, dto);
        circuitRepo.save(circuit);
        return toDTO(circuit);
    }

    public CircuitDTO update(Long id, CircuitDTO dto) {
        Circuit circuit = circuitRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Circuit non trouvé"));
        updateEntity(circuit, dto);
        circuitRepo.save(circuit);
        return toDTO(circuit);
    }

    public void delete(Long id) {
        circuitRepo.deleteById(id);
    }

    // ---------------------------------------
    // FILTRES & RECHERCHES
    // ---------------------------------------

    public List<CircuitDTO> getActifs() {
        return circuitRepo.findByActifTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CircuitDTO> getByZone(Long zoneId) {
        return circuitRepo.findByZone_IdZone(zoneId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CircuitDTO> searchByNom(String nom) {
        return circuitRepo.findByNomContainingIgnoreCase(nom)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
