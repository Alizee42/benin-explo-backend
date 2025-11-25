package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "devis_activites")
public class DevisActivite {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
    ---------------------------------------------------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "devis_id")
    private Devis devis;

    @ManyToOne
    @JoinColumn(name = "activite_id")
    private Activite activite;

    private Integer quantite;

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */
    public DevisActivite() {}

    public DevisActivite(Long id, Devis devis, Activite activite, Integer quantite) {
        this.id = id;
        this.devis = devis;
        this.activite = activite;
        this.quantite = quantite;
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Devis getDevis() { return devis; }
    public void setDevis(Devis devis) { this.devis = devis; }

    public Activite getActivite() { return activite; }
    public void setActivite(Activite activite) { this.activite = activite; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
}
