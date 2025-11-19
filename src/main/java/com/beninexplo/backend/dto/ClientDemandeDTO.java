package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class ClientDemandeDTO {

    private Long id;

    private Long devisId;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    private String message;
}
