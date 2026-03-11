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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityEndpointAccessTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void anonymousCanReadPublicCatalogEndpoints() throws Exception {
        mockMvc.perform(get("/api/circuits"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/hebergements"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/zones"))
                .andExpect(status().isOk());
    }

    @Test
    void anonymousCanCheckHebergementAvailability() throws Exception {
        mockMvc.perform(get("/api/reservations-hebergement/disponibilite")
                        .param("hebergementId", "1")
                        .param("dateArrivee", "2026-04-01")
                        .param("dateDepart", "2026-04-03"))
                .andExpect(status().isOk());
    }

    @Test
    void anonymousCannotReadProtectedOperationalEndpoints() throws Exception {
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/circuits-personnalises"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/reservations-hebergement"))
                .andExpect(status().isForbidden());
    }

    @Test
    void anonymousCannotWriteAdminCatalogEndpoints() throws Exception {
        mockMvc.perform(post("/api/circuits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/media")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanReadProtectedOperationalEndpoints() throws Exception {
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/circuits-personnalises"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/reservations-hebergement"))
                .andExpect(status().isOk());
    }
}
