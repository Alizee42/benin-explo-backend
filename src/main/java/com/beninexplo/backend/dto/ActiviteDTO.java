package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class ActiviteDTO {

    private Long id;
    private String nom;
    private String description;
    private String ville;

    private Integer dureeInterne;
    private Integer distanceDepuisCotonou;
    private Integer poids;
    private String difficulte;

    private boolean actif;

    private Long categorieId;
    private Long zoneId;
    private Long imagePrincipaleId;
}
