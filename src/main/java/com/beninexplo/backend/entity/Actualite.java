package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Actualite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idActualite;

    private String titre;

    @Column(length = 10000)
    private String contenu;

    private LocalDateTime datePublication = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "image_principale_id")
    private Media imagePrincipale;

    @ManyToOne
    @JoinColumn(name = "auteur_id")
    private Utilisateur auteur;
}
