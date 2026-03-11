package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ActualiteDTO {

    private Long id;

    @NotBlank(message = "Le titre de l'actualite est obligatoire.")
    @Size(max = 255, message = "Le titre de l'actualite ne doit pas depasser 255 caracteres.")
    private String titre;

    @NotBlank(message = "Le contenu de l'actualite est obligatoire.")
    @Size(max = 20000, message = "Le contenu de l'actualite ne doit pas depasser 20000 caracteres.")
    private String contenu;

    @Pattern(
            regexp = "^$|^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?(\\.\\d{1,9})?$",
            message = "La date de publication doit respecter le format ISO_LOCAL_DATE_TIME."
    )
    private String datePublication;

    private boolean aLaUne;

    @Positive(message = "L'image principale doit etre un identifiant positif.")
    private Long imagePrincipaleId;

    @Positive(message = "L'auteur doit etre un identifiant positif.")
    private Long auteurId;

    public ActualiteDTO() {}

    public ActualiteDTO(Long id, String titre, String contenu,
                        String datePublication, boolean aLaUne, Long imagePrincipaleId,
                        Long auteurId) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.datePublication = datePublication;
        this.aLaUne = aLaUne;
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

    public boolean isALaUne() { return aLaUne; }
    public void setALaUne(boolean ALaUne) { this.aLaUne = ALaUne; }

    public Long getImagePrincipaleId() { return imagePrincipaleId; }
    public void setImagePrincipaleId(Long imagePrincipaleId) { this.imagePrincipaleId = imagePrincipaleId; }

    public Long getAuteurId() { return auteurId; }
    public void setAuteurId(Long auteurId) { this.auteurId = auteurId; }
}
