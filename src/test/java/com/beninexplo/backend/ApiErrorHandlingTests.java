package com.beninexplo.backend;

import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiErrorHandlingTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * getRequiredCurrentUser() (utilisé par les services de réservation) cherche l'utilisateur
     * authentifié par email en base — @WithMockUser seul ne suffit pas.
     */
    private void ensureMockUserExists(String email) {
        if (utilisateurRepository.findByEmail(email).isPresent()) {
            return;
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom("Test");
        utilisateur.setPrenom("Admin");
        utilisateur.setEmail(email);
        utilisateur.setTelephone("+22900000000");
        utilisateur.setMotDePasse("hash");
        utilisateur.setRole("ADMIN");
        utilisateurRepository.save(utilisateur);
    }

    @Test
    @WithMockUser(username = "api.error.tests@example.com", roles = "ADMIN")
    void invalidReservationHebergementPayloadReturnsStructuredBadRequest() throws Exception {
        // POST /api/reservations-hebergement exige un utilisateur authentifié (SecurityConfig) ;
        // sans @WithMockUser la requête est bloquée à 403 avant même la validation du payload.
        ensureMockUserExists("api.error.tests@example.com");
        String payload = """
                {
                  "hebergementId": 1,
                  "nomClient": "Test",
                  "prenomClient": "User",
                  "emailClient": "test@example.com",
                  "telephoneClient": "00000000",
                  "dateArrivee": "2026-03-01",
                  "dateDepart": "2026-03-05",
                  "nombrePersonnes": 2,
                  "commentaires": "test"
                }
                """;

        mockMvc.perform(post("/api/reservations-hebergement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/reservations-hebergement"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void missingCircuitPersonnaliseReturnsStructuredNotFound() throws Exception {
        mockMvc.perform(get("/api/circuits-personnalises/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/circuits-personnalises/999999"));
    }
}
