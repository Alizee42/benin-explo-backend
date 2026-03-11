package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CircuitDTO {

    private Long id;

    @NotBlank(message = "Le titre du circuit est obligatoire.")
    @Size(max = 255, message = "Le titre du circuit ne doit pas depasser 255 caracteres.")
    private String titre;

    @Size(max = 2000, message = "Le resume ne doit pas depasser 2000 caracteres.")
    private String resume;

    @NotBlank(message = "La description du circuit est obligatoire.")
    @Size(max = 5000, message = "La description du circuit ne doit pas depasser 5000 caracteres.")
    private String description;

    @NotBlank(message = "La duree indicative est obligatoire.")
    @Size(max = 255, message = "La duree indicative ne doit pas depasser 255 caracteres.")
    private String dureeIndicative;

    @NotNull(message = "Le prix indicatif est obligatoire.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix indicatif doit etre superieur a zero.")
    private BigDecimal prixIndicatif;

    @Size(max = 255, message = "La formule proposee ne doit pas depasser 255 caracteres.")
    private String formuleProposee;

    private String localisation;
    private boolean actif;
    private Long zoneId;
    private String zoneNom;

    @NotNull(message = "La ville principale du circuit est obligatoire.")
    @Positive(message = "La ville principale du circuit doit etre un identifiant positif.")
    private Long villeId;

    private String villeNom;
    private List<Long> activiteIds;

    @Size(max = 1000, message = "L'image principale ne doit pas depasser 1000 caracteres.")
    private String img;

    @Size(max = 20, message = "La galerie ne doit pas contenir plus de 20 images.")
    private List<@NotBlank(message = "Les URL de galerie ne peuvent pas etre vides.")
            @Size(max = 1000, message = "Une URL de galerie ne doit pas depasser 1000 caracteres.") String> galerie;

    @Valid
    @Size(max = 30, message = "Le programme ne doit pas contenir plus de 30 etapes.")
    private List<ProgrammeDay> programme;

    @Size(max = 30, message = "La liste d'aventures ne doit pas contenir plus de 30 elements.")
    private List<@NotBlank(message = "Une aventure ne peut pas etre vide.")
            @Size(max = 255, message = "Une aventure ne doit pas depasser 255 caracteres.") String> aventures;

    @Valid
    @Size(max = 20, message = "Les points forts ne doivent pas contenir plus de 20 elements.")
    private List<PointFort> pointsForts;

    @Size(max = 30, message = "La liste des inclus ne doit pas contenir plus de 30 elements.")
    private List<@NotBlank(message = "Une valeur incluse ne peut pas etre vide.")
            @Size(max = 255, message = "Une valeur incluse ne doit pas depasser 255 caracteres.") String> inclus;

    @Size(max = 30, message = "La liste des non inclus ne doit pas contenir plus de 30 elements.")
    private List<@NotBlank(message = "Une valeur non incluse ne peut pas etre vide.")
            @Size(max = 255, message = "Une valeur non incluse ne doit pas depasser 255 caracteres.") String> nonInclus;

    public CircuitDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDureeIndicative() { return dureeIndicative; }
    public void setDureeIndicative(String dureeIndicative) { this.dureeIndicative = dureeIndicative; }

    public BigDecimal getPrixIndicatif() { return prixIndicatif; }
    public void setPrixIndicatif(BigDecimal prixIndicatif) { this.prixIndicatif = prixIndicatif; }

    public String getFormuleProposee() { return formuleProposee; }
    public void setFormuleProposee(String formuleProposee) { this.formuleProposee = formuleProposee; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public Long getZoneId() { return zoneId; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }

    public String getZoneNom() { return zoneNom; }
    public void setZoneNom(String zoneNom) { this.zoneNom = zoneNom; }

    public Long getVilleId() { return villeId; }
    public void setVilleId(Long villeId) { this.villeId = villeId; }

    public String getVilleNom() { return villeNom; }
    public void setVilleNom(String villeNom) { this.villeNom = villeNom; }

    public List<Long> getActiviteIds() { return activiteIds; }
    public void setActiviteIds(List<Long> activiteIds) { this.activiteIds = activiteIds; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public List<String> getGalerie() { return galerie; }
    public void setGalerie(List<String> galerie) { this.galerie = galerie; }

    public List<ProgrammeDay> getProgramme() { return programme; }
    public void setProgramme(List<ProgrammeDay> programme) { this.programme = programme; }

    public List<String> getAventures() { return aventures; }
    public void setAventures(List<String> aventures) { this.aventures = aventures; }

    public List<PointFort> getPointsForts() { return pointsForts; }
    public void setPointsForts(List<PointFort> pointsForts) { this.pointsForts = pointsForts; }

    public List<String> getInclus() { return inclus; }
    public void setInclus(List<String> inclus) { this.inclus = inclus; }

    public List<String> getNonInclus() { return nonInclus; }
    public void setNonInclus(List<String> nonInclus) { this.nonInclus = nonInclus; }

    public static class PointFort {
        @NotBlank(message = "L'icone d'un point fort est obligatoire.")
        @Size(max = 100, message = "L'icone d'un point fort ne doit pas depasser 100 caracteres.")
        private String icon;

        @NotBlank(message = "Le titre d'un point fort est obligatoire.")
        @Size(max = 120, message = "Le titre d'un point fort ne doit pas depasser 120 caracteres.")
        private String title;

        @NotBlank(message = "La description d'un point fort est obligatoire.")
        @Size(max = 500, message = "La description d'un point fort ne doit pas depasser 500 caracteres.")
        private String desc;

        public PointFort() {
        }

        public PointFort(String icon, String title, String desc) {
            this.icon = icon;
            this.title = title;
            this.desc = desc;
        }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDesc() { return desc; }
        public void setDesc(String desc) { this.desc = desc; }
    }

    public static class ProgrammeDay {
        @Positive(message = "Le numero du jour doit etre superieur a zero.")
        private Integer day;

        @Size(max = 120, message = "Le titre d'etape ne doit pas depasser 120 caracteres.")
        private String title;

        @NotBlank(message = "La description d'etape est obligatoire.")
        @Size(max = 2000, message = "La description d'etape ne doit pas depasser 2000 caracteres.")
        private String description;

        @Size(max = 120, message = "L'horaire indicatif ne doit pas depasser 120 caracteres.")
        private String approxTime;

        @Size(max = 10, message = "La liste des repas inclus ne doit pas contenir plus de 10 elements.")
        private List<@NotBlank(message = "Un repas inclus ne peut pas etre vide.")
                @Size(max = 100, message = "Un repas inclus ne doit pas depasser 100 caracteres.") String> mealsIncluded;

        @Size(max = 20, message = "La liste des activites d'une etape ne doit pas contenir plus de 20 elements.")
        private List<@Positive(message = "Une activite du programme doit etre un identifiant positif.") Integer> activities;

        public ProgrammeDay() {
        }

        public ProgrammeDay(Integer day, String title, String description, String approxTime,
                            List<String> mealsIncluded, List<Integer> activities) {
            this.day = day;
            this.title = title;
            this.description = description;
            this.approxTime = approxTime;
            this.mealsIncluded = mealsIncluded;
            this.activities = activities;
        }

        public Integer getDay() { return day; }
        public void setDay(Integer day) { this.day = day; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getApproxTime() { return approxTime; }
        public void setApproxTime(String approxTime) { this.approxTime = approxTime; }

        public List<String> getMealsIncluded() { return mealsIncluded; }
        public void setMealsIncluded(List<String> mealsIncluded) { this.mealsIncluded = mealsIncluded; }

        public List<Integer> getActivities() { return activities; }
        public void setActivities(List<Integer> activities) { this.activities = activities; }
    }
}
