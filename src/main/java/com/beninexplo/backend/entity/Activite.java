package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idActivite;

    private String nom;

    @Column(length = 5000)
    private String description;

    private String ville;

    private Integer dureeInterne;          // durée indicative interne
    private Integer distanceDepuisCotonou; // km
    private Integer poids;                 // 1 proche / 2 moyen / 3 loin
    private String difficulte;

    private boolean actif = true;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private CategorieActivite categorie;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @ManyToOne
    @JoinColumn(name = "image_principale_id")
    private Media imagePrincipale;
}
