package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class ReservationResponseDTO {

    private Long id;

    private Long utilisateurId;
    private Long devisId;

    private String dateDebut;
    private String dateFin;

    private String montantTotal;

    private String statut;
    private String dateCreation;
}
