package com.beninexplo.backend.dto;

import java.time.LocalDate;

public class ReservationHebergementIndisponibiliteDTO {

    private LocalDate dateArrivee;
    private LocalDate dateDepart;

    public ReservationHebergementIndisponibiliteDTO() {
    }

    public ReservationHebergementIndisponibiliteDTO(LocalDate dateArrivee, LocalDate dateDepart) {
        this.dateArrivee = dateArrivee;
        this.dateDepart = dateDepart;
    }

    public LocalDate getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(LocalDate dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public LocalDate getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDate dateDepart) {
        this.dateDepart = dateDepart;
    }
}
