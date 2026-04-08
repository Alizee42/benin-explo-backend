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

    @Size(max = 600, message = "Le resume de l'actualite ne doit pas depasser 600 caracteres.")
    private String resume;

    @Pattern(
            regexp = "^$|^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?(\\.\\d{1,9})?$",
            message = "La date de publication doit respecter le format ISO_LOCAL_DATE_TIME."
    )
    private String datePublication;

    private boolean aLaUne;
    private boolean publiee = true;

    @Positive(message = "L'image principale doit etre un identifiant positif.")
    private Long imagePrincipaleId;

    @Size(max = 1000, message = "L'URL de l'image ne doit pas depasser 1000 caracteres.")
    private String imageUrl;

    @Positive(message = "L'auteur doit etre un identifiant positif.")
    private Long auteurId;

    private String auteurNom;

    public ActualiteDTO() {}

    public ActualiteDTO(Long id, String titre, String contenu, String resume,
                        String datePublication, boolean aLaUne, boolean publiee,
                        Long imagePrincipaleId, String imageUrl, Long auteurId, String auteurNom) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.resume = resume;
        this.datePublication = datePublication;
        this.aLaUne = aLaUne;
        this.publiee = publiee;
        this.imagePrincipaleId = imagePrincipaleId;
        this.imageUrl = imageUrl;
        this.auteurId = auteurId;
        this.auteurNom = auteurNom;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public String getDatePublication() { return datePublication; }
    public void setDatePublication(String datePublication) { this.datePublication = datePublication; }

    public boolean isALaUne() { return aLaUne; }
    public void setALaUne(boolean ALaUne) { this.aLaUne = ALaUne; }

    public boolean isPubliee() { return publiee; }
    public void setPubliee(boolean publiee) { this.publiee = publiee; }

    public Long getImagePrincipaleId() { return imagePrincipaleId; }
    public void setImagePrincipaleId(Long imagePrincipaleId) { this.imagePrincipaleId = imagePrincipaleId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Long getAuteurId() { return auteurId; }
    public void setAuteurId(Long auteurId) { this.auteurId = auteurId; }

    public String getAuteurNom() { return auteurNom; }
    public void setAuteurNom(String auteurNom) { this.auteurNom = auteurNom; }
}
