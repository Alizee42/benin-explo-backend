package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "circuits_personnalises")
public class CircuitPersonnalise {

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

    // Paramètres du circuit
    private int nombreJours;
    private int nombrePersonnes;
    private LocalDate dateCreation;
    private LocalDate dateVoyageSouhaitee;

    // Options
    private boolean avecHebergement;
    private String typeHebergement; // standard, confort, luxe
    private boolean avecTransport;
    private String typeTransport; // voiture, minibus, etc.
    private boolean avecGuide;
    private boolean avecChauffeur;
    private boolean pensionComplete;

    // Prix
    private BigDecimal prixEstime;
    private BigDecimal prixFinal;

    // Statut du traitement
    @Enumerated(EnumType.STRING)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    // Relation avec les jours du circuit
    @OneToMany(mappedBy = "circuitPersonnalise", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numeroJour ASC")
    private List<CircuitPersonnaliseJour> jours = new ArrayList<>();

    // Si accepté, peut être lié à un circuit créé par l'admin
    @OneToOne
    @JoinColumn(name = "circuit_cree_id")
    private Circuit circuitCree;

    // ----------------------------------------------------
    // ENUM STATUT
    // ----------------------------------------------------
    public enum StatutDemande {
        EN_ATTENTE,    // Nouvelle demande
        EN_TRAITEMENT, // Admin en train de traiter
        ACCEPTE,       // Devis accepté par le client
        REFUSE,        // Refusé ou annulé
        TERMINE        // Circuit effectué
    }

    // ----------------------------------------------------
    // CONSTRUCTEURS
    // ----------------------------------------------------
    public CircuitPersonnalise() {
        this.dateCreation = LocalDate.now();
    }

    // ----------------------------------------------------
    // MÉTHODES UTILITAIRES
    // ----------------------------------------------------

    /**
     * Ajoute un jour au circuit
     */
    public void addJour(CircuitPersonnaliseJour jour) {
        jours.add(jour);
        jour.setCircuitPersonnalise(this);
    }

    /**
     * Supprime un jour du circuit
     */
    public void removeJour(CircuitPersonnaliseJour jour) {
        jours.remove(jour);
        jour.setCircuitPersonnalise(null);
    }

    /**
     * Calcule un prix estimé basé sur les options
     */
    public BigDecimal calculerPrixEstime() {
        BigDecimal base = BigDecimal.valueOf(50000); // 50k XOF par personne par jour
        BigDecimal total = base.multiply(BigDecimal.valueOf(nombrePersonnes))
                               .multiply(BigDecimal.valueOf(nombreJours));

        // Ajustements selon options
        if (avecHebergement) {
            BigDecimal hebergement = switch (typeHebergement != null ? typeHebergement : "standard") {
                case "luxe" -> BigDecimal.valueOf(100000);
                case "confort" -> BigDecimal.valueOf(50000);
                default -> BigDecimal.valueOf(25000);
            };
            total = total.add(hebergement.multiply(BigDecimal.valueOf(nombreJours)));
        }

        if (avecTransport) {
            BigDecimal transport = BigDecimal.valueOf(30000).multiply(BigDecimal.valueOf(nombreJours));
            total = total.add(transport);
        }

        if (avecGuide) {
            total = total.add(BigDecimal.valueOf(25000).multiply(BigDecimal.valueOf(nombreJours)));
        }

        if (pensionComplete) {
            total = total.add(BigDecimal.valueOf(15000).multiply(BigDecimal.valueOf(nombrePersonnes))
                                                       .multiply(BigDecimal.valueOf(nombreJours)));
        }

        return total;
    }

    @PrePersist
    private void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDate.now();
        }
        if (prixEstime == null) {
            prixEstime = calculerPrixEstime();
        }
    }

    // ----------------------------------------------------
    // GETTERS / SETTERS
    // ----------------------------------------------------

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
