package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Actualite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idActualite;

    private String titre;

    @Column(length = 10000)
    private String contenu;

    private LocalDateTime datePublication = LocalDateTime.now();

    public Long getIdActualite() {
		return idActualite;
	}

	public void setIdActualite(Long idActualite) {
		this.idActualite = idActualite;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getContenu() {
		return contenu;
	}

	public void setContenu(String contenu) {
		this.contenu = contenu;
	}

	public LocalDateTime getDatePublication() {
		return datePublication;
	}

	public void setDatePublication(LocalDateTime datePublication) {
		this.datePublication = datePublication;
	}

	public Media getImagePrincipale() {
		return imagePrincipale;
	}

	public void setImagePrincipale(Media imagePrincipale) {
		this.imagePrincipale = imagePrincipale;
	}

	public Utilisateur getAuteur() {
		return auteur;
	}

	public void setAuteur(Utilisateur auteur) {
		this.auteur = auteur;
	}

	@ManyToOne
    @JoinColumn(name = "image_principale_id")
    private Media imagePrincipale;

    @ManyToOne
    @JoinColumn(name = "auteur_id")
    private Utilisateur auteur;

    public Actualite() {
    }

    public Actualite(Long idActualite, String titre, String contenu, LocalDateTime datePublication, Media imagePrincipale, Utilisateur auteur) {
        this.idActualite = idActualite;
        this.titre = titre;
        this.contenu = contenu;
        this.datePublication = datePublication;
        this.imagePrincipale = imagePrincipale;
        this.auteur = auteur;
    }
}
