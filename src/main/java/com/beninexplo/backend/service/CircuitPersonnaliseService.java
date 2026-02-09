package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.CircuitPersonnaliseDTO;
import com.beninexplo.backend.entity.*;
import com.beninexplo.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    public CircuitPersonnaliseService(
            CircuitPersonnaliseRepository circuitRepository,
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

    // ----------------------------------------------------
    // CREATE
    // ----------------------------------------------------
    public CircuitPersonnaliseDTO create(CircuitPersonnaliseDTO dto) {
        CircuitPersonnalise entity = new CircuitPersonnalise();
        
        // Client
        entity.setNomClient(dto.getNomClient());
        entity.setPrenomClient(dto.getPrenomClient());
        entity.setEmailClient(dto.getEmailClient());
        entity.setTelephoneClient(dto.getTelephoneClient());
        entity.setMessageClient(dto.getMessageClient());
        
        // Paramètres
        entity.setNombreJours(dto.getNombreJours());
        entity.setNombrePersonnes(dto.getNombrePersonnes());
        entity.setDateVoyageSouhaitee(dto.getDateVoyageSouhaitee());
        
        // Options
        entity.setAvecHebergement(dto.isAvecHebergement());
        entity.setTypeHebergement(dto.getTypeHebergement());
        entity.setAvecTransport(dto.isAvecTransport());
        entity.setTypeTransport(dto.getTypeTransport());
        entity.setAvecGuide(dto.isAvecGuide());
        entity.setAvecChauffeur(dto.isAvecChauffeur());
        entity.setPensionComplete(dto.isPensionComplete());
        
        // Statut par défaut
        entity.setStatut(CircuitPersonnalise.StatutDemande.EN_ATTENTE);
        
        // Sauvegarder le circuit
        entity = circuitRepository.save(entity);
        
        // Créer les jours
        if (dto.getJours() != null && !dto.getJours().isEmpty()) {
            for (CircuitPersonnaliseDTO.JourDTO jourDto : dto.getJours()) {
                CircuitPersonnaliseJour jour = new CircuitPersonnaliseJour();
                jour.setCircuitPersonnalise(entity);
                jour.setNumeroJour(jourDto.getNumeroJour());
                jour.setDescriptionJour(jourDto.getDescriptionJour());
                
                // Zone
                if (jourDto.getZoneId() != null) {
                    Zone zone = zoneRepository.findById(jourDto.getZoneId())
                        .orElseThrow(() -> new RuntimeException("Zone non trouvée: " + jourDto.getZoneId()));
                    jour.setZone(zone);
                }
                
                // Ville
                if (jourDto.getVilleId() != null) {
                    Ville ville = villeRepository.findById(jourDto.getVilleId())
                        .orElseThrow(() -> new RuntimeException("Ville non trouvée: " + jourDto.getVilleId()));
                    jour.setVille(ville);
                }
                
                // Activités
                if (jourDto.getActiviteIds() != null && !jourDto.getActiviteIds().isEmpty()) {
                    List<Activite> activites = activiteRepository.findAllById(jourDto.getActiviteIds());
                    jour.setActivites(activites);
                }
                
                jourRepository.save(jour);
            }
        }
        
        return toDTO(circuitRepository.findById(entity.getId()).orElseThrow());
    }

    // ----------------------------------------------------
    // GET ALL
    // ----------------------------------------------------
    public List<CircuitPersonnaliseDTO> getAll() {
        return circuitRepository.findAllByOrderByDateCreationDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------
    // GET BY ID
    // ----------------------------------------------------
    public CircuitPersonnaliseDTO getById(Long id) {
        CircuitPersonnalise entity = circuitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Circuit personnalisé non trouvé: " + id));
        return toDTO(entity);
    }

    // ----------------------------------------------------
    // GET BY STATUT
    // ----------------------------------------------------
    public List<CircuitPersonnaliseDTO> getByStatut(String statut) {
        CircuitPersonnalise.StatutDemande statutEnum = CircuitPersonnalise.StatutDemande.valueOf(statut);
        return circuitRepository.findByStatutOrderByDateCreationDesc(statutEnum)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------
    // UPDATE STATUT
    // ----------------------------------------------------
    public CircuitPersonnaliseDTO updateStatut(Long id, String nouveauStatut, BigDecimal prixFinal) {
        CircuitPersonnalise entity = circuitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Circuit personnalisé non trouvé: " + id));
        
        CircuitPersonnalise.StatutDemande statutEnum = CircuitPersonnalise.StatutDemande.valueOf(nouveauStatut);
        entity.setStatut(statutEnum);
        
        if (prixFinal != null) {
            entity.setPrixFinal(prixFinal);
        }
        
        entity = circuitRepository.save(entity);
        return toDTO(entity);
    }

    // ----------------------------------------------------
    // DELETE
    // ----------------------------------------------------
    public void delete(Long id) {
        CircuitPersonnalise entity = circuitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Circuit personnalisé non trouvé: " + id));
        
        // Supprimer les jours
        jourRepository.deleteAll(entity.getJours());
        
        // Supprimer le circuit
        circuitRepository.delete(entity);
    }

    // ----------------------------------------------------
    // MAPPING ENTITY -> DTO
    // ----------------------------------------------------
    private CircuitPersonnaliseDTO toDTO(CircuitPersonnalise entity) {
        CircuitPersonnaliseDTO dto = new CircuitPersonnaliseDTO();
        
        dto.setId(entity.getId());
        
        // Client
        dto.setNomClient(entity.getNomClient());
        dto.setPrenomClient(entity.getPrenomClient());
        dto.setEmailClient(entity.getEmailClient());
        dto.setTelephoneClient(entity.getTelephoneClient());
        dto.setMessageClient(entity.getMessageClient());
        
        // Paramètres
        dto.setNombreJours(entity.getNombreJours());
        dto.setNombrePersonnes(entity.getNombrePersonnes());
        dto.setDateCreation(entity.getDateCreation());
        dto.setDateVoyageSouhaitee(entity.getDateVoyageSouhaitee());
        
        // Options
        dto.setAvecHebergement(entity.isAvecHebergement());
        dto.setTypeHebergement(entity.getTypeHebergement());
        dto.setAvecTransport(entity.isAvecTransport());
        dto.setTypeTransport(entity.getTypeTransport());
        dto.setAvecGuide(entity.isAvecGuide());
        dto.setAvecChauffeur(entity.isAvecChauffeur());
        dto.setPensionComplete(entity.isPensionComplete());
        
        // Prix
        dto.setPrixEstime(entity.getPrixEstime());
        dto.setPrixFinal(entity.getPrixFinal());
        
        // Statut
        dto.setStatut(entity.getStatut().name());
        
        // Circuit créé
        if (entity.getCircuitCree() != null) {
            dto.setCircuitCreeId(entity.getCircuitCree().getIdCircuit());
        }
        
        // Jours
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
        
        // Zone
        if (jour.getZone() != null) {
            dto.setZoneId(jour.getZone().getIdZone());
            dto.setZoneNom(jour.getZone().getNom());
        }
        
        // Ville
        if (jour.getVille() != null) {
            dto.setVilleId(jour.getVille().getIdVille());
            dto.setVilleNom(jour.getVille().getNom());
        }
        
        // Activités
        if (jour.getActivites() != null && !jour.getActivites().isEmpty()) {
            List<Long> activiteIds = jour.getActivites().stream()
                    .map(Activite::getIdActivite)
                    .collect(Collectors.toList());
            dto.setActiviteIds(activiteIds);
            
            List<String> activiteNoms = jour.getActivites().stream()
                    .map(Activite::getNom)
                    .collect(Collectors.toList());
            dto.setActiviteNoms(activiteNoms);
        }
        
        return dto;
    }
}
