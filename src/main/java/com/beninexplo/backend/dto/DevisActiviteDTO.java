package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class DevisActiviteDTO {

    private Long id;

    private Long devisId;
    private Long activiteId;

    private Integer ordre;
}
