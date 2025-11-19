package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class ParametresSiteDTO {

    private Long id;

    private String nomAgence;
    private String messageAccueil;

    private String emailContact;
    private String telephoneContact;
    private String adresseAgence;

    private String couleurPrimaire;
    private String couleurSecondaire;

    private String urlFacebook;
    private String urlInstagram;
}
