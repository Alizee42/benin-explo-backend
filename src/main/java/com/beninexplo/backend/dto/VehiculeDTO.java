package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VehiculeDTO {

    private Long id;

    @NotBlank(message = "La marque du vehicule est obligatoire.")
    @Size(max = 100, message = "La marque du vehicule ne doit pas depasser 100 caracteres.")
    private String marque;

    @NotBlank(message = "Le modele du vehicule est obligatoire.")
    @Size(max = 100, message = "Le modele du vehicule ne doit pas depasser 100 caracteres.")
    private String modele;

    @NotBlank(message = "La matricule du vehicule est obligatoire.")
    @Size(max = 50, message = "La matricule du vehicule ne doit pas depasser 50 caracteres.")
    private String matricule;

    @Min(value = 1950, message = "L'annee du vehicule doit etre superieure ou egale a 1950.")
    @Max(value = 2100, message = "L'annee du vehicule doit etre inferieure ou egale a 2100.")
    private int annee;
    private boolean disponible;

    /* ----------------------
       CONSTRUCTEURS
    ----------------------- */
    public VehiculeDTO() {}

    public VehiculeDTO(Long id, String marque, String modele, String matricule, int annee, boolean disponible) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.matricule = matricule;
        this.annee = annee;
        this.disponible = disponible;
    }

    /* ----------------------
       GETTERS & SETTERS
    ----------------------- */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
