package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Devis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDevis;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur; // optionnel (client non connecté)

    private String formule; // circuit / circuit+transport / tout compris

    private Integer dureeCircuit;       // en jours
    private LocalDate dateDebutCircuit; 
    private LocalDate dateFinCircuit;   // optionnel (calculé côté backend)

    private Integer nbAdultes;
    private Integer nbEnfants;
    private Integer nbParticipants;      // helpers

    private LocalDateTime dateDemande = LocalDateTime.now();
    private String statut = "en_attente";

    @Column(length = 2000)
    private String commentaireInterne;
}
