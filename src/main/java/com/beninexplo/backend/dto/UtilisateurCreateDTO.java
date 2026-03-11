package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UtilisateurCreateDTO {

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres.")
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire.")
    @Size(max = 100, message = "Le prenom ne doit pas depasser 100 caracteres.")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "L'email doit etre valide.")
    @Size(max = 150, message = "L'email ne doit pas depasser 150 caracteres.")
    private String email;

    @Pattern(regexp = "^$|^[0-9+()\\-\\s]{8,20}$", message = "Le telephone doit contenir entre 8 et 20 caracteres valides.")
    private String telephone;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 8, max = 255, message = "Le mot de passe doit contenir entre 8 et 255 caracteres.")
    private String motDePasse;

    public UtilisateurCreateDTO() {
    }

    public UtilisateurCreateDTO(String nom, String prenom, String email,
                                String telephone, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}
