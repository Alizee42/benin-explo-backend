package com.beninexplo.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CircuitDTO {

    private Long id;
    private String nom;
    private String description;

    private String dureeIndicative;
    private BigDecimal prixIndicatif;
    private String formuleProposee;
    private String niveau;

    private boolean actif;

    private Long zoneId;
    private Long imagePrincipaleId;
}
