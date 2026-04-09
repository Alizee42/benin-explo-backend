package com.beninexplo.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContactMessageDTO {

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres.")
    private String nom;

    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "L'email doit etre valide.")
    @Size(max = 150, message = "L'email ne doit pas depasser 150 caracteres.")
    private String email;

    @Size(max = 200, message = "Le sujet ne doit pas depasser 200 caracteres.")
    private String sujet;

    @NotBlank(message = "Le message est obligatoire.")
    @Size(max = 3000, message = "Le message ne doit pas depasser 3000 caracteres.")
    private String message;

    public ContactMessageDTO() {}

    public String getNom()     { return nom; }
    public String getEmail()   { return email; }
    public String getSujet()   { return sujet; }
    public String getMessage() { return message; }

    public void setNom(String nom)         { this.nom = nom; }
    public void setEmail(String email)     { this.email = email; }
    public void setSujet(String sujet)     { this.sujet = sujet; }
    public void setMessage(String message) { this.message = message; }
}
