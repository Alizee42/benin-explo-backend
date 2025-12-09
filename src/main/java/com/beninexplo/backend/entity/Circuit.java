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

    // Court résumé (affiché comme chapeau)
    @Column(length = 2000)
    private String resume;

    private String dureeIndicative;
    private BigDecimal prixIndicatif;

    private String formuleProposee;

    private boolean actif = true;

    @ManyToOne
    @JoinColumn(name = "ville_id")
    private Ville ville;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @ManyToOne
    @JoinColumn(name = "image_principale_id")
    private Media imagePrincipale;

    // Image principale (base64 ou URL) stockée en TEXT
    @Column(columnDefinition = "text")
    private String img;

    // Galerie d'images stockée en JSON (TEXT)
    @Column(columnDefinition = "text")
    private String galerie;

    // Programme jour par jour stocké en JSON (TEXT)
    @Column(columnDefinition = "text")
    private String programme;

    // Points forts (liste d'objets) stockée en JSON (TEXT)
    @Column(columnDefinition = "text")
    private String pointsForts;

    // Inclus / non inclus stockés en JSON
    @Column(columnDefinition = "text")
    private String inclus;

    @Column(columnDefinition = "text")
    private String nonInclus;

    // Sections tourisme / aventures (listes de chaînes)
    @Column(columnDefinition = "text")
    private String tourisme;

    @Column(columnDefinition = "text")
    private String aventures;

    // -----------------------------------------
    // CONSTRUCTEURS
    // -----------------------------------------
    public Circuit() {}

    public Circuit(Long idCircuit, String nom, String description, String dureeIndicative,
                   BigDecimal prixIndicatif, String formuleProposee,
                   boolean actif, Zone zone, Media imagePrincipale) {
        this.idCircuit = idCircuit;
        this.nom = nom;
        this.description = description;
        this.dureeIndicative = dureeIndicative;
        this.prixIndicatif = prixIndicatif;
        this.formuleProposee = formuleProposee;
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

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public Ville getVille() { return ville; }
    public void setVille(Ville ville) { this.ville = ville; }

    public Zone getZone() { return zone; }
    public void setZone(Zone zone) { this.zone = zone; }

    public Media getImagePrincipale() { return imagePrincipale; }
    public void setImagePrincipale(Media imagePrincipale) { this.imagePrincipale = imagePrincipale; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
    public String getGalerie() { return galerie; }
    public void setGalerie(String galerie) { this.galerie = galerie; }

    public String getProgramme() { return programme; }
    public void setProgramme(String programme) { this.programme = programme; }

    public String getPointsForts() { return pointsForts; }
    public void setPointsForts(String pointsForts) { this.pointsForts = pointsForts; }

    public String getInclus() { return inclus; }
    public void setInclus(String inclus) { this.inclus = inclus; }

    public String getNonInclus() { return nonInclus; }
    public void setNonInclus(String nonInclus) { this.nonInclus = nonInclus; }

    public String getTourisme() { return tourisme; }
    public void setTourisme(String tourisme) { this.tourisme = tourisme; }

    public String getAventures() { return aventures; }
    public void setAventures(String aventures) { this.aventures = aventures; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }
}
