package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DevisActiviteDTO {

    private Long id;

    private Long devisId;
    private Long activiteId;

    private Integer ordre;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDevisId() {
		return devisId;
	}

	public void setDevisId(Long devisId) {
		this.devisId = devisId;
	}

	public Long getActiviteId() {
		return activiteId;
	}

	public void setActiviteId(Long activiteId) {
		this.activiteId = activiteId;
	}

	public Integer getOrdre() {
		return ordre;
	}

	public void setOrdre(Integer ordre) {
		this.ordre = ordre;
	}
    
    
    
}
