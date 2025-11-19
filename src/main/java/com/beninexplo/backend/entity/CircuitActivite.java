package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CircuitActivite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCircuitActivite;

    @ManyToOne
    @JoinColumn(name = "circuit_id")
    private Circuit circuit;

    @ManyToOne
    @JoinColumn(name = "activite_id")
    private Activite activite;

    private Integer ordre;          // ordre d'exécution
    private Integer jourIndicatif;  // optionnel : jour du circuit
}
