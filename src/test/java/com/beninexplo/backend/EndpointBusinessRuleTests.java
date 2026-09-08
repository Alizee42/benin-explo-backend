package com.beninexplo.backend;

import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.repository.UtilisateurRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EndpointBusinessRuleTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * getRequiredCurrentUser() (utilisé par ReservationService) cherche l'utilisateur
     * authentifié par email en base — @WithMockUser seul ne suffit pas.
     */
    private void ensureMockUserExists(String email) {
        if (utilisateurRepository.findByEmail(email).isPresent()) {
            return;
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom("Test");
        utilisateur.setPrenom("User");
        utilisateur.setEmail(email);
        utilisateur.setTelephone("+22900000000");
        utilisateur.setMotDePasse("hash");
        utilisateur.setRole("USER");
        utilisateurRepository.save(utilisateur);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatingUnknownCircuitReturnsStructuredNotFound() throws Exception {
        long villeId = firstIdFromArray("/api/villes");
        String payload = """
                {
                  "titre": "Circuit test",
                  "description": "Description valide pour declencher le vrai cas 404.",
                  "dureeIndicative": "3 jours / 2 nuits",
                  "prixIndicatif": 150000,
                  "villeId": %d,
                  "actif": true
                }
                """.formatted(villeId);

        mockMvc.perform(put("/api/circuits/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/circuits/999999"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void mediaUploadWithEmptyFileReturnsStructuredBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/api/media/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/media/upload"));
    }

    @Test
    void missingUploadedDocumentReturnsStructuredNotFound() throws Exception {
        mockMvc.perform(get("/api/uploads/documents/introuvable.pdf"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/uploads/documents/introuvable.pdf"));
    }

    @Test
    void devisWithUnknownCircuitReturnsStructuredNotFound() throws Exception {
        String payload = """
                {
                  "nom": "Doe",
                  "prenom": "Jane",
                  "email": "jane@example.com",
                  "telephone": "0102030405",
                  "message": "Besoin d'un devis",
                  "circuitId": 999999
                }
                """;

        mockMvc.perform(post("/api/devis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/devis"));
    }

    @Test
    @WithMockUser(username = "business.rule.tests@example.com", roles = "USER")
    void reservationWithUnknownCircuitReturnsStructuredNotFound() throws Exception {
        // POST /api/reservations exige un utilisateur authentifié (SecurityConfig) ; sans
        // @WithMockUser la requête est bloquée à 403 avant même d'atteindre la règle métier.
        ensureMockUserExists("business.rule.tests@example.com");
        String payload = """
                {
                  "nom": "Doe",
                  "prenom": "John",
                  "email": "john@example.com",
                  "telephone": "0102030405",
                  "dateReservation": "%s",
                  "circuitId": 999999
                }
                """.formatted(LocalDate.now().plusDays(15));

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/reservations"));
    }

    private long firstIdFromArray(String endpoint) throws Exception {
        String body = mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(body);
        if (!root.isArray() || root.isEmpty()) {
            throw new IllegalStateException("Aucune donnee disponible sur " + endpoint + " pour executer le test.");
        }
        return root.get(0).get("id").asLong();
    }
}
