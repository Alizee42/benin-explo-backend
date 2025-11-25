package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.lang.Nullable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ReservationRequestDTO {

    @Nullable
    private Long utilisateurId;

    @Nullable
    private Long devisId;

    private String dateDebut;
    private String dateFin;

    private String montantTotal;

	public Long getUtilisateurId() {
		return utilisateurId;
	}

	public void setUtilisateurId(Long utilisateurId) {
		this.utilisateurId = utilisateurId;
	}

	public Long getDevisId() {
		return devisId;
	}

	public void setDevisId(Long devisId) {
		this.devisId = devisId;
	}

	public String getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(String dateDebut) {
		this.dateDebut = dateDebut;
	}

	public String getDateFin() {
		return dateFin;
	}

	public void setDateFin(String dateFin) {
		this.dateFin = dateFin;
	}

	public String getMontantTotal() {
		return montantTotal;
	}

	public void setMontantTotal(String montantTotal) {
		this.montantTotal = montantTotal;
	}
    
    
}
