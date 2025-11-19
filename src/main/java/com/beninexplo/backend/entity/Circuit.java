package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Circuit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCircuit;

    private String nom;

    @Column(length = 5000)
    private String description;

    private String dureeIndicative;       // ex: "3 jours" ou "1 semaine"
    private BigDecimal prixIndicatif;     // prix de base du circuit

    private String formuleProposee;       // "circuit", "tout compris", etc.
    private String niveau;                // aventure, détente, découverte…
    
    private boolean actif = true;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @ManyToOne
    @JoinColumn(name = "image_principale_id")
    private Media imagePrincipale;
}
