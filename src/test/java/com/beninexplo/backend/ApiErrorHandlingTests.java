package com.beninexplo.backend;

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

    @Test
    void invalidReservationHebergementPayloadReturnsStructuredBadRequest() throws Exception {
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
