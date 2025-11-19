package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevisActivite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDevisActivite;

    @ManyToOne
    @JoinColumn(name = "devis_id")
    private Devis devis;

    @ManyToOne
    @JoinColumn(name = "activite_id")
    private Activite activite;

    private Integer ordre; // ordre du programme
}
