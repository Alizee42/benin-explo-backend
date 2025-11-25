package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "circuit_activites")
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

    private Integer ordre;          // ordre d'exécution dans le circuit
    private Integer jourIndicatif;  // optionnel : n° du jour dans le circuit

    public CircuitActivite() {}

    public CircuitActivite(Long idCircuitActivite, Circuit circuit, Activite activite,
                           Integer ordre, Integer jourIndicatif) {
        this.idCircuitActivite = idCircuitActivite;
        this.circuit = circuit;
        this.activite = activite;
        this.ordre = ordre;
        this.jourIndicatif = jourIndicatif;
    }
 // -----------------------------------------
    // GETTERS / SETTERS
    // -----------------------------------------

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
