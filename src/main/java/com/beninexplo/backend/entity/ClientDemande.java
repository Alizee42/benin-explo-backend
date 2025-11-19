package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClientDemande;

    @ManyToOne
    @JoinColumn(name = "devis_id")
    private Devis devis;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    @Column(length = 5000)
    private String message;
}
