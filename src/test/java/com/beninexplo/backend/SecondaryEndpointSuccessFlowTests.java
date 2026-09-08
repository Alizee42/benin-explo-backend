package com.beninexplo.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecondaryEndpointSuccessFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final List<Path> createdFiles = new ArrayList<>();

    @AfterEach
    void cleanupFiles() throws Exception {
        for (Path createdFile : createdFiles) {
            Files.deleteIfExists(createdFile);
        }
        createdFiles.clear();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateAndUpdateZoneSuccessfully() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        MvcResult createResult = mockMvc.perform(post("/api/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nom": "Zone %s",
                                  "description": "Zone de test backend"
                                }
                                """.formatted(suffix)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Zone " + suffix))
                .andReturn();

        long zoneId = readId(createResult, "idZone", "id");

        mockMvc.perform(put("/api/zones/{id}", zoneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nom": "Zone %s Maj",
                                  "description": "Zone mise a jour"
                                }
                                """.formatted(suffix)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idZone").value(zoneId))
                .andExpect(jsonPath("$.nom").value("Zone " + suffix + " Maj"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateAndUpdateCategorySuccessfully() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        MvcResult createResult = mockMvc.perform(post("/api/categories-activites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nom": "Categorie %s",
                                  "description": "Categorie secondaire de test"
                                }
                                """.formatted(suffix)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Categorie " + suffix))
                .andReturn();

        long categoryId = readId(createResult, "id");

        mockMvc.perform(put("/api/categories-activites/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nom": "Categorie %s Maj",
                                  "description": "Categorie mise a jour"
                                }
                                """.formatted(suffix)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.nom").value("Categorie " + suffix + " Maj"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateAndUpdateSiteSettingsSuccessfully() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        MvcResult createResult = mockMvc.perform(post("/api/parametres-site")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "emailContact": "contact.%s@example.com",
                                  "telephoneContact": "+22901020308",
                                  "adresseAgence": "Cotonou centre"
                                }
                                """.formatted(suffix)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.emailContact").value("contact." + suffix + "@example.com"))
                .andReturn();

        long paramId = readId(createResult, "id");

        mockMvc.perform(put("/api/parametres-site/{id}", paramId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "emailContact": "contact.update.%s@example.com",
                                  "telephoneContact": "+22901020309",
                                  "adresseAgence": "Ganhi"
                                }
                                """.formatted(suffix)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paramId))
                .andExpect(jsonPath("$.adresseAgence").value("Ganhi"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateAndUpdateActualiteSuccessfully() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        MvcResult createResult = mockMvc.perform(post("/admin/actualites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titre": "Actualite %s",
                                  "contenu": "Contenu d'actualite de test pour le backend.",
                                  "datePublication": "%s"
                                }
                                """.formatted(suffix, LocalDateTime.now().withNano(0))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titre").value("Actualite " + suffix))
                .andReturn();

        long actualiteId = readId(createResult, "id");

        mockMvc.perform(put("/admin/actualites/{id}", actualiteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titre": "Actualite %s Maj",
                                  "contenu": "Contenu mis a jour pour l'actualite de test.",
                                  "datePublication": "%s"
                                }
                                """.formatted(suffix, LocalDateTime.now().plusHours(1).withNano(0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(actualiteId))
                .andExpect(jsonPath("$.titre").value("Actualite " + suffix + " Maj"));
    }

    @Test
    void publicCanRegisterTombolaSuccessfully() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        mockMvc.perform(post("/tombola/inscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "tombola.%s@example.com",
                                  "nom": "Tombola",
                                  "prenom": "Client"
                                }
                                """.formatted(suffix)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("tombola." + suffix + "@example.com"))
                .andExpect(jsonPath("$.nom").value("Tombola"))
                .andExpect(jsonPath("$.prenom").value("Client"));
    }

    private long firstIdFromArray(String endpoint, String fieldName) throws Exception {
        MvcResult result = mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        if (!root.isArray() || root.isEmpty() || root.get(0).get(fieldName) == null) {
            throw new IllegalStateException("Aucune donnee exploitable sur " + endpoint + " pour executer le test.");
        }
        return root.get(0).get(fieldName).asLong();
    }

    private long readId(MvcResult result, String... candidateFields) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        for (String candidateField : candidateFields) {
            JsonNode node = root.get(candidateField);
            if (node != null && !node.isNull()) {
                return node.asLong();
            }
        }
        throw new IllegalStateException("Aucun identifiant n'a ete trouve dans la reponse JSON.");
    }
}
