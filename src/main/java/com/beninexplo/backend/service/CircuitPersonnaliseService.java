package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.CircuitPersonnaliseDTO;
import com.beninexplo.backend.dto.TarifsCircuitPersonnaliseDTO;
import com.beninexplo.backend.entity.Activite;
import com.beninexplo.backend.entity.Circuit;
import com.beninexplo.backend.entity.CircuitPersonnalise;
import com.beninexplo.backend.entity.CircuitPersonnaliseJour;
import com.beninexplo.backend.entity.Hebergement;
import com.beninexplo.backend.entity.PaiementCircuitPersonnalise;
import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.entity.Ville;
import com.beninexplo.backend.entity.Zone;
import com.beninexplo.backend.exception.BadRequestException;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.ActiviteRepository;
import com.beninexplo.backend.repository.CircuitPersonnaliseJourRepository;
import com.beninexplo.backend.repository.CircuitPersonnaliseRepository;
import com.beninexplo.backend.repository.HebergementRepository;
import com.beninexplo.backend.repository.VilleRepository;
import com.beninexplo.backend.repository.ZoneRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional
public class CircuitPersonnaliseService {

    private final CircuitPersonnaliseRepository circuitRepository;
    private final CircuitPersonnaliseJourRepository jourRepository;
    private final ZoneRepository zoneRepository;
    private final VilleRepository villeRepository;
    private final ActiviteRepository activiteRepository;
    private final HebergementRepository hebergementRepository;
    private final ReservationHebergementService reservationHebergementService;
    private final TarifsCircuitPersonnaliseService tarifsCircuitPersonnaliseService;
    private final AuthenticatedUserService authenticatedUserService;

    public CircuitPersonnaliseService(CircuitPersonnaliseRepository circuitRepository,
                                      CircuitPersonnaliseJourRepository jourRepository,
                                      ZoneRepository zoneRepository,
                                      VilleRepository villeRepository,
                                      ActiviteRepository activiteRepository,
                                      HebergementRepository hebergementRepository,
                                      ReservationHebergementService reservationHebergementService,
                                      TarifsCircuitPersonnaliseService tarifsCircuitPersonnaliseService,
                                      AuthenticatedUserService authenticatedUserService) {
        this.circuitRepository = circuitRepository;
        this.jourRepository = jourRepository;
        this.zoneRepository = zoneRepository;
        this.villeRepository = villeRepository;
        this.activiteRepository = activiteRepository;
        this.hebergementRepository = hebergementRepository;
        this.reservationHebergementService = reservationHebergementService;
        this.tarifsCircuitPersonnaliseService = tarifsCircuitPersonnaliseService;
        this.authenticatedUserService = authenticatedUserService;
    }

    public CircuitPersonnaliseDTO create(CircuitPersonnaliseDTO dto) {
        Utilisateur currentUser = authenticatedUserService.getCurrentUserOrNull();
        CircuitPersonnalise entity = new CircuitPersonnalise();

        entity.setUtilisateur(currentUser);
        entity.setNomClient(resolveRequiredText(dto.getNomClient(), currentUser != null ? currentUser.getNom() : null,
                "Le nom du client est obligatoire."));
        entity.setPrenomClient(resolveRequiredText(dto.getPrenomClient(), currentUser != null ? currentUser.getPrenom() : null,
                "Le prenom du client est obligatoire."));
        entity.setEmailClient(currentUser == null
                ? resolveRequiredText(dto.getEmailClient(), null, "L'email du client est obligatoire.")
                : resolveRequiredText(currentUser.getEmail(), dto.getEmailClient(), "L'email du compte est obligatoire."));
        entity.setTelephoneClient(resolveRequiredText(dto.getTelephoneClient(), currentUser != null ? currentUser.getTelephone() : null,
                "Le telephone du client est obligatoire."));
        entity.setMessageClient(blankToNull(dto.getMessageClient()));
        entity.setNombreJours(dto.getNombreJours());
        entity.setNombrePersonnes(dto.getNombrePersonnes());
        entity.setDateVoyageSouhaitee(dto.getDateVoyageSouhaitee());
        entity.setAvecHebergement(dto.isAvecHebergement() || dto.getHebergementId() != null);
        entity.setAvecTransport(dto.isAvecTransport());
        entity.setTypeTransport(dto.getTypeTransport());
        entity.setAvecGuide(dto.isAvecGuide());
        entity.setAvecChauffeur(dto.isAvecChauffeur());
        entity.setPensionComplete(dto.isPensionComplete());
        entity.setStatut(CircuitPersonnalise.StatutDemande.EN_ATTENTE);
        entity.setDevisePrixEstime("EUR");

        applyHebergementSelection(entity, dto);

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

        entity = circuitRepository.findById(entity.getId()).orElseThrow();
        applyPricingBreakdown(entity);
        entity = circuitRepository.save(entity);
        entity = ensureReference(entity);

        return toDTO(entity);
    }

