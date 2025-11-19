package com.beninexplo.backend.dto;

import lombok.Data;

@Data
public class MediaDTO {

    private Long id;
    private String url;
    private String type;
    private String description;
    private String dateUpload; // formaté côté DTO
}
