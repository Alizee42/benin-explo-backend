package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class TombolaParticipantDTO {

    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    private String dateParticipation;
    private String statut;
    private String codeUnique;
}
