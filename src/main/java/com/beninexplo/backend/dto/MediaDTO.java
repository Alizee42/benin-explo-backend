package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MediaDTO {

    private Long id;
    private String url;
    private String type;
    private String description;
    private String dateUpload; // formaté côté DTO
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getDateUpload() {
		return dateUpload;
	}
	public void setDateUpload(String dateUpload) {
		this.dateUpload = dateUpload;
	}
    
    
    
}
