package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class CircuitActiviteDTO {

    private Long id;

    private Long circuitId;
    private Long activiteId;

    private Integer ordre;
    private Integer jourIndicatif;
}
