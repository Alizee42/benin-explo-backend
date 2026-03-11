package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.CircuitPersonnaliseDTO;
import com.beninexplo.backend.entity.Activite;
import com.beninexplo.backend.entity.CircuitPersonnalise;
import com.beninexplo.backend.entity.CircuitPersonnaliseJour;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.ActiviteRepository;
import com.beninexplo.backend.repository.CircuitPersonnaliseJourRepository;
import com.beninexplo.backend.repository.CircuitPersonnaliseRepository;
import com.beninexplo.backend.repository.VilleRepository;
import com.beninexplo.backend.repository.ZoneRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CircuitPersonnaliseService {

    private final CircuitPersonnaliseRepository circuitRepository;
    private final CircuitPersonnaliseJourRepository jourRepository;
    private final ZoneRepository zoneRepository;
    private final VilleRepository villeRepository;
    private final ActiviteRepository activiteRepository;

    public CircuitPersonnaliseService(CircuitPersonnaliseRepository circuitRepository,
                                      CircuitPersonnaliseJourRepository jourRepository,
                                      ZoneRepository zoneRepository,
                                      VilleRepository villeRepository,
                                      ActiviteRepository activiteRepository) {
        this.circuitRepository = circuitRepository;
        this.jourRepository = jourRepository;
        this.zoneRepository = zoneRepository;
        this.villeRepository = villeRepository;
        this.activiteRepository = activiteRepository;
    }

    public CircuitPersonnaliseDTO create(CircuitPersonnaliseDTO dto) {
        CircuitPersonnalise entity = new CircuitPersonnalise();

        entity.setNomClient(dto.getNomClient());
        entity.setPrenomClient(dto.getPrenomClient());
        entity.setEmailClient(dto.getEmailClient());
        entity.setTelephoneClient(dto.getTelephoneClient());
        entity.setMessageClient(dto.getMessageClient());
        entity.setNombreJours(dto.getNombreJours());
        entity.setNombrePersonnes(dto.getNombrePersonnes());
        entity.setDateVoyageSouhaitee(dto.getDateVoyageSouhaitee());
        entity.setAvecHebergement(dto.isAvecHebergement());
        entity.setTypeHebergement(dto.getTypeHebergement());
        entity.setAvecTransport(dto.isAvecTransport());
        entity.setTypeTransport(dto.getTypeTransport());
        entity.setAvecGuide(dto.isAvecGuide());
        entity.setAvecChauffeur(dto.isAvecChauffeur());
        entity.setPensionComplete(dto.isPensionComplete());
        entity.setStatut(CircuitPersonnalise.StatutDemande.EN_ATTENTE);

        entity = circuitRepository.save(entity);

        if (dto.getJours() != null && !dto.getJours().isEmpty()) {
            for (CircuitPersonnaliseDTO.JourDTO jourDto : dto.getJours()) {
                CircuitPersonnaliseJour jour = new CircuitPersonnaliseJour();
                jour.setCircuitPersonnalise(entity);
                jour.setNumeroJour(jourDto.getNumeroJour());
                jour.setDescriptionJour(jourDto.getDescriptionJour());

                if (jourDto.getZoneId() != null) {
                    Zone zone = zoneRepository.findById(jourDto.getZoneId())
                            .orElseThrow(() -> new ResourceNotFoundException("Zone non trouvee: " + jourDto.getZoneId()));
                    jour.setZone(zone);
                }

                if (jourDto.getVilleId() != null) {
                    Ville ville = villeRepository.findById(jourDto.getVilleId())
                            .orElseThrow(() -> new ResourceNotFoundException("Ville non trouvee: " + jourDto.getVilleId()));
                    jour.setVille(ville);
                }

                if (jourDto.getActiviteIds() != null && !jourDto.getActiviteIds().isEmpty()) {
                    List<Activite> activites = activiteRepository.findAllById(jourDto.getActiviteIds());
                    if (activites.size() != jourDto.getActiviteIds().size()) {
                        throw new BadRequestException("Une ou plusieurs activites sont introuvables pour le jour " + jourDto.getNumeroJour());
                    }

                    validateJourActivities(jour, jourDto, activites);
                    jour.setActivites(activites);
                }

                jourRepository.save(jour);
            }
        }

        return toDTO(circuitRepository.findById(entity.getId()).orElseThrow());
    }

    public List<CircuitPersonnaliseDTO> getAll() {
        return circuitRepository.findAllByOrderByDateCreationDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CircuitPersonnaliseDTO getById(Long id) {
        CircuitPersonnalise entity = circuitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Circuit personnalise non trouve: " + id));
        return toDTO(entity);
    }

    public List<CircuitPersonnaliseDTO> getByStatut(String statut) {
        CircuitPersonnalise.StatutDemande statutEnum = CircuitPersonnalise.StatutDemande.valueOf(statut);
        return circuitRepository.findByStatutOrderByDateCreationDesc(statutEnum)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CircuitPersonnaliseDTO updateStatut(Long id, String nouveauStatut, BigDecimal prixFinal) {
        CircuitPersonnalise entity = circuitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Circuit personnalise non trouve: " + id));

        CircuitPersonnalise.StatutDemande statutEnum = CircuitPersonnalise.StatutDemande.valueOf(nouveauStatut);
        entity.setStatut(statutEnum);

        if (prixFinal != null) {
            entity.setPrixFinal(prixFinal);
        }

        entity = circuitRepository.save(entity);
        return toDTO(entity);
    }

    public void delete(Long id) {
        CircuitPersonnalise entity = circuitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Circuit personnalise non trouve: " + id));

        jourRepository.deleteAll(entity.getJours());
        circuitRepository.delete(entity);
    }

    private void validateJourActivities(CircuitPersonnaliseJour jour,
                                        CircuitPersonnaliseDTO.JourDTO jourDto,
                                        List<Activite> activites) {
        for (Activite activite : activites) {
            Ville activiteVille = activite.getVille();
            if (activiteVille == null) {
                throw new BadRequestException("L'activite '" + activite.getNom() + "' n'est associee a aucune ville.");
            }

            if (jour.getVille() != null
                    && (activiteVille.getIdVille() == null
                    || !activiteVille.getIdVille().equals(jour.getVille().getIdVille()))) {
                throw new BadRequestException(
                        "L'activite '" + activite.getNom() + "' n'appartient pas a la ville selectionnee pour le jour " + jourDto.getNumeroJour()
                );
            }

            if (jour.getZone() != null) {
                Zone activiteZone = activiteVille.getZone();
                if (activiteZone == null || activiteZone.getIdZone() == null
                        || !activiteZone.getIdZone().equals(jour.getZone().getIdZone())) {
                    throw new BadRequestException(
                            "L'activite '" + activite.getNom() + "' n'appartient pas a la zone selectionnee pour le jour " + jourDto.getNumeroJour()
                    );
                }
            }
        }
    }

    private CircuitPersonnaliseDTO toDTO(CircuitPersonnalise entity) {
        CircuitPersonnaliseDTO dto = new CircuitPersonnaliseDTO();

        dto.setId(entity.getId());
        dto.setNomClient(entity.getNomClient());
        dto.setPrenomClient(entity.getPrenomClient());
        dto.setEmailClient(entity.getEmailClient());
        dto.setTelephoneClient(entity.getTelephoneClient());
        dto.setMessageClient(entity.getMessageClient());
        dto.setNombreJours(entity.getNombreJours());
        dto.setNombrePersonnes(entity.getNombrePersonnes());
        dto.setDateCreation(entity.getDateCreation());
        dto.setDateVoyageSouhaitee(entity.getDateVoyageSouhaitee());
        dto.setAvecHebergement(entity.isAvecHebergement());
        dto.setTypeHebergement(entity.getTypeHebergement());
        dto.setAvecTransport(entity.isAvecTransport());
        dto.setTypeTransport(entity.getTypeTransport());
        dto.setAvecGuide(entity.isAvecGuide());
        dto.setAvecChauffeur(entity.isAvecChauffeur());
        dto.setPensionComplete(entity.isPensionComplete());
        dto.setPrixEstime(entity.getPrixEstime());
        dto.setPrixFinal(entity.getPrixFinal());
        dto.setStatut(entity.getStatut().name());

        if (entity.getCircuitCree() != null) {
            dto.setCircuitCreeId(entity.getCircuitCree().getIdCircuit());
        }

        List<CircuitPersonnaliseDTO.JourDTO> joursDto = entity.getJours().stream()
                .map(this::jourToDTO)
                .collect(Collectors.toList());
        dto.setJours(joursDto);

        return dto;
    }

    private CircuitPersonnaliseDTO.JourDTO jourToDTO(CircuitPersonnaliseJour jour) {
        CircuitPersonnaliseDTO.JourDTO dto = new CircuitPersonnaliseDTO.JourDTO();

        dto.setId(jour.getId());
        dto.setNumeroJour(jour.getNumeroJour());
        dto.setDescriptionJour(jour.getDescriptionJour());

        if (jour.getZone() != null) {
            dto.setZoneId(jour.getZone().getIdZone());
            dto.setZoneNom(jour.getZone().getNom());
        }

        if (jour.getVille() != null) {
            dto.setVilleId(jour.getVille().getIdVille());
            dto.setVilleNom(jour.getVille().getNom());
        }

        if (jour.getActivites() != null && !jour.getActivites().isEmpty()) {
            dto.setActiviteIds(jour.getActivites().stream()
                    .map(Activite::getIdActivite)
                    .collect(Collectors.toList()));

            dto.setActiviteNoms(jour.getActivites().stream()
                    .map(Activite::getNom)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
