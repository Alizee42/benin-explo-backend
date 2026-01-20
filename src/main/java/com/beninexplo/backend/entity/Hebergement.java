package com.beninexplo.backend.entity;

import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.*;


@Entity
@Table(name = "hebergements")
public class Hebergement {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
    ---------------------------------------------------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHebergement;

    private String nom;
    private String type; // hôtel, maison d'hôte, auberge, lodge...
    private String localisation; // ville
    @Column(nullable = true)
    private String quartier; // quartier dans la ville (optionnel)

    @Column(length = 5000)
    private String description;

    private double prixParNuit;

    @Column(columnDefinition = "TEXT")
    private String imageUrls; // JSON array of image URLs

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */
    public Hebergement() {}

    public Hebergement(Long idHebergement, String nom, String type, String localisation, String quartier,
                       String description, double prixParNuit) {
        this.idHebergement = idHebergement;
        this.nom = nom;
        this.type = type;
        this.localisation = localisation;
        this.quartier = quartier;
        this.description = description;
        this.prixParNuit = prixParNuit;
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */
    public Long getIdHebergement() { return idHebergement; }
    public void setIdHebergement(Long idHebergement) { this.idHebergement = idHebergement; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public String getQuartier() { return quartier; }
    public void setQuartier(String quartier) { this.quartier = quartier; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrixParNuit() { return prixParNuit; }
    public void setPrixParNuit(double prixParNuit) { this.prixParNuit = prixParNuit; }

    public List<String> getImageUrls() {
        if (imageUrls == null || imageUrls.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(imageUrls, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void setImageUrls(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            this.imageUrls = null;
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.imageUrls = mapper.writeValueAsString(imageUrls);
        } catch (Exception e) {
            this.imageUrls = null;
        }
    }
}
