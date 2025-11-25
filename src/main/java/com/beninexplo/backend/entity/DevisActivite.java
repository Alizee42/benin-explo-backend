package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
public class DevisActivite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDevisActivite;

    @ManyToOne
    @JoinColumn(name = "devis_id")
    private Devis devis;

    @ManyToOne
    @JoinColumn(name = "activite_id")
    private Activite activite;

    private Integer ordre; // ordre du programme

    public DevisActivite() {
    }

    public DevisActivite(Long idDevisActivite, Devis devis, Activite activite, Integer ordre) {
        this.idDevisActivite = idDevisActivite;
        this.devis = devis;
        this.activite = activite;
        this.ordre = ordre;
    }

	public Long getIdDevisActivite() {
		return idDevisActivite;
	}

	public void setIdDevisActivite(Long idDevisActivite) {
		this.idDevisActivite = idDevisActivite;
	}

	public Devis getDevis() {
		return devis;
	}

	public void setDevis(Devis devis) {
		this.devis = devis;
	}

	public Activite getActivite() {
		return activite;
	}

	public void setActivite(Activite activite) {
		this.activite = activite;
	}

	public Integer getOrdre() {
		return ordre;
	}

	public void setOrdre(Integer ordre) {
		this.ordre = ordre;
	}
    
}
