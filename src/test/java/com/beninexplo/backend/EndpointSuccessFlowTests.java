package com.beninexplo.backend;

import com.beninexplo.backend.entity.Utilisateur;
import com.beninexplo.backend.repository.UtilisateurRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    /** Signature PNG minimale (89 50 4E 47 0D 0A 1A 0A) exigée par MediaService. */
    private static final byte[] PNG_MAGIC_BYTES = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    /** Signature MP4 minimale ('ftyp' box à l'offset 4) exigée par MediaService. */
    private static final byte[] MP4_MAGIC_BYTES = {
            0x00, 0x00, 0x00, 0x18, 0x66, 0x74, 0x79, 0x70, 0x69, 0x73, 0x6F, 0x6D
    };

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * /api/media/upload délègue à Cloudinary (service tiers) : on mocke le bean pour ne pas
     * dépendre d'un vrai compte Cloudinary en test. Uploader n'étant pas final, Mockito
     * (mock-maker-subclass) peut le sous-classer.
     */
    @MockitoBean
    private Cloudinary cloudinary;

    private final List<Path> createdFiles = new ArrayList<>();

    /**
     * getRequiredCurrentUser() (utilisé par les services de réservation) cherche l'utilisateur
     * authentifié par email en base — @WithMockUser seul ne suffit pas, il faut un utilisateur
     * réel dont l'email correspond au "username" simulé.
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
    @SuppressWarnings("unchecked")
    void adminCanUploadMediaImageSuccessfully() throws Exception {
        // /api/media/upload délègue à Cloudinary (MediaService), un service tiers réel qu'on ne
        // veut pas appeler en test : on mocke le bean Cloudinary pour simuler une réponse d'upload.
        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(ObjectUtils.asMap("secure_url", "https://res.cloudinary.com/demo/image/upload/media-success.png"));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "media-success.png",
                MediaType.IMAGE_PNG_VALUE,
                PNG_MAGIC_BYTES
        );

        mockMvc.perform(multipart("/api/media/upload").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("image"))
                .andExpect(jsonPath("$.description").value("media-success.png"))
                .andExpect(jsonPath("$.url").value("https://res.cloudinary.com/demo/image/upload/media-success.png"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @SuppressWarnings("unchecked")
    void adminCanUploadMediaVideoSuccessfully() throws Exception {
        // MediaDTO accepte le type "video" ; MediaService doit router vers Cloudinary avec
        // resource_type=video (et non "image" en dur) et valider les magic bytes MP4/WebM.
        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(ObjectUtils.asMap("secure_url", "https://res.cloudinary.com/demo/video/upload/media-success.mp4"));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "media-success.mp4",
                "video/mp4",
                MP4_MAGIC_BYTES
        );

        mockMvc.perform(multipart("/api/media/upload").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("video"))
                .andExpect(jsonPath("$.description").value("media-success.mp4"))
                .andExpect(jsonPath("$.url").value("https://res.cloudinary.com/demo/video/upload/media-success.mp4"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminUploadingNonImageContentToMediaEndpointIsRejected() throws Exception {
        // MediaService valide désormais les magic bytes réels du fichier, comme
        // ImageStorageServiceImpl le faisait déjà pour le stockage local (supprimé) : un
        // contenu texte déguisé en image/png doit être rejeté avant tout appel à Cloudinary.
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "fake.png",
                MediaType.IMAGE_PNG_VALUE,
                "not-really-an-image".getBytes()
        );

        mockMvc.perform(multipart("/api/media/upload").file(file))
                .andExpect(status().isBadRequest());
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
    @WithMockUser(username = "reservation.tests@example.com", roles = "ADMIN")
    void authenticatedUserCanCreateReservationSuccessfully() throws Exception {
        // POST /api/reservations exige un utilisateur authentifié (SecurityConfig) :
        // ReservationService#create() résout le client via l'utilisateur connecté, pas via le payload.
        ensureMockUserExists("reservation.tests@example.com");
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
    @WithMockUser(username = "reservation.tests@example.com", roles = "ADMIN")
    void adminCanCreateAndUpdateReservationHebergementSuccessfully() throws Exception {
        ensureMockUserExists("reservation.tests@example.com");
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

    @Test
    @WithMockUser(username = "reservation.tests@example.com", roles = "ADMIN")
    void adminCanExposeOnlyBookedRangesForPublicCalendarSuccessfully() throws Exception {
        ensureMockUserExists("reservation.tests@example.com");
        LocalDate bookedStart = LocalDate.now().plusDays(28);
        LocalDate bookedEnd = LocalDate.now().plusDays(31);
        long hebergementId = createTestHebergement();

        String createPayload = """
                {
                  "hebergementId": %d,
                  "nomClient": "Client",
                  "prenomClient": "Visible",
                  "emailClient": "visible.%d@example.com",
                  "telephoneClient": "+22901020307",
                  "dateArrivee": "%s",
                  "dateDepart": "%s",
                  "nombrePersonnes": 2,
                  "commentaires": "Calendrier public"
                }
                """.formatted(hebergementId, System.nanoTime(), bookedStart, bookedEnd);

        mockMvc.perform(post("/api/reservations-hebergement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/reservations-hebergement/indisponibilites/{hebergementId}", hebergementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dateArrivee").value(bookedStart.toString()))
                .andExpect(jsonPath("$[0].dateDepart").value(bookedEnd.toString()))
                .andExpect(jsonPath("$[0].nomClient").doesNotExist())
                .andExpect(jsonPath("$[0].emailClient").doesNotExist());
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
