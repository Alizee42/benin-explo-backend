package com.beninexplo.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EndpointSuccessFlowTests {

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
    void adminCanCreateAndUpdateCircuitSuccessfully() throws Exception {
        long villeId = firstIdFromArray("/api/villes");
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        String createPayload = """
                {
                  "titre": "Circuit %s",
                  "resume": "Un circuit de test",
                  "description": "Circuit de demonstration pour valider la creation.",
                  "dureeIndicative": "3 jours / 2 nuits",
                  "prixIndicatif": 185000,
                  "formuleProposee": "Standard",
                  "villeId": %d,
                  "actif": true,
                  "img": "https://example.com/circuit-%s.jpg"
                }
                """.formatted(suffix, villeId, suffix);

        MvcResult createResult = mockMvc.perform(post("/api/circuits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titre").value("Circuit " + suffix))
                .andExpect(jsonPath("$.villeId").value(villeId))
                .andReturn();

        long circuitId = readId(createResult);

        String updatePayload = """
                {
                  "titre": "Circuit %s Maj",
                  "resume": "Resume mis a jour",
                  "description": "Description mise a jour pour le circuit de test.",
                  "dureeIndicative": "4 jours / 3 nuits",
                  "prixIndicatif": 225000,
                  "formuleProposee": "Premium",
                  "villeId": %d,
                  "actif": true,
                  "img": "https://example.com/circuit-%s-update.jpg"
                }
                """.formatted(suffix, villeId, suffix);

        mockMvc.perform(put("/api/circuits/{id}", circuitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(circuitId))
                .andExpect(jsonPath("$.titre").value("Circuit " + suffix + " Maj"))
                .andExpect(jsonPath("$.prixIndicatif").value(225000));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateAndUpdateMediaMetadataSuccessfully() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String createPayload = """
                {
                  "url": "/uploads/media-%s.jpg",
                  "type": "image",
                  "description": "Media de test"
                }
                """.formatted(suffix);

        MvcResult createResult = mockMvc.perform(post("/api/media")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("image"))
                .andReturn();

        long mediaId = readId(createResult);

        String updatePayload = """
                {
                  "url": "/uploads/media-%s-updated.jpg",
                  "type": "video",
                  "description": "Media mis a jour"
                }
                """.formatted(suffix);

        mockMvc.perform(put("/api/media/{id}", mediaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mediaId))
                .andExpect(jsonPath("$.type").value("video"))
                .andExpect(jsonPath("$.description").value("Media mis a jour"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanUploadMediaImageSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "media-success.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake-image-content".getBytes()
        );

        MvcResult result = mockMvc.perform(multipart("/api/media/upload").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("image"))
                .andExpect(jsonPath("$.description").value("media-success.png"))
                .andExpect(jsonPath("$.url").isString())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        String url = root.get("url").asText();
        String filename = url.replaceFirst("^/uploads/", "");
        createdFiles.add(Path.of("uploads", filename));
    }

    @Test
    void publicCanReadUploadedDocumentSuccessfully() throws Exception {
        String filename = "endpoint-success-" + UUID.randomUUID() + ".txt";
        Path file = Path.of("uploads", "documents", filename);
        Files.createDirectories(file.getParent());
        Files.writeString(file, "document de test backend");
        createdFiles.add(file);

        mockMvc.perform(get("/api/uploads/documents/{filename}", filename))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=\"" + filename + "\""))
                .andExpect(content().string("document de test backend"));
    }

    @Test
    void publicCanCreateDevisSuccessfully() throws Exception {
        long circuitId = firstIdFromArray("/api/circuits");

        String payload = """
                {
                  "nom": "Doe",
                  "prenom": "Jane",
                  "email": "jane.%d@example.com",
                  "telephone": "+22901020304",
                  "message": "Je souhaite recevoir un devis detaille pour ce circuit.",
                  "circuitId": %d
                }
                """.formatted(System.nanoTime(), circuitId);

        mockMvc.perform(post("/api/devis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Doe"))
                .andExpect(jsonPath("$.prenom").value("Jane"))
                .andExpect(jsonPath("$.circuitId").value(circuitId));
    }

    @Test
    void publicCanCreateReservationSuccessfully() throws Exception {
        long circuitId = firstIdFromArray("/api/circuits");

        String payload = """
                {
                  "nom": "Doe",
                  "prenom": "John",
                  "email": "john.%d@example.com",
                  "telephone": "+22901020305",
                  "dateReservation": "%s",
                  "circuitId": %d
                }
                """.formatted(System.nanoTime(), LocalDate.now().plusDays(15), circuitId);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Doe"))
                .andExpect(jsonPath("$.prenom").value("John"))
                .andExpect(jsonPath("$.circuitId").value(circuitId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateAndUpdateReservationHebergementSuccessfully() throws Exception {
        LocalDate dateArrivee = LocalDate.now().plusDays(20);
        LocalDate dateDepart = LocalDate.now().plusDays(23);
        long hebergementId = createTestHebergement();

        String createPayload = """
                {
                  "hebergementId": %d,
                  "nomClient": "Client",
                  "prenomClient": "Demo",
                  "emailClient": "client.%d@example.com",
                  "telephoneClient": "+22901020306",
                  "dateArrivee": "%s",
                  "dateDepart": "%s",
                  "nombrePersonnes": 2,
                  "commentaires": "Reservation de test"
                }
                """.formatted(hebergementId, System.nanoTime(), dateArrivee, dateDepart);

        MvcResult createResult = mockMvc.perform(post("/api/reservations-hebergement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hebergementId").value(hebergementId))
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"))
                .andReturn();

        long reservationId = readId(createResult);

        String updatePayload = """
                {
                  "id": %d,
                  "hebergementId": %d,
                  "nomClient": "Client",
                  "prenomClient": "Demo",
                  "emailClient": "client.update.%d@example.com",
                  "telephoneClient": "+22901020306",
                  "dateArrivee": "%s",
                  "dateDepart": "%s",
                  "nombrePersonnes": 3,
                  "statut": "CONFIRMEE",
                  "commentaires": "Reservation confirmee"
                }
                """.formatted(reservationId, hebergementId, System.nanoTime(), dateArrivee, dateDepart);

        mockMvc.perform(put("/api/reservations-hebergement/{id}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId))
                .andExpect(jsonPath("$.statut").value("CONFIRMEE"))
                .andExpect(jsonPath("$.nombrePersonnes").value(3));
    }

    private long firstIdFromArray(String endpoint) throws Exception {
        MvcResult result = mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        if (!root.isArray() || root.isEmpty()) {
            throw new IllegalStateException("Aucune donnee disponible sur " + endpoint + " pour executer le test.");
        }
        return root.get(0).get("id").asLong();
    }

    private long readId(MvcResult result) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get("id").asLong();
    }

    private long createTestHebergement() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String payload = """
                {
                  "nom": "Hebergement test %s",
                  "type": "Hotel",
                  "localisation": "Cotonou",
                  "quartier": "Haie Vive",
                  "description": "Hebergement temporaire pour les tests d'integration.",
                  "prixParNuit": 72.5,
                  "imageUrls": ["/assets/images/test-hebergement.jpg"]
                }
                """.formatted(suffix);

        MvcResult result = mockMvc.perform(post("/api/hebergements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        return readId(result);
    }
}
