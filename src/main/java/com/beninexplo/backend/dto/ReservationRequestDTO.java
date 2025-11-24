package com.beninexplo.backend.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class ReservationRequestDTO {

    @Nullable
    private Long utilisateurId;

    @Nullable
    private Long devisId;

    private String dateDebut;
    private String dateFin;

    private String montantTotal;
}
