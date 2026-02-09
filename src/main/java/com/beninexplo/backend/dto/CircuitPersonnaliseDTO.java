package com.beninexplo.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CircuitPersonnaliseDTO {

    private Long id;
    
    // Client
    private String nomClient;
    private String prenomClient;
    private String emailClient;
    private String telephoneClient;
    private String messageClient;
    
    // Paramètres
    private int nombreJours;
    private int nombrePersonnes;
    private LocalDate dateCreation;
    private LocalDate dateVoyageSouhaitee;
    
    // Options
    private boolean avecHebergement;
    private String typeHebergement;
    private boolean avecTransport;
    private String typeTransport;
    private boolean avecGuide;
    private boolean avecChauffeur;
    private boolean pensionComplete;
    
    // Prix
    private BigDecimal prixEstime;
    private BigDecimal prixFinal;
    
    // Statut
    private String statut;
    
    // Jours du circuit
    private List<JourDTO> jours = new ArrayList<>();
    
    // Circuit créé si accepté
    private Long circuitCreeId;
    
    // ----------------------------------------------------
    // CLASSE INTERNE JOUR DTO
    // ----------------------------------------------------
    public static class JourDTO {
        private Long id;
        private int numeroJour;
        private Long zoneId;
        private String zoneNom;
        private Long villeId;
        private String villeNom;
        private List<Long> activiteIds = new ArrayList<>();
        private List<String> activiteNoms = new ArrayList<>();
        private String descriptionJour;
        
        // Constructeurs
        public JourDTO() {}
        
        public JourDTO(Long id, int numeroJour, Long zoneId, String zoneNom, 
                      Long villeId, String villeNom, String descriptionJour) {
            this.id = id;
            this.numeroJour = numeroJour;
            this.zoneId = zoneId;
            this.zoneNom = zoneNom;
            this.villeId = villeId;
            this.villeNom = villeNom;
            this.descriptionJour = descriptionJour;
        }
        
        // Getters/Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public int getNumeroJour() { return numeroJour; }
        public void setNumeroJour(int numeroJour) { this.numeroJour = numeroJour; }
        
        public Long getZoneId() { return zoneId; }
        public void setZoneId(Long zoneId) { this.zoneId = zoneId; }
        
        public String getZoneNom() { return zoneNom; }
        public void setZoneNom(String zoneNom) { this.zoneNom = zoneNom; }
        
        public Long getVilleId() { return villeId; }
        public void setVilleId(Long villeId) { this.villeId = villeId; }
        
        public String getVilleNom() { return villeNom; }
        public void setVilleNom(String villeNom) { this.villeNom = villeNom; }
        
        public List<Long> getActiviteIds() { return activiteIds; }
        public void setActiviteIds(List<Long> activiteIds) { this.activiteIds = activiteIds; }
        
        public List<String> getActiviteNoms() { return activiteNoms; }
        public void setActiviteNoms(List<String> activiteNoms) { this.activiteNoms = activiteNoms; }
        
        public String getDescriptionJour() { return descriptionJour; }
        public void setDescriptionJour(String descriptionJour) { this.descriptionJour = descriptionJour; }
    }
    
    // ----------------------------------------------------
    // CONSTRUCTEURS
    // ----------------------------------------------------
    public CircuitPersonnaliseDTO() {}
    
    // ----------------------------------------------------
    // GETTERS / SETTERS
    // ----------------------------------------------------
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }
    
    public String getPrenomClient() { return prenomClient; }
    public void setPrenomClient(String prenomClient) { this.prenomClient = prenomClient; }
    
    public String getEmailClient() { return emailClient; }
    public void setEmailClient(String emailClient) { this.emailClient = emailClient; }
    
    public String getTelephoneClient() { return telephoneClient; }
    public void setTelephoneClient(String telephoneClient) { this.telephoneClient = telephoneClient; }
    
    public String getMessageClient() { return messageClient; }
    public void setMessageClient(String messageClient) { this.messageClient = messageClient; }
    
    public int getNombreJours() { return nombreJours; }
    public void setNombreJours(int nombreJours) { this.nombreJours = nombreJours; }
    
    public int getNombrePersonnes() { return nombrePersonnes; }
    public void setNombrePersonnes(int nombrePersonnes) { this.nombrePersonnes = nombrePersonnes; }
    
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDate getDateVoyageSouhaitee() { return dateVoyageSouhaitee; }
    public void setDateVoyageSouhaitee(LocalDate dateVoyageSouhaitee) { this.dateVoyageSouhaitee = dateVoyageSouhaitee; }
    
    public boolean isAvecHebergement() { return avecHebergement; }
    public void setAvecHebergement(boolean avecHebergement) { this.avecHebergement = avecHebergement; }
    
    public String getTypeHebergement() { return typeHebergement; }
    public void setTypeHebergement(String typeHebergement) { this.typeHebergement = typeHebergement; }
    
    public boolean isAvecTransport() { return avecTransport; }
    public void setAvecTransport(boolean avecTransport) { this.avecTransport = avecTransport; }
    
    public String getTypeTransport() { return typeTransport; }
    public void setTypeTransport(String typeTransport) { this.typeTransport = typeTransport; }
    
    public boolean isAvecGuide() { return avecGuide; }
    public void setAvecGuide(boolean avecGuide) { this.avecGuide = avecGuide; }
    
    public boolean isAvecChauffeur() { return avecChauffeur; }
    public void setAvecChauffeur(boolean avecChauffeur) { this.avecChauffeur = avecChauffeur; }
    
    public boolean isPensionComplete() { return pensionComplete; }
    public void setPensionComplete(boolean pensionComplete) { this.pensionComplete = pensionComplete; }
    
    public BigDecimal getPrixEstime() { return prixEstime; }
    public void setPrixEstime(BigDecimal prixEstime) { this.prixEstime = prixEstime; }
    
    public BigDecimal getPrixFinal() { return prixFinal; }
    public void setPrixFinal(BigDecimal prixFinal) { this.prixFinal = prixFinal; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public List<JourDTO> getJours() { return jours; }
    public void setJours(List<JourDTO> jours) { this.jours = jours; }
    
    public Long getCircuitCreeId() { return circuitCreeId; }
    public void setCircuitCreeId(Long circuitCreeId) { this.circuitCreeId = circuitCreeId; }
}
