package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TombolaParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParticipant;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    private LocalDateTime dateParticipation = LocalDateTime.now();

    private String statut = "valide";  
    private String codeUnique;         // généré automatiquement
}
