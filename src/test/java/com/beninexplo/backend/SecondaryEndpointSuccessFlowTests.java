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
    void adminCanCreateAndUpdateVehiculeSuccessfully() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        MvcResult createResult = mockMvc.perform(post("/admin/vehicules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "marque": "Toyota",
                                  "modele": "Land Cruiser",
                                  "matricule": "TEST-%s",
                                  "annee": 2023,
                                  "disponible": true
                                }
                                """.formatted(suffix)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.marque").value("Toyota"))
                .andReturn();

        long vehiculeId = readId(createResult, "id");

        mockMvc.perform(put("/admin/vehicules/{id}", vehiculeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "marque": "Toyota",
                                  "modele": "Hiace",
                                  "matricule": "TEST-%s",
                                  "annee": 2024,
                                  "disponible": false
                                }
                                """.formatted(suffix)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vehiculeId))
                .andExpect(jsonPath("$.modele").value("Hiace"))
                .andExpect(jsonPath("$.disponible").value(false));
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
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateAndUpdateCircuitActiviteSuccessfully() throws Exception {
        long circuitId = firstIdFromArray("/api/circuits", "id");
        long activiteId = firstIdFromArray("/api/activites", "id");

        MvcResult createResult = mockMvc.perform(post("/api/circuit-activites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "circuitId": %d,
                                  "activiteId": %d,
                                  "ordre": 1,
                                  "jourIndicatif": 1
                                }
                                """.formatted(circuitId, activiteId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.circuitId").value(circuitId))
                .andExpect(jsonPath("$.activiteId").value(activiteId))
                .andReturn();

        long associationId = readId(createResult, "id");

        mockMvc.perform(put("/api/circuit-activites/{id}", associationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "circuitId": %d,
                                  "activiteId": %d,
                                  "ordre": 2,
                                  "jourIndicatif": 2
                                }
                                """.formatted(circuitId, activiteId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(associationId))
                .andExpect(jsonPath("$.ordre").value(2))
                .andExpect(jsonPath("$.jourIndicatif").value(2));
    }

    @Test
    void publicCanCreateDevisActiviteSuccessfully() throws Exception {
        long circuitId = firstIdFromArray("/api/circuits", "id");
        long activiteId = firstIdFromArray("/api/activites", "id");

        MvcResult devisResult = mockMvc.perform(post("/api/devis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nom": "Doe",
                                  "prenom": "Janet",
                                  "email": "devis.activite.%d@example.com",
                                  "telephone": "+22901020310",
                                  "message": "Je souhaite un devis avec activite complementaire.",
                                  "circuitId": %d
                                }
                                """.formatted(System.nanoTime(), circuitId)))
                .andExpect(status().isCreated())
                .andReturn();

        long devisId = readId(devisResult, "id");

        mockMvc.perform(post("/api/devis-activites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "devisId": %d,
                                  "activiteId": %d,
                                  "quantite": 3
                                }
                                """.formatted(devisId, activiteId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.devisId").value(devisId))
                .andExpect(jsonPath("$.activiteId").value(activiteId))
                .andExpect(jsonPath("$.quantite").value(3));
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

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanUploadImageOnImagesEndpointSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "secondary-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake-image-content".getBytes()
        );

        MvcResult result = mockMvc.perform(multipart("/api/images/upload")
                        .file(file)
                        .param("folder", "tests"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.filename").isString())
                .andExpect(jsonPath("$.url").value(org.hamcrest.Matchers.startsWith("/images/tests/")))
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        String filename = root.get("filename").asText();
        createdFiles.add(Path.of("uploads", "tests", filename));
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
