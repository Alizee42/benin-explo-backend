package com.beninexplo.backend.dto;

import com.beninexplo.backend.entity.TypeActivite;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ActiviteDTO {

    private Long id;

    @NotBlank(message = "Le nom de l'activite est obligatoire.")
    @Size(max = 255, message = "Le nom de l'activite ne doit pas depasser 255 caracteres.")
    private String nom;

    private TypeActivite type;

    @NotBlank(message = "La description de l'activite est obligatoire.")
    @Size(max = 5000, message = "La description de l'activite ne doit pas depasser 5000 caracteres.")
    private String description;

    @Positive(message = "La ville doit etre un identifiant positif.")
    private Long villeId;

    private String villeNom;
    private Long zoneId;
    private String zoneNom;

    @PositiveOrZero(message = "La duree interne doit etre positive ou nulle.")
    private Integer dureeInterne;

    @PositiveOrZero(message = "Le poids doit etre positif ou nul.")
    private Integer poids;

    @Size(max = 100, message = "La difficulte ne doit pas depasser 100 caracteres.")
    private String difficulte;

    private Long categorieId;
    private String categorieNom;

    @Positive(message = "L'image principale doit etre un identifiant positif.")
    private Long imagePrincipaleId;

    private String imagePrincipaleUrl;

    public ActiviteDTO() {
    }

    public ActiviteDTO(Long id,
                       String nom,
                       TypeActivite type,
                       String description,
                       Long villeId,
                       String villeNom,
                       Long zoneId,
                       String zoneNom,
                       Integer dureeInterne,
                       Integer poids,
                       String difficulte,
                       Long imagePrincipaleId,
                       String imagePrincipaleUrl) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.villeId = villeId;
        this.villeNom = villeNom;
        this.zoneId = zoneId;
        this.zoneNom = zoneNom;
        this.dureeInterne = dureeInterne;
        this.poids = poids;
        this.difficulte = difficulte;
        this.imagePrincipaleId = imagePrincipaleId;
        this.imagePrincipaleUrl = imagePrincipaleUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public TypeActivite getType() { return type; }
    public void setType(TypeActivite type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getVilleId() { return villeId; }
    public void setVilleId(Long villeId) { this.villeId = villeId; }

    public String getVilleNom() { return villeNom; }
    public void setVilleNom(String villeNom) { this.villeNom = villeNom; }

    public Long getZoneId() { return zoneId; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }

    public String getZoneNom() { return zoneNom; }
    public void setZoneNom(String zoneNom) { this.zoneNom = zoneNom; }

    public Integer getDureeInterne() { return dureeInterne; }
    public void setDureeInterne(Integer dureeInterne) { this.dureeInterne = dureeInterne; }

    public Integer getPoids() { return poids; }
    public void setPoids(Integer poids) { this.poids = poids; }

    public String getDifficulte() { return difficulte; }
    public void setDifficulte(String difficulte) { this.difficulte = difficulte; }

    public Long getCategorieId() { return categorieId; }
    public void setCategorieId(Long categorieId) { this.categorieId = categorieId; }

    public String getCategorieNom() { return categorieNom; }
    public void setCategorieNom(String categorieNom) { this.categorieNom = categorieNom; }

    public Long getImagePrincipaleId() { return imagePrincipaleId; }
    public void setImagePrincipaleId(Long imagePrincipaleId) { this.imagePrincipaleId = imagePrincipaleId; }

    public String getImagePrincipaleUrl() { return imagePrincipaleUrl; }
    public void setImagePrincipaleUrl(String imagePrincipaleUrl) { this.imagePrincipaleUrl = imagePrincipaleUrl; }
}
