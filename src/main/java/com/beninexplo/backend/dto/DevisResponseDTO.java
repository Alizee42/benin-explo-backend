package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class DevisResponseDTO {

    private Long id;

    private String formule;
    private Integer dureeCircuit;

    private String dateDebutCircuit;
    private String dateFinCircuit;

    private Integer nbAdultes;
    private Integer nbEnfants;
    private Integer nbParticipants;

    private String statut;
    private String dateDemande;

    private Long utilisateurId;
}
