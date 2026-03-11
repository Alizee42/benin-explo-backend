package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VilleDTO {

    private Long id;

    @NotBlank(message = "Le nom de la ville est obligatoire.")
    @Size(max = 255, message = "Le nom de la ville ne doit pas depasser 255 caracteres.")
    private String nom;

    @NotNull(message = "La zone est obligatoire.")
    @Positive(message = "La zone doit etre un identifiant positif.")
    private Long zoneId;

    private String zoneNom;

    public VilleDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Long getZoneId() { return zoneId; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }

    public String getZoneNom() { return zoneNom; }
    public void setZoneNom(String zoneNom) { this.zoneNom = zoneNom; }
}
