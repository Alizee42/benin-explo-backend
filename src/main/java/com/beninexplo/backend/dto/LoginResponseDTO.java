package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {

    private boolean success;
    private String message;
    private UtilisateurDTO utilisateur; // null si échec
    private String token;               // ⭐ AJOUT : le JWT
}
