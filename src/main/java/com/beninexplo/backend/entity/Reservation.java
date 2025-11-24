package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "devis_id")
    private Devis devis;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private BigDecimal montantTotal;

    private String statut = "en_attente"; // en_attente / confirmé / payé / annulé

    private LocalDateTime dateCreation = LocalDateTime.now();
}
