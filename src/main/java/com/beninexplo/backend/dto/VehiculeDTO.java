package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class VehiculeDTO {

    private Long id;
    private String modele;
    private String type;
    private String prixParJour;

    private boolean actif;

    private Long zoneId;
}
