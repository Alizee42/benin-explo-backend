package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hebergement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHebergement;

    private String nom;
    private String type; // hôtel, lodge, auberge…
    
    @Column(length = 2000)
    private String description;

    private BigDecimal prixParNuit;

    private boolean actif = true;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;
}
