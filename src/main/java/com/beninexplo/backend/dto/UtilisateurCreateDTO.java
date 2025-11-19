package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class UtilisateurCreateDTO {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String role;
    private String motDePasse; // mot de passe en clair (sera hashé)
}
