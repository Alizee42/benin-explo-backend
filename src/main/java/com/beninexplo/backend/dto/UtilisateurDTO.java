package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class UtilisateurDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String email;

    private String telephone;
    private String role;

    private boolean actif;

    // On utilise String pour éviter les problèmes de format JSON,
    // le service convertira LocalDateTime → String ISO
    private String dateCreation;
}
