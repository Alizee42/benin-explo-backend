package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ActualiteDTO {

    private Long id;
    private String titre;
    private String contenu;
    private String datePublication;
    private Long imagePrincipaleId;
    private Long auteurId;

    public ActualiteDTO() {}

    public ActualiteDTO(Long id, String titre, String contenu,
                        String datePublication, Long imagePrincipaleId,
                        Long auteurId) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.datePublication = datePublication;
        this.imagePrincipaleId = imagePrincipaleId;
        this.auteurId = auteurId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getDatePublication() { return datePublication; }
    public void setDatePublication(String datePublication) { this.datePublication = datePublication; }

    public Long getImagePrincipaleId() { return imagePrincipaleId; }
    public void setImagePrincipaleId(Long imagePrincipaleId) { this.imagePrincipaleId = imagePrincipaleId; }

    public Long getAuteurId() { return auteurId; }
    public void setAuteurId(Long auteurId) { this.auteurId = auteurId; }
}
