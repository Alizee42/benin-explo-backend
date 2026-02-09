package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "circuit_personnalise_jours")
public class CircuitPersonnaliseJour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "circuit_personnalise_id", nullable = false)
    private CircuitPersonnalise circuitPersonnalise;

    private int numeroJour;

    // Localisation du jour (peut être null si non spécifié)
    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @ManyToOne
    @JoinColumn(name = "ville_id")
    private Ville ville;

    // Activités sélectionnées pour ce jour
    @ManyToMany
    @JoinTable(
        name = "circuit_perso_jour_activites",
        joinColumns = @JoinColumn(name = "jour_id"),
        inverseJoinColumns = @JoinColumn(name = "activite_id")
    )
    private List<Activite> activites = new ArrayList<>();

    @Column(length = 2000)
    private String descriptionJour;

    // ----------------------------------------------------
    // CONSTRUCTEURS
    // ----------------------------------------------------
    public CircuitPersonnaliseJour() {}

    public CircuitPersonnaliseJour(int numeroJour, Zone zone, Ville ville) {
        this.numeroJour = numeroJour;
        this.zone = zone;
        this.ville = ville;
    }

    // ----------------------------------------------------
    // MÉTHODES UTILITAIRES
    // ----------------------------------------------------

    /**
     * Ajoute une activité au jour
     */
    public void addActivite(Activite activite) {
        if (!activites.contains(activite)) {
            activites.add(activite);
        }
    }

    /**
     * Supprime une activité du jour
     */
    public void removeActivite(Activite activite) {
        activites.remove(activite);
    }

    /**
     * Récupère la zone (soit directement, soit via la ville)
     */
    public Zone getZoneEffective() {
        if (zone != null) {
            return zone;
        }
        if (ville != null) {
            return ville.getZone();
        }
        return null;
    }

    /**
     * Validation: s'assurer que les activités correspondent à la zone/ville
     */
    @PrePersist
    @PreUpdate
    private void validate() {
        if (ville != null && zone != null) {
            // Vérifier cohérence zone/ville
            if (!ville.getZone().equals(zone)) {
                throw new IllegalStateException(
                    "La ville " + ville.getNom() + " n'appartient pas à la zone " + zone.getNom()
                );
            }
        }

        // Vérifier que les activités sont cohérentes avec la localisation
        Zone zoneEffective = getZoneEffective();
        if (zoneEffective != null) {
            for (Activite activite : activites) {
                if (!activite.getZone().equals(zoneEffective)) {
                    throw new IllegalStateException(
                        "L'activité " + activite.getNom() + 
                        " n'appartient pas à la zone du jour " + numeroJour
                    );
                }
            }
        }

        if (ville != null) {
            for (Activite activite : activites) {
                if (!activite.getVille().equals(ville)) {
                    throw new IllegalStateException(
                        "L'activité " + activite.getNom() + 
                        " n'appartient pas à la ville du jour " + numeroJour
                    );
                }
            }
        }
    }

    // ----------------------------------------------------
    // GETTERS / SETTERS
    // ----------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CircuitPersonnalise getCircuitPersonnalise() {
        return circuitPersonnalise;
    }

    public void setCircuitPersonnalise(CircuitPersonnalise circuitPersonnalise) {
        this.circuitPersonnalise = circuitPersonnalise;
    }

    public int getNumeroJour() {
        return numeroJour;
    }

    public void setNumeroJour(int numeroJour) {
        this.numeroJour = numeroJour;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Ville getVille() {
        return ville;
    }

    public void setVille(Ville ville) {
        this.ville = ville;
    }

    public List<Activite> getActivites() {
        return activites;
    }

    public void setActivites(List<Activite> activites) {
        this.activites = activites;
    }

    public String getDescriptionJour() {
        return descriptionJour;
    }

    public void setDescriptionJour(String descriptionJour) {
        this.descriptionJour = descriptionJour;
    }
}
