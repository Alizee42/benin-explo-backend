package com.beninexplo.backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "circuits_personnalises")
public class CircuitPersonnalise extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Informations client
    private String nomClient;
    private String prenomClient;
    private String emailClient;
    private String telephoneClient;

    @Column(length = 5000)
    private String messageClient;

    // Parametres du circuit
    private int nombreJours;
    private int nombrePersonnes;
    private LocalDate dateCreation;
    private LocalDate dateVoyageSouhaitee;

    // Options
    private boolean avecHebergement;
    private String typeHebergement;

    @ManyToOne
    @JoinColumn(name = "hebergement_id")
    private Hebergement hebergement;

    private LocalDate dateArriveeHebergement;
    private LocalDate dateDepartHebergement;
    private boolean avecTransport;
    private String typeTransport;
    private boolean avecGuide;
    private boolean avecChauffeur;
    private boolean pensionComplete;

    // Prix
    private BigDecimal prixActivitesEstime;
    private BigDecimal prixHebergementEstime;
    private BigDecimal prixTransportEstime;
    private BigDecimal prixGuideEstime;
    private BigDecimal prixChauffeurEstime;
    private BigDecimal prixPensionCompleteEstime;
    private BigDecimal prixEstime;
    private String devisePrixEstime;
    private BigDecimal prixFinal;

    // Statut du traitement
    @Enumerated(EnumType.STRING)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    // Relation avec les jours du circuit
    @OneToMany(mappedBy = "circuitPersonnalise", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numeroJour ASC")
    private List<CircuitPersonnaliseJour> jours = new ArrayList<>();

    // Si accepte, peut etre lie a un circuit cree par l'admin
    @OneToOne
    @JoinColumn(name = "circuit_cree_id")
    private Circuit circuitCree;

    public enum StatutDemande {
        EN_ATTENTE,
        EN_TRAITEMENT,
        ACCEPTE,
        REFUSE,
        TERMINE
    }

    public CircuitPersonnalise() {
        this.dateCreation = LocalDate.now();
    }

    public void addJour(CircuitPersonnaliseJour jour) {
        jours.add(jour);
        jour.setCircuitPersonnalise(this);
    }

    public void removeJour(CircuitPersonnaliseJour jour) {
        jours.remove(jour);
        jour.setCircuitPersonnalise(null);
    }

    public BigDecimal calculerPrixEstime() {
        return defaultAmount(prixActivitesEstime)
                .add(defaultAmount(prixHebergementEstime))
                .add(defaultAmount(prixTransportEstime))
                .add(defaultAmount(prixGuideEstime))
                .add(defaultAmount(prixChauffeurEstime))
                .add(defaultAmount(prixPensionCompleteEstime));
    }

    @PrePersist
    @PreUpdate
    private void refreshComputedFields() {
        if (dateCreation == null) {
            dateCreation = LocalDate.now();
        }
        prixActivitesEstime = defaultAmount(prixActivitesEstime);
        prixHebergementEstime = defaultAmount(prixHebergementEstime);
        prixTransportEstime = defaultAmount(prixTransportEstime);
        prixGuideEstime = defaultAmount(prixGuideEstime);
        prixChauffeurEstime = defaultAmount(prixChauffeurEstime);
        prixPensionCompleteEstime = defaultAmount(prixPensionCompleteEstime);
        if (devisePrixEstime == null || devisePrixEstime.isBlank()) {
            devisePrixEstime = "EUR";
        }
        prixEstime = calculerPrixEstime();
    }

    private BigDecimal defaultAmount(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    public int getNombreNuitsHebergement() {
        if (dateArriveeHebergement == null || dateDepartHebergement == null || !dateDepartHebergement.isAfter(dateArriveeHebergement)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(dateArriveeHebergement, dateDepartHebergement);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getPrenomClient() {
        return prenomClient;
    }

    public void setPrenomClient(String prenomClient) {
        this.prenomClient = prenomClient;
    }

    public String getEmailClient() {
        return emailClient;
    }

    public void setEmailClient(String emailClient) {
        this.emailClient = emailClient;
    }

    public String getTelephoneClient() {
        return telephoneClient;
    }

    public void setTelephoneClient(String telephoneClient) {
        this.telephoneClient = telephoneClient;
    }

    public String getMessageClient() {
        return messageClient;
    }

    public void setMessageClient(String messageClient) {
        this.messageClient = messageClient;
    }

    public int getNombreJours() {
        return nombreJours;
    }

    public void setNombreJours(int nombreJours) {
        this.nombreJours = nombreJours;
    }

    public int getNombrePersonnes() {
        return nombrePersonnes;
    }

    public void setNombrePersonnes(int nombrePersonnes) {
        this.nombrePersonnes = nombrePersonnes;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDate getDateVoyageSouhaitee() {
        return dateVoyageSouhaitee;
    }

    public void setDateVoyageSouhaitee(LocalDate dateVoyageSouhaitee) {
        this.dateVoyageSouhaitee = dateVoyageSouhaitee;
    }

    public boolean isAvecHebergement() {
        return avecHebergement;
    }

    public void setAvecHebergement(boolean avecHebergement) {
        this.avecHebergement = avecHebergement;
    }

    public String getTypeHebergement() {
        return typeHebergement;
    }

    public void setTypeHebergement(String typeHebergement) {
        this.typeHebergement = typeHebergement;
    }

    public Hebergement getHebergement() {
        return hebergement;
    }

    public void setHebergement(Hebergement hebergement) {
        this.hebergement = hebergement;
    }

    public LocalDate getDateArriveeHebergement() {
        return dateArriveeHebergement;
    }

    public void setDateArriveeHebergement(LocalDate dateArriveeHebergement) {
        this.dateArriveeHebergement = dateArriveeHebergement;
    }

    public LocalDate getDateDepartHebergement() {
        return dateDepartHebergement;
    }

    public void setDateDepartHebergement(LocalDate dateDepartHebergement) {
        this.dateDepartHebergement = dateDepartHebergement;
    }

    public boolean isAvecTransport() {
        return avecTransport;
    }

    public void setAvecTransport(boolean avecTransport) {
        this.avecTransport = avecTransport;
    }

    public String getTypeTransport() {
        return typeTransport;
    }

    public void setTypeTransport(String typeTransport) {
        this.typeTransport = typeTransport;
    }

    public boolean isAvecGuide() {
        return avecGuide;
    }

    public void setAvecGuide(boolean avecGuide) {
        this.avecGuide = avecGuide;
    }

    public boolean isAvecChauffeur() {
        return avecChauffeur;
    }

    public void setAvecChauffeur(boolean avecChauffeur) {
        this.avecChauffeur = avecChauffeur;
    }

    public boolean isPensionComplete() {
        return pensionComplete;
    }

    public void setPensionComplete(boolean pensionComplete) {
        this.pensionComplete = pensionComplete;
    }

    public BigDecimal getPrixEstime() {
        return prixEstime;
    }

    public void setPrixEstime(BigDecimal prixEstime) {
        this.prixEstime = prixEstime;
    }

    public BigDecimal getPrixActivitesEstime() {
        return prixActivitesEstime;
    }

    public void setPrixActivitesEstime(BigDecimal prixActivitesEstime) {
        this.prixActivitesEstime = prixActivitesEstime;
    }

    public BigDecimal getPrixHebergementEstime() {
        return prixHebergementEstime;
    }

    public void setPrixHebergementEstime(BigDecimal prixHebergementEstime) {
        this.prixHebergementEstime = prixHebergementEstime;
    }

    public BigDecimal getPrixTransportEstime() {
        return prixTransportEstime;
    }

    public void setPrixTransportEstime(BigDecimal prixTransportEstime) {
        this.prixTransportEstime = prixTransportEstime;
    }

    public BigDecimal getPrixGuideEstime() {
        return prixGuideEstime;
    }

    public void setPrixGuideEstime(BigDecimal prixGuideEstime) {
        this.prixGuideEstime = prixGuideEstime;
    }

    public BigDecimal getPrixChauffeurEstime() {
        return prixChauffeurEstime;
    }

    public void setPrixChauffeurEstime(BigDecimal prixChauffeurEstime) {
        this.prixChauffeurEstime = prixChauffeurEstime;
    }

    public BigDecimal getPrixPensionCompleteEstime() {
        return prixPensionCompleteEstime;
    }

    public void setPrixPensionCompleteEstime(BigDecimal prixPensionCompleteEstime) {
        this.prixPensionCompleteEstime = prixPensionCompleteEstime;
    }

    public String getDevisePrixEstime() {
        return devisePrixEstime;
    }

    public void setDevisePrixEstime(String devisePrixEstime) {
        this.devisePrixEstime = devisePrixEstime;
    }

    public BigDecimal getPrixFinal() {
        return prixFinal;
    }

    public void setPrixFinal(BigDecimal prixFinal) {
        this.prixFinal = prixFinal;
    }

    public StatutDemande getStatut() {
        return statut;
    }

    public void setStatut(StatutDemande statut) {
        this.statut = statut;
    }

    public List<CircuitPersonnaliseJour> getJours() {
        return jours;
    }

    public void setJours(List<CircuitPersonnaliseJour> jours) {
        this.jours = jours;
    }

    public Circuit getCircuitCree() {
        return circuitCree;
    }

    public void setCircuitCree(Circuit circuitCree) {
        this.circuitCree = circuitCree;
    }
}
