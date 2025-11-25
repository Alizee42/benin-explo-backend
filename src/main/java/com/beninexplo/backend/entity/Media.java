package com.beninexplo.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "medias")

public class Media {

    /* ----------------------------------------------------
       🟦 ATTRIBUTS
    ---------------------------------------------------- */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMedia;

    private String url;
    private String type; // image, video, autre
    private String description;

    /* ----------------------------------------------------
       🟩 CONSTRUCTEURS
    ---------------------------------------------------- */

    public Media() {}

    public Media(Long idMedia, String url, String type, String description) {
        this.idMedia = idMedia;
        this.url = url;
        this.type = type;
        this.description = description;
    }

    /* ----------------------------------------------------
       🟨 GETTERS & SETTERS
    ---------------------------------------------------- */

    public Long getIdMedia() { return idMedia; }
    public void setIdMedia(Long idMedia) { this.idMedia = idMedia; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
