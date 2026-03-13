package com.beninexplo.backend.dto;

import java.math.BigDecimal;

public class TarifsCircuitPersonnaliseDTO {

    private Long id;
    private String devise;
    private BigDecimal transportCompactParJour;
    private BigDecimal transportFamilialParJour;
    private BigDecimal transportMinibusParJour;
    private BigDecimal transportBusParJour;
    private BigDecimal guideParJour;
    private BigDecimal chauffeurParJour;
    private BigDecimal pensionCompleteParPersonneParJour;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public BigDecimal getTransportCompactParJour() {
        return transportCompactParJour;
    }

    public void setTransportCompactParJour(BigDecimal transportCompactParJour) {
        this.transportCompactParJour = transportCompactParJour;
    }

    public BigDecimal getTransportFamilialParJour() {
        return transportFamilialParJour;
    }

    public void setTransportFamilialParJour(BigDecimal transportFamilialParJour) {
        this.transportFamilialParJour = transportFamilialParJour;
    }

    public BigDecimal getTransportMinibusParJour() {
        return transportMinibusParJour;
    }

    public void setTransportMinibusParJour(BigDecimal transportMinibusParJour) {
        this.transportMinibusParJour = transportMinibusParJour;
    }

    public BigDecimal getTransportBusParJour() {
        return transportBusParJour;
    }

    public void setTransportBusParJour(BigDecimal transportBusParJour) {
        this.transportBusParJour = transportBusParJour;
    }

    public BigDecimal getGuideParJour() {
        return guideParJour;
    }

    public void setGuideParJour(BigDecimal guideParJour) {
        this.guideParJour = guideParJour;
    }

    public BigDecimal getChauffeurParJour() {
        return chauffeurParJour;
    }

    public void setChauffeurParJour(BigDecimal chauffeurParJour) {
        this.chauffeurParJour = chauffeurParJour;
    }

    public BigDecimal getPensionCompleteParPersonneParJour() {
        return pensionCompleteParPersonneParJour;
    }

    public void setPensionCompleteParPersonneParJour(BigDecimal pensionCompleteParPersonneParJour) {
        this.pensionCompleteParPersonneParJour = pensionCompleteParPersonneParJour;
    }
}
