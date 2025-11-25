package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ZoneDTO {

    private Long idZone;
    private String nom;
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
