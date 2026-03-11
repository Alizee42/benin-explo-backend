package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CircuitActiviteDTO {

    private Long id;

    @NotNull(message = "Le circuit est obligatoire.")
    @Positive(message = "Le circuit doit etre un identifiant positif.")
    private Long circuitId;

    @NotNull(message = "L'activite est obligatoire.")
    @Positive(message = "L'activite doit etre un identifiant positif.")
    private Long activiteId;

    @Positive(message = "L'ordre doit etre superieur a zero.")
    private Integer ordre;

    @Positive(message = "Le jour indicatif doit etre superieur a zero.")
    private Integer jourIndicatif;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCircuitId() {
        return circuitId;
    }

    public void setCircuitId(Long circuitId) {
        this.circuitId = circuitId;
    }

    public Long getActiviteId() {
        return activiteId;
    }

    public void setActiviteId(Long activiteId) {
        this.activiteId = activiteId;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    public Integer getJourIndicatif() {
        return jourIndicatif;
    }

    public void setJourIndicatif(Integer jourIndicatif) {
        this.jourIndicatif = jourIndicatif;
    }
}
