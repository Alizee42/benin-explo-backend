package com.beninexplo.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMedia;

    private String url;        // URL du fichier stocké (Render / Cloudinary / autre)
    private String type;       // image / video

    @Column(length = 2000)
    private String description;

    private LocalDateTime dateUpload = LocalDateTime.now();
}
