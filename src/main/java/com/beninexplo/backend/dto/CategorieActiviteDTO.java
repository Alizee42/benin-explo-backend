package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CategorieActiviteDTO {

    private Long id;

    @NotBlank(message = "Le nom de la categorie est obligatoire.")
    @Size(max = 120, message = "Le nom de la categorie ne doit pas depasser 120 caracteres.")
    private String nom;

    @Size(max = 1000, message = "La description de la categorie ne doit pas depasser 1000 caracteres.")
    private String description;

    public CategorieActiviteDTO() {}

    public CategorieActiviteDTO(Long id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    // GETTERS & SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

}