    public List<CircuitPersonnaliseDTO> getAll() {
        return circuitRepository.findAllByOrderByDateCreationDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CircuitPersonnaliseDTO> getMine() {
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        return circuitRepository.findByUtilisateurIdOrEmailClientIgnoreCaseOrderByDateCreationDesc(currentUser.getId(), currentUser.getEmail())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CircuitPersonnaliseDTO getById(Long id) {
        CircuitPersonnalise entity = circuitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Circuit personnalise non trouve: " + id));
        return toDTO(entity);
    }

    public CircuitPersonnaliseDTO getMineById(Long id) {
        return toDTO(getOwnedDemande(id));
    }

    public List<CircuitPersonnaliseDTO> getByStatut(String statut) {
        CircuitPersonnalise.StatutDemande statutEnum = CircuitPersonnalise.StatutDemande.valueOf(statut);
        return circuitRepository.findByStatutOrderByDateCreationDesc(statutEnum)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CircuitPersonnaliseDTO updateStatut(Long id,
                                               String nouveauStatut,
                                               BigDecimal prixFinal,
                                               String commentaireAdmin,
                                               String motifRefus) {
        CircuitPersonnalise entity = circuitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Circuit personnalise non trouve: " + id));

        CircuitPersonnalise.StatutDemande statutEnum = CircuitPersonnalise.StatutDemande.valueOf(nouveauStatut);
        entity.setStatut(statutEnum);
        entity.setDateTraitement(LocalDate.now());
        entity.setCommentaireAdmin(blankToNull(commentaireAdmin));

        if (prixFinal != null) {
            entity.setPrixFinal(prixFinal);
        }

        if (statutEnum == CircuitPersonnalise.StatutDemande.ACCEPTE) {
            BigDecimal montantFinal = entity.getPrixFinal();
            if ((montantFinal == null || montantFinal.signum() <= 0)
                    && entity.getPrixEstime() != null
                    && entity.getPrixEstime().signum() > 0) {
                montantFinal = entity.getPrixEstime();
            }
            if (montantFinal == null || montantFinal.signum() <= 0) {
                throw new BadRequestException("Un prix final valide est requis pour valider ce devis.");
            }
            entity.setPrixFinal(montantFinal);
            entity.setMotifRefus(null);
        } else if (statutEnum == CircuitPersonnalise.StatutDemande.REFUSE) {
            entity.setMotifRefus(blankToNull(motifRefus));
        } else if (motifRefus != null) {
            entity.setMotifRefus(blankToNull(motifRefus));
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

    private void applyHebergementSelection(CircuitPersonnalise entity, CircuitPersonnaliseDTO dto) {
        if (dto.getHebergementId() == null) {
            if (dto.getDateArriveeHebergement() != null || dto.getDateDepartHebergement() != null) {
                throw new BadRequestException("Selectionnez un hebergement avant de renseigner les dates de sejour.");
            }
            entity.setHebergement(null);
            entity.setDateArriveeHebergement(null);
            entity.setDateDepartHebergement(null);
            entity.setTypeHebergement(dto.getTypeHebergement());
            return;
        }

        validateHebergementDates(dto.getDateArriveeHebergement(), dto.getDateDepartHebergement());

        Hebergement hebergement = hebergementRepository.findById(dto.getHebergementId())
                .orElseThrow(() -> new ResourceNotFoundException("Hebergement non trouve: " + dto.getHebergementId()));

        boolean disponible = reservationHebergementService.isAvailable(
                hebergement.getIdHebergement(),
                dto.getDateArriveeHebergement(),
                dto.getDateDepartHebergement()
        );
        if (!disponible) {
            throw new BadRequestException("L'hebergement selectionne n'est pas disponible pour ces dates.");
        }

        entity.setHebergement(hebergement);
        entity.setDateArriveeHebergement(dto.getDateArriveeHebergement());
        entity.setDateDepartHebergement(dto.getDateDepartHebergement());
        entity.setTypeHebergement(hebergement.getNom());
    }

    private void validateHebergementDates(LocalDate dateArrivee, LocalDate dateDepart) {
        if (dateArrivee == null || dateDepart == null) {
            throw new BadRequestException("Les dates d'arrivee et de depart sont obligatoires pour l'hebergement choisi.");
        }
        if (dateArrivee.isBefore(LocalDate.now())) {
            throw new BadRequestException("La date d'arrivee de l'hebergement ne peut pas etre dans le passe.");
        }
        if (!dateDepart.isAfter(dateArrivee)) {
            throw new BadRequestException("La date de depart de l'hebergement doit etre apres la date d'arrivee.");
        }
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

    private void applyPricingBreakdown(CircuitPersonnalise entity) {
        TarifsCircuitPersonnaliseDTO tarifs = tarifsCircuitPersonnaliseService.getCurrent();

        entity.setPrixActivitesEstime(calculateActivitiesPrice(entity));
        entity.setPrixHebergementEstime(calculateHebergementPrice(entity));
        entity.setPrixTransportEstime(calculateTransportPrice(entity, tarifs));
        entity.setPrixGuideEstime(calculateGuidePrice(entity, tarifs));
        entity.setPrixChauffeurEstime(calculateChauffeurPrice(entity, tarifs));
        entity.setPrixPensionCompleteEstime(calculatePensionPrice(entity, tarifs));
        entity.setDevisePrixEstime(normalizeCurrency(tarifs.getDevise()));
        entity.setPrixEstime(entity.calculerPrixEstime());
    }

    private BigDecimal calculateActivitiesPrice(CircuitPersonnalise entity) {
        BigDecimal total = BigDecimal.ZERO;

        for (CircuitPersonnaliseJour jour : entity.getJours()) {
            if (jour.getActivites() == null) {
                continue;
            }
            for (Activite activite : jour.getActivites()) {
                if (activite.getPoids() != null) {
                    total = total.add(BigDecimal.valueOf(activite.getPoids()));
                }
            }
        }

        return total;
    }

    private BigDecimal calculateHebergementPrice(CircuitPersonnalise entity) {
        long nombreNuits = entity.getNombreNuitsHebergement();
        if (!entity.isAvecHebergement() || entity.getHebergement() == null || nombreNuits <= 0) {
            return BigDecimal.ZERO;
        }

        return entity.getHebergement().getPrixParNuit()
                .multiply(BigDecimal.valueOf(nombreNuits));
    }

    private BigDecimal calculateTransportPrice(CircuitPersonnalise entity, TarifsCircuitPersonnaliseDTO tarifs) {
        if (!entity.isAvecTransport() || entity.getTypeTransport() == null || entity.getTypeTransport().isBlank()) {
            return BigDecimal.ZERO;
        }

        BigDecimal dailyRate = resolveTransportRate(entity.getTypeTransport(), tarifs);
        return dailyRate.multiply(BigDecimal.valueOf(resolveBillableDays(entity)));
    }

    private BigDecimal calculateGuidePrice(CircuitPersonnalise entity, TarifsCircuitPersonnaliseDTO tarifs) {
        if (!entity.isAvecGuide()) {
            return BigDecimal.ZERO;
        }

        return defaultAmount(tarifs.getGuideParJour())
                .multiply(BigDecimal.valueOf(resolveBillableDays(entity)));
    }

    private BigDecimal calculateChauffeurPrice(CircuitPersonnalise entity, TarifsCircuitPersonnaliseDTO tarifs) {
        if (!entity.isAvecChauffeur()) {
            return BigDecimal.ZERO;
        }

        return defaultAmount(tarifs.getChauffeurParJour())
                .multiply(BigDecimal.valueOf(resolveBillableDays(entity)));
    }

    private BigDecimal calculatePensionPrice(CircuitPersonnalise entity, TarifsCircuitPersonnaliseDTO tarifs) {
        if (!entity.isPensionComplete()) {
            return BigDecimal.ZERO;
        }

        return defaultAmount(tarifs.getPensionCompleteParPersonneParJour())
                .multiply(BigDecimal.valueOf(Math.max(entity.getNombrePersonnes(), 0L)))
                .multiply(BigDecimal.valueOf(resolveBillableDays(entity)));
    }

    private BigDecimal resolveTransportRate(String transportType, TarifsCircuitPersonnaliseDTO tarifs) {
        String normalized = transportType.toLowerCase(Locale.ROOT);

        if (normalized.contains("compact")) {
            return defaultAmount(tarifs.getTransportCompactParJour());
        }
        if (normalized.contains("famil")) {
            return defaultAmount(tarifs.getTransportFamilialParJour());
        }
        if (normalized.contains("minibus")) {
            return defaultAmount(tarifs.getTransportMinibusParJour());
        }
        if (normalized.contains("bus")) {
            return defaultAmount(tarifs.getTransportBusParJour());
        }

        return BigDecimal.ZERO;
    }

    private int resolveBillableDays(CircuitPersonnalise entity) {
        if (entity.getNombreJours() > 0) {
            return entity.getNombreJours();
        }
        return Math.max(entity.getJours().size(), 0);
    }

    private BigDecimal defaultAmount(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String normalizeCurrency(String devise) {
        if (devise == null || devise.isBlank()) {
            return "EUR";
        }
        return devise.trim().toUpperCase(Locale.ROOT);
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
        dto.setPrixActivitesEstime(entity.getPrixActivitesEstime());
        dto.setPrixHebergementEstime(entity.getPrixHebergementEstime());
        dto.setPrixTransportEstime(entity.getPrixTransportEstime());
        dto.setPrixGuideEstime(entity.getPrixGuideEstime());
        dto.setPrixChauffeurEstime(entity.getPrixChauffeurEstime());
        dto.setPrixPensionCompleteEstime(entity.getPrixPensionCompleteEstime());
        dto.setPrixEstime(entity.getPrixEstime());
        dto.setDevisePrixEstime(entity.getDevisePrixEstime() != null ? entity.getDevisePrixEstime() : "EUR");
        dto.setPrixFinal(entity.getPrixFinal());
        dto.setReferenceReservation(resolveReference(entity));
        dto.setStatut(entity.getStatut().name());
        dto.setCommentaireAdmin(entity.getCommentaireAdmin());
        dto.setDateTraitement(entity.getDateTraitement());
        dto.setMotifRefus(entity.getMotifRefus());
        if (entity.getUtilisateur() != null) {
            dto.setUtilisateurId(entity.getUtilisateur().getId());
        }

        PaiementCircuitPersonnalise paiement = entity.getPaiement();
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

        if (entity.getHebergement() != null) {
            dto.setHebergementId(entity.getHebergement().getIdHebergement());
            dto.setHebergementNom(entity.getHebergement().getNom());
            dto.setHebergementLocalisation(entity.getHebergement().getLocalisation());
            dto.setHebergementPrixParNuit(entity.getHebergement().getPrixParNuit());
        }
        dto.setDateArriveeHebergement(entity.getDateArriveeHebergement());
        dto.setDateDepartHebergement(entity.getDateDepartHebergement());
        dto.setNombreNuitsHebergement(entity.getNombreNuitsHebergement());

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

    private CircuitPersonnalise ensureReference(CircuitPersonnalise entity) {
        if (entity.getId() == null) {
            return entity;
        }
        if (StringUtils.hasText(entity.getReferenceReservation())) {
            return entity;
        }
        entity.setReferenceReservation(buildReference(entity.getId()));
        return circuitRepository.save(entity);
    }

    private String resolveReference(CircuitPersonnalise entity) {
        if (StringUtils.hasText(entity.getReferenceReservation())) {
            return entity.getReferenceReservation().trim();
        }
        if (entity.getId() == null) {
            return null;
        }
        return buildReference(entity.getId());
    }

    private String buildReference(Long id) {
        return "CPS-" + String.format("%06d", id);
    }

    private String normalizePaymentStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return "A_PAYER";
        }
        return status.trim().toUpperCase();
    }

    private CircuitPersonnalise getOwnedDemande(Long id) {
        Utilisateur currentUser = authenticatedUserService.getRequiredCurrentUser();
        CircuitPersonnalise demande = circuitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devis personnalise introuvable."));

        boolean ownedByUserId = demande.getUtilisateur() != null
                && demande.getUtilisateur().getId() != null
                && demande.getUtilisateur().getId().equals(currentUser.getId());
        boolean ownedByEmail = StringUtils.hasText(demande.getEmailClient())
                && demande.getEmailClient().trim().equalsIgnoreCase(currentUser.getEmail());
        if (!ownedByUserId && !ownedByEmail) {
            throw new ResourceNotFoundException("Devis personnalise introuvable pour ce compte.");
        }
        return demande;
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

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
