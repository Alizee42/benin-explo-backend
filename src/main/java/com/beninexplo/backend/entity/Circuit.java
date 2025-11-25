package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "circuits")
public class Circuit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCircuit;

    private String nom;

    @Column(length = 5000)
    private String description;

    private String dureeIndicative;
    private BigDecimal prixIndicatif;

    private String formuleProposee;
    private String niveau;

    private boolean actif = true;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @ManyToOne
    @JoinColumn(name = "image_principale_id")
    private Media imagePrincipale;

    // -----------------------------------------
    // CONSTRUCTEURS
    // -----------------------------------------
    public Circuit() {}

    public Circuit(Long idCircuit, String nom, String description, String dureeIndicative,
                   BigDecimal prixIndicatif, String formuleProposee, String niveau,
                   boolean actif, Zone zone, Media imagePrincipale) {
        this.idCircuit = idCircuit;
        this.nom = nom;
        this.description = description;
        this.dureeIndicative = dureeIndicative;
        this.prixIndicatif = prixIndicatif;
        this.formuleProposee = formuleProposee;
        this.niveau = niveau;
        this.actif = actif;
        this.zone = zone;
        this.imagePrincipale = imagePrincipale;
    }

    // -----------------------------------------
    // GETTERS / SETTERS
    // -----------------------------------------
    public Long getIdCircuit() { return idCircuit; }
    public void setIdCircuit(Long idCircuit) { this.idCircuit = idCircuit; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDureeIndicative() { return dureeIndicative; }
    public void setDureeIndicative(String dureeIndicative) { this.dureeIndicative = dureeIndicative; }

    public BigDecimal getPrixIndicatif() { return prixIndicatif; }
    public void setPrixIndicatif(BigDecimal prixIndicatif) { this.prixIndicatif = prixIndicatif; }

    public String getFormuleProposee() { return formuleProposee; }
    public void setFormuleProposee(String formuleProposee) { this.formuleProposee = formuleProposee; }

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public Zone getZone() { return zone; }
    public void setZone(Zone zone) { this.zone = zone; }

    public Media getImagePrincipale() { return imagePrincipale; }
    public void setImagePrincipale(Media imagePrincipale) { this.imagePrincipale = imagePrincipale; }
}
