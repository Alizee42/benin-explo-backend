package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
public class CircuitActivite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCircuitActivite;

    @ManyToOne
    @JoinColumn(name = "circuit_id")
    private Circuit circuit;

    @ManyToOne
    @JoinColumn(name = "activite_id")
    private Activite activite;

    private Integer ordre;          // ordre d'exécution
    private Integer jourIndicatif;  // optionnel : jour du circuit

    public CircuitActivite() {
    }

    public CircuitActivite(Long idCircuitActivite, Circuit circuit, Activite activite, Integer ordre, Integer jourIndicatif) {
        this.idCircuitActivite = idCircuitActivite;
        this.circuit = circuit;
        this.activite = activite;
        this.ordre = ordre;
        this.jourIndicatif = jourIndicatif;
    }

	public Long getIdCircuitActivite() {
		return idCircuitActivite;
	}

	public void setIdCircuitActivite(Long idCircuitActivite) {
		this.idCircuitActivite = idCircuitActivite;
	}

	public Circuit getCircuit() {
		return circuit;
	}

	public void setCircuit(Circuit circuit) {
		this.circuit = circuit;
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

	public Integer getJourIndicatif() {
		return jourIndicatif;
	}

	public void setJourIndicatif(Integer jourIndicatif) {
		this.jourIndicatif = jourIndicatif;
	}
    
}
