package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ZoneDTO {

    private Long idZone;

    @NotBlank(message = "Le nom de la zone est obligatoire.")
    @Size(max = 120, message = "Le nom de la zone ne doit pas depasser 120 caracteres.")
    private String nom;

    @Size(max = 1000, message = "La description de la zone ne doit pas depasser 1000 caracteres.")
    private String description;

    public ZoneDTO() {}

    public ZoneDTO(Long idZone, String nom, String description) {
        this.idZone = idZone;
        this.nom = nom;
        this.description = description;
    }

    public Long getIdZone() { return idZone; }
    public void setIdZone(Long idZone) { this.idZone = idZone; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
