package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.TombolaParticipantDTO;
import com.beninexplo.backend.service.TombolaParticipantService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tombola")
public class TombolaParticipantController {

    private final TombolaParticipantService tombolaParticipantService;

    public TombolaParticipantController(TombolaParticipantService tombolaParticipantService) {
        this.tombolaParticipantService = tombolaParticipantService;
    }

    @PostMapping("/inscription")
    public TombolaParticipantDTO inscriptionTombola(@Valid @RequestBody InscriptionTombolaRequest request) {
        return tombolaParticipantService.inscrireParticipant(
                request.getEmail(),
                request.getNom(),
                request.getPrenom()
        );
    }

    public static class InscriptionTombolaRequest {
        @NotBlank(message = "L'email est obligatoire.")
        @Email(message = "L'email doit etre valide.")
        @Size(max = 150, message = "L'email ne doit pas depasser 150 caracteres.")
        private String email;

        @NotBlank(message = "Le nom est obligatoire.")
        @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres.")
        private String nom;

        @NotBlank(message = "Le prenom est obligatoire.")
        @Size(max = 100, message = "Le prenom ne doit pas depasser 100 caracteres.")
        private String prenom;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }
    }
}
