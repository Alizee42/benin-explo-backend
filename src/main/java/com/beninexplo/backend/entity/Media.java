package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMedia;

    private String url;        // URL du fichier stocké (Render / Cloudinary / autre)
    private String type;       // image / video

    @Column(length = 2000)
    private String description;

    private LocalDateTime dateUpload = LocalDateTime.now();

    public Media() {
    }

    public Media(Long idMedia, String url, String type, String description, LocalDateTime dateUpload) {
        this.idMedia = idMedia;
        this.url = url;
        this.type = type;
        this.description = description;
        this.dateUpload = dateUpload;
    }

	public Long getIdMedia() {
		return idMedia;
	}

	public void setIdMedia(Long idMedia) {
		this.idMedia = idMedia;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getDateUpload() {
		return dateUpload;
	}

	public void setDateUpload(LocalDateTime dateUpload) {
		this.dateUpload = dateUpload;
	}
    
}
