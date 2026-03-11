package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DevisActiviteDTO {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
    ---------------------------------------------------- */
    private Long id;

    @NotNull(message = "Le devis est obligatoire.")
    @Positive(message = "Le devis doit etre un identifiant positif.")
    private Long devisId;

    @NotNull(message = "L'activite est obligatoire.")
    @Positive(message = "L'activite doit etre un identifiant positif.")
    private Long activiteId;

    @NotNull(message = "La quantite est obligatoire.")
    @Positive(message = "La quantite doit etre superieure a zero.")
    private Integer quantite;

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */
    public DevisActiviteDTO() {}

    public DevisActiviteDTO(Long id, Long devisId, Long activiteId, Integer quantite) {
        this.id = id;
        this.devisId = devisId;
        this.activiteId = activiteId;
        this.quantite = quantite;
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDevisId() { return devisId; }
    public void setDevisId(Long devisId) { this.devisId = devisId; }

    public Long getActiviteId() { return activiteId; }
    public void setActiviteId(Long activiteId) { this.activiteId = activiteId; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
}
