package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HebergementDTO {

    private Long id;

    @NotBlank(message = "Le nom de l'hebergement est obligatoire.")
    @Size(max = 255, message = "Le nom de l'hebergement ne doit pas depasser 255 caracteres.")
    private String nom;

    @NotBlank(message = "Le type d'hebergement est obligatoire.")
    @Size(max = 100, message = "Le type d'hebergement ne doit pas depasser 100 caracteres.")
    private String type;

    @NotBlank(message = "La localisation est obligatoire.")
    @Size(max = 255, message = "La localisation ne doit pas depasser 255 caracteres.")
    private String localisation;

    @Size(max = 255, message = "Le quartier ne doit pas depasser 255 caracteres.")
    private String quartier;

    @NotBlank(message = "La description de l'hebergement est obligatoire.")
    @Size(max = 5000, message = "La description de l'hebergement ne doit pas depasser 5000 caracteres.")
    private String description;

    @NotNull(message = "Le prix par nuit est obligatoire.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix par nuit doit etre superieur a zero.")
    private BigDecimal prixParNuit;

    @Size(max = 20, message = "La liste d'images ne doit pas contenir plus de 20 elements.")
    private List<@NotBlank(message = "Une URL d'image ne peut pas etre vide.")
            @Size(max = 1000, message = "Une URL d'image ne doit pas depasser 1000 caracteres.") String> imageUrls;

    public HebergementDTO() {
    }

    public HebergementDTO(Long id, String nom, String type, String localisation,
                          String quartier, String description, BigDecimal prixParNuit, List<String> imageUrls) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.localisation = localisation;
        this.quartier = quartier;
        this.description = description;
        this.prixParNuit = prixParNuit;
        this.imageUrls = imageUrls;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public String getQuartier() { return quartier; }
    public void setQuartier(String quartier) { this.quartier = quartier; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrixParNuit() { return prixParNuit; }
    public void setPrixParNuit(BigDecimal prixParNuit) { this.prixParNuit = prixParNuit; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}
