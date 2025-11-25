package com.beninexplo.backend.controller;

import com.beninexplo.backend.dto.TombolaParticipantDTO;
import com.beninexplo.backend.service.TombolaParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tombola")
@CrossOrigin("*")
public class TombolaParticipantController {

    @Autowired
    private TombolaParticipantService tombolaParticipantService;

    /* ----------------------------------------------------
       🟦 INSCRIPTION À LA TOMBOLA
       Input :
       {
         "email": "exemple@mail.com",
         "nom": "Nom",
         "prenom": "Prenom"
       }

       - Crée un utilisateur si nécessaire
       - Ajoute le rôle PARTICIPANT si existant
       - Enregistre la participation
    ---------------------------------------------------- */
    @PostMapping("/inscription")
    public TombolaParticipantDTO inscriptionTombola(@RequestBody InscriptionTombolaRequest request) {
        return tombolaParticipantService.inscrireParticipant(
                request.getEmail(),
                request.getNom(),
                request.getPrenom()
        );
    }

    /* ----------------------------------------------------
       🟩 DTO interne pour la requête
       (permet d’avoir une entrée claire sans créer un autre fichier)
    ---------------------------------------------------- */
    public static class InscriptionTombolaRequest {
        private String email;
        private String nom;
        private String prenom;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }

        public String getPrenom() { return prenom; }
        public void setPrenom(String prenom) { this.prenom = prenom; }
    }
}
