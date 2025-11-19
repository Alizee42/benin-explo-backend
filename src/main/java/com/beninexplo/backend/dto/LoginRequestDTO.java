package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {

    private String email;
    private String motDePasse;
}
