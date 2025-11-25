package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MediaDTO {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
    ---------------------------------------------------- */

    private Long id;
    private String url;
    private String type;
    private String description;

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */

    public MediaDTO() {}

    public MediaDTO(Long id, String url, String type, String description) {
        this.id = id;
        this.url = url;
        this.type = type;
        this.description = description;
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
