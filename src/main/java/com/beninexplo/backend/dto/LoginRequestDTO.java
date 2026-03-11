package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LoginRequestDTO {

    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "L'email doit etre valide.")
    @Size(max = 150, message = "L'email ne doit pas depasser 150 caracteres.")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(max = 255, message = "Le mot de passe ne doit pas depasser 255 caracteres.")
    private String motDePasse;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String email, String motDePasse) {
        this.email = email;
        this.motDePasse = motDePasse;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}
