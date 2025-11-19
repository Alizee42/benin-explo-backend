package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParametresSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParametres;

    private String nomAgence;

    @Column(length = 5000)
    private String messageAccueil;

    private String emailContact;
    private String telephoneContact;
    private String adresseAgence;

    private String couleurPrimaire;     // ex : "#009900"
    private String couleurSecondaire;   // ex : "#FFCC00"

    private String urlFacebook;
    private String urlInstagram;
}
