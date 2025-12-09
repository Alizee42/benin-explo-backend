package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.math.BigDecimal;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CircuitDTO {

    private Long id;
    private String titre;
    private String resume;
    private String description;

    private String dureeIndicative;
    private BigDecimal prixIndicatif;

    private String formuleProposee;
    private String localisation;

    private boolean actif;

    private Long zoneId;
    private Long villeId;
    private String villeNom;
    private List<Long> activiteIds;
    private String img; // Image principale (hero image)
    private List<String> galerie; // Galerie d'images (3-10 images)
    private List<ProgrammeDay> programme; // Programme jour par jour (peut être chaîne ou objet)
    private List<String> aventures; // Liste d'aventures / activités (nouveau champ)
    private List<PointFort> pointsForts; // Points forts avec icône
    private List<String> inclus; // Ce qui est inclus
    private List<String> nonInclus; // Ce qui n'est pas inclus

    public CircuitDTO() {}

    // GETTERS / SETTERS
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

    // Classe interne pour PointFort
    public static class PointFort {
        private String icon;
        private String title;
        private String desc;

        public PointFort() {}

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

    // Classe interne pour représenter une journée du programme
    public static class ProgrammeDay {
        private Integer day; // numéro du jour (optionnel)
        private String description;
        private String approxTime;
        private List<String> mealsIncluded;
        private List<Integer> activities;

        public ProgrammeDay() {}

        public ProgrammeDay(Integer day, String description, String approxTime,
                            List<String> mealsIncluded, List<Integer> activities) {
            this.day = day;
            this.description = description;
            this.approxTime = approxTime;
            this.mealsIncluded = mealsIncluded;
            this.activities = activities;
        }

        public Integer getDay() { return day; }
        public void setDay(Integer day) { this.day = day; }

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
