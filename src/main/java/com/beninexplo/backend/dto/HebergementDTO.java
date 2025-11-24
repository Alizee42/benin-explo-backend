package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class HebergementDTO {

    private Long id;
    private String nom;
    private String type;
    private String description;
    private String prixParNuit;
    private boolean actif;

    private Long zoneId;
}
