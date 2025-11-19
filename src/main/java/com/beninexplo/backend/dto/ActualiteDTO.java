package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class ActualiteDTO {

    private Long id;

    private String titre;
    private String contenu;
    private String datePublication;

    private Long imagePrincipaleId;
    private Long auteurId;
}
