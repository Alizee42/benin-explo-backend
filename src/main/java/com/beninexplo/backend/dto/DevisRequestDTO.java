package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class DevisRequestDTO {

    private Long utilisateurId; // optionnel : null si visiteur non connecté

    private String formule;

    private Integer dureeCircuit;

    private String dateDebutCircuit; // format yyyy-MM-dd

    private Integer nbAdultes;
    private Integer nbEnfants;
}
