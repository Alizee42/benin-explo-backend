-- ============================================================
-- V1 — Schéma complet Bénin Explo
-- Généré depuis les entités JPA — source unique de vérité
-- ============================================================

-- ------------------------------------------------------------
-- zones
-- ------------------------------------------------------------
CREATE TABLE zones (
    id_zone   BIGSERIAL PRIMARY KEY,
    nom       VARCHAR(255),
    description VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- ------------------------------------------------------------
-- villes
-- ------------------------------------------------------------
CREATE TABLE villes (
    id_ville   BIGSERIAL PRIMARY KEY,
    nom        VARCHAR(255),
    zone_id    BIGINT REFERENCES zones(id_zone),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- ------------------------------------------------------------
-- medias
-- ------------------------------------------------------------
CREATE TABLE medias (
    id_media    BIGSERIAL PRIMARY KEY,
    url         VARCHAR(255),
    type        VARCHAR(255),
    description VARCHAR(255),
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

-- ------------------------------------------------------------
-- categories_activites
-- ------------------------------------------------------------
CREATE TABLE categories_activites (
    id_categorie BIGSERIAL PRIMARY KEY,
    nom          VARCHAR(255),
    description  VARCHAR(5000),
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP
);

-- ------------------------------------------------------------
-- utilisateurs
-- ------------------------------------------------------------
CREATE TABLE utilisateurs (
    id           BIGSERIAL PRIMARY KEY,
    nom          VARCHAR(255),
    prenom       VARCHAR(255),
    email        VARCHAR(255) NOT NULL,
    telephone    VARCHAR(255),
    mot_de_passe VARCHAR(255),
    role         VARCHAR(255),
    date_creation TIMESTAMP(6),
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP,
    CONSTRAINT uk_utilisateurs_email UNIQUE (email)
);

-- ------------------------------------------------------------
-- activites
-- ------------------------------------------------------------
CREATE TABLE activites (
    id_activite        BIGSERIAL PRIMARY KEY,
    nom                VARCHAR(255),
    type               VARCHAR(255) NOT NULL DEFAULT 'ACTIVITE',
    description        VARCHAR(5000),
    ville_id           BIGINT NOT NULL REFERENCES villes(id_ville),
    duree_interne      INTEGER,
    poids              INTEGER,
    difficulte         VARCHAR(255),
    image_principale_id BIGINT REFERENCES medias(id_media),
    categorie_id       BIGINT REFERENCES categories_activites(id_categorie),
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP
);

-- ------------------------------------------------------------
-- circuits
-- ------------------------------------------------------------
CREATE TABLE circuits (
    id_circuit          BIGSERIAL PRIMARY KEY,
    nom                 VARCHAR(255),
    description         VARCHAR(5000),
    resume              VARCHAR(2000),
    duree_indicative    VARCHAR(255),
    prix_indicatif      NUMERIC(38,2),
    formule_proposee    VARCHAR(255),
    actif               BOOLEAN NOT NULL DEFAULT true,
    ville_id            BIGINT NOT NULL REFERENCES villes(id_ville),
    image_principale_id BIGINT REFERENCES medias(id_media),
    img                 TEXT,
    galerie             TEXT,
    programme           TEXT,
    points_forts        TEXT,
    inclus              TEXT,
    non_inclus          TEXT,
    tourisme            TEXT,
    aventures           TEXT,
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP
);

-- ------------------------------------------------------------
-- circuit_activites  (pas d'audit)
-- ------------------------------------------------------------
CREATE TABLE circuit_activites (
    id_circuit_activite BIGSERIAL PRIMARY KEY,
    circuit_id          BIGINT REFERENCES circuits(id_circuit) ON DELETE CASCADE,
    activite_id         BIGINT REFERENCES activites(id_activite),
    ordre               INTEGER,
    jour_indicatif      INTEGER
);

-- ------------------------------------------------------------
-- actualite
-- ------------------------------------------------------------
CREATE TABLE actualite (
    id_actualite        BIGSERIAL PRIMARY KEY,
    titre               VARCHAR(255),
    contenu             VARCHAR(10000),
    resume              VARCHAR(600),
    date_publication    TIMESTAMP(6),
    a_la_une            BOOLEAN NOT NULL DEFAULT false,
    publiee             BOOLEAN NOT NULL DEFAULT true,
    image_url           VARCHAR(1000),
    image_principale_id BIGINT REFERENCES medias(id_media),
    auteur_id           BIGINT REFERENCES utilisateurs(id),
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP
);

-- ------------------------------------------------------------
-- hebergements
-- ------------------------------------------------------------
CREATE TABLE hebergements (
    id_hebergement BIGSERIAL PRIMARY KEY,
    nom            VARCHAR(255),
    type           VARCHAR(255),
    localisation   VARCHAR(255),
    quartier       VARCHAR(255),
    description    VARCHAR(5000),
    prix_par_nuit  NUMERIC(10,2),
    image_urls     TEXT,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP
);

-- ------------------------------------------------------------
-- reservations
-- ------------------------------------------------------------
CREATE TABLE reservations (
    id_reservation       BIGSERIAL PRIMARY KEY,
    nom                  VARCHAR(255),
    prenom               VARCHAR(255),
    email                VARCHAR(255),
    telephone            VARCHAR(255),
    date_reservation     DATE,
    statut               VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE',
    nombre_personnes     INTEGER NOT NULL DEFAULT 1,
    commentaires         VARCHAR(1000),
    reference_reservation VARCHAR(20),
    circuit_id           BIGINT REFERENCES circuits(id_circuit) ON DELETE CASCADE,
    utilisateur_id       BIGINT REFERENCES utilisateurs(id) ON DELETE SET NULL,
    created_at           TIMESTAMP,
    updated_at           TIMESTAMP,
    CONSTRAINT uk_reservations_reference UNIQUE (reference_reservation)
);

-- ------------------------------------------------------------
-- reservations_hebergement
-- ------------------------------------------------------------
CREATE TABLE reservations_hebergement (
    id_reservation        BIGSERIAL PRIMARY KEY,
    id_hebergement        BIGINT REFERENCES hebergements(id_hebergement) ON DELETE CASCADE,
    utilisateur_id        BIGINT REFERENCES utilisateurs(id) ON DELETE SET NULL,
    nom_client            VARCHAR(255),
    prenom_client         VARCHAR(255),
    email_client          VARCHAR(255),
    telephone_client      VARCHAR(255),
    reference_reservation VARCHAR(20),
    date_arrivee          DATE,
    date_depart           DATE,
    nombre_nuits          INTEGER NOT NULL DEFAULT 0,
    nombre_personnes      INTEGER NOT NULL DEFAULT 0,
    prix_total            NUMERIC(10,2) NOT NULL DEFAULT 0,
    statut                VARCHAR(255),
    commentaires          VARCHAR(1000),
    date_creation         DATE,
    created_at            TIMESTAMP,
    updated_at            TIMESTAMP,
    CONSTRAINT uk_reservations_hebergement_reference UNIQUE (reference_reservation)
);

-- ------------------------------------------------------------
-- circuits_personnalises
-- ------------------------------------------------------------
CREATE TABLE circuits_personnalises (
    id                          BIGSERIAL PRIMARY KEY,
    -- Informations client
    nom_client                  VARCHAR(255),
    prenom_client               VARCHAR(255),
    email_client                VARCHAR(255),
    telephone_client            VARCHAR(255),
    message_client              VARCHAR(5000),
    -- Paramètres du circuit
    nombre_jours                INTEGER NOT NULL DEFAULT 0,
    nombre_personnes            INTEGER NOT NULL DEFAULT 0,
    date_creation               DATE,
    date_voyage_souhaitee       DATE,
    -- Options
    avec_hebergement            BOOLEAN NOT NULL DEFAULT false,
    type_hebergement            VARCHAR(255),
    hebergement_id              BIGINT REFERENCES hebergements(id_hebergement),
    date_arrivee_hebergement    DATE,
    date_depart_hebergement     DATE,
    avec_transport              BOOLEAN NOT NULL DEFAULT false,
    type_transport              VARCHAR(255),
    avec_guide                  BOOLEAN NOT NULL DEFAULT false,
    avec_chauffeur              BOOLEAN NOT NULL DEFAULT false,
    pension_complete            BOOLEAN NOT NULL DEFAULT false,
    -- Détail des prix estimés
    prix_activites_estime       NUMERIC(12,2) NOT NULL DEFAULT 0,
    prix_hebergement_estime     NUMERIC(12,2) NOT NULL DEFAULT 0,
    prix_transport_estime       NUMERIC(12,2) NOT NULL DEFAULT 0,
    prix_guide_estime           NUMERIC(12,2) NOT NULL DEFAULT 0,
    prix_chauffeur_estime       NUMERIC(12,2) NOT NULL DEFAULT 0,
    prix_pension_complete_estime NUMERIC(12,2) NOT NULL DEFAULT 0,
    prix_estime                 NUMERIC(38,2),
    devise_prix_estime          VARCHAR(10) NOT NULL DEFAULT 'EUR',
    prix_final                  NUMERIC(38,2),
    -- Traitement admin
    statut                      VARCHAR(255) DEFAULT 'EN_ATTENTE',
    commentaire_admin           VARCHAR(5000),
    date_traitement             DATE,
    motif_refus                 VARCHAR(5000),
    -- Relations
    utilisateur_id              BIGINT REFERENCES utilisateurs(id) ON DELETE SET NULL,
    reference_reservation       VARCHAR(20),
    circuit_cree_id             BIGINT,
    -- Audit
    created_at                  TIMESTAMP,
    updated_at                  TIMESTAMP,
    CONSTRAINT circuits_personnalises_statut_check
        CHECK (statut IN ('EN_ATTENTE','EN_TRAITEMENT','ACCEPTE','REFUSE','TERMINE')),
    CONSTRAINT uk_circuits_personnalises_reference UNIQUE (reference_reservation),
    CONSTRAINT uk_circuits_personnalises_circuit_cree UNIQUE (circuit_cree_id)
);

ALTER TABLE circuits_personnalises
    ADD CONSTRAINT fk_circuits_personnalises_circuit_cree
    FOREIGN KEY (circuit_cree_id) REFERENCES circuits(id_circuit) ON DELETE SET NULL;

-- ------------------------------------------------------------
-- circuit_personnalise_jours  (pas d'audit)
-- ------------------------------------------------------------
CREATE TABLE circuit_personnalise_jours (
    id                     BIGSERIAL PRIMARY KEY,
    circuit_personnalise_id BIGINT NOT NULL REFERENCES circuits_personnalises(id) ON DELETE CASCADE,
    numero_jour            INTEGER NOT NULL,
    zone_id                BIGINT REFERENCES zones(id_zone),
    ville_id               BIGINT REFERENCES villes(id_ville),
    description_jour       VARCHAR(2000)
);

-- ------------------------------------------------------------
-- circuit_perso_jour_activites  (table de jonction, pas d'audit)
-- ------------------------------------------------------------
CREATE TABLE circuit_perso_jour_activites (
    jour_id     BIGINT NOT NULL REFERENCES circuit_personnalise_jours(id) ON DELETE CASCADE,
    activite_id BIGINT NOT NULL REFERENCES activites(id_activite) ON DELETE CASCADE,
    PRIMARY KEY (jour_id, activite_id)
);

-- ------------------------------------------------------------
-- devis
-- ------------------------------------------------------------
CREATE TABLE devis (
    id_devis   BIGSERIAL PRIMARY KEY,
    nom        VARCHAR(255),
    prenom     VARCHAR(255),
    email      VARCHAR(255),
    telephone  VARCHAR(255),
    message    VARCHAR(20000),
    circuit_id BIGINT REFERENCES circuits(id_circuit),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- ------------------------------------------------------------
-- devis_activites  (pas d'audit)
-- ------------------------------------------------------------
CREATE TABLE devis_activites (
    id          BIGSERIAL PRIMARY KEY,
    devis_id    BIGINT REFERENCES devis(id_devis),
    activite_id BIGINT REFERENCES activites(id_activite),
    quantite    INTEGER
);

-- ------------------------------------------------------------
-- tarifs_circuit_personnalise  (pas d'audit, 1 seule ligne)
-- ------------------------------------------------------------
CREATE TABLE tarifs_circuit_personnalise (
    id                                  BIGSERIAL PRIMARY KEY,
    devise                              VARCHAR(10) NOT NULL DEFAULT 'EUR',
    transport_compact_par_jour          NUMERIC(12,2) NOT NULL DEFAULT 0,
    transport_familial_par_jour         NUMERIC(12,2) NOT NULL DEFAULT 0,
    transport_minibus_par_jour          NUMERIC(12,2) NOT NULL DEFAULT 0,
    transport_bus_par_jour              NUMERIC(12,2) NOT NULL DEFAULT 0,
    guide_par_jour                      NUMERIC(12,2) NOT NULL DEFAULT 0,
    chauffeur_par_jour                  NUMERIC(12,2) NOT NULL DEFAULT 0,
    pension_complete_par_personne_par_jour NUMERIC(12,2) NOT NULL DEFAULT 0
);

INSERT INTO tarifs_circuit_personnalise (devise) VALUES ('EUR');

-- ------------------------------------------------------------
-- parametres_site
-- ------------------------------------------------------------
CREATE TABLE parametres_site (
    id_parametres      BIGSERIAL PRIMARY KEY,
    email_contact      VARCHAR(255),
    telephone_contact  VARCHAR(255),
    adresse_agence     VARCHAR(255),
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP
);

-- ------------------------------------------------------------
-- tombola_participants
-- ------------------------------------------------------------
CREATE TABLE tombola_participants (
    id               BIGSERIAL PRIMARY KEY,
    utilisateur_id   BIGINT NOT NULL REFERENCES utilisateurs(id),
    email            VARCHAR(255) NOT NULL,
    nom              VARCHAR(255),
    prenom           VARCHAR(255),
    date_inscription TIMESTAMP(6),
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP
);

-- ------------------------------------------------------------
-- vehicules  (pas d'audit)
-- ------------------------------------------------------------
CREATE TABLE vehicules (
    id         BIGSERIAL PRIMARY KEY,
    marque     VARCHAR(255),
    modele     VARCHAR(255),
    matricule  VARCHAR(255),
    annee      INTEGER NOT NULL DEFAULT 0,
    disponible BOOLEAN NOT NULL DEFAULT true
);

-- ------------------------------------------------------------
-- paiements_reservation_hebergement
-- ------------------------------------------------------------
CREATE TABLE paiements_reservation_hebergement (
    id                        BIGSERIAL PRIMARY KEY,
    reservation_hebergement_id BIGINT NOT NULL UNIQUE
        REFERENCES reservations_hebergement(id_reservation) ON DELETE CASCADE,
    provider                  VARCHAR(30) NOT NULL DEFAULT 'PAYPAL',
    statut                    VARCHAR(30) NOT NULL DEFAULT 'A_PAYER',
    montant                   NUMERIC(10,2) NOT NULL,
    devise                    VARCHAR(3) NOT NULL DEFAULT 'EUR',
    paypal_order_id           VARCHAR(120),
    paypal_capture_id         VARCHAR(120),
    paypal_payer_id           VARCHAR(120),
    paypal_request_id         VARCHAR(120),
    date_paiement             TIMESTAMP,
    order_payload             TEXT,
    capture_payload           TEXT,
    created_at                TIMESTAMP,
    updated_at                TIMESTAMP
);

-- ------------------------------------------------------------
-- paiements_reservation_circuit
-- ------------------------------------------------------------
CREATE TABLE paiements_reservation_circuit (
    id             BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL UNIQUE
        REFERENCES reservations(id_reservation) ON DELETE CASCADE,
    provider          VARCHAR(30) NOT NULL DEFAULT 'PAYPAL',
    statut            VARCHAR(30) NOT NULL DEFAULT 'A_PAYER',
    montant           NUMERIC(10,2) NOT NULL,
    devise            VARCHAR(3) NOT NULL DEFAULT 'EUR',
    paypal_order_id   VARCHAR(120),
    paypal_capture_id VARCHAR(120),
    paypal_payer_id   VARCHAR(120),
    paypal_request_id VARCHAR(120),
    date_paiement     TIMESTAMP,
    order_payload     TEXT,
    capture_payload   TEXT,
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP
);

-- ------------------------------------------------------------
-- paiements_circuit_personnalise
-- ------------------------------------------------------------
CREATE TABLE paiements_circuit_personnalise (
    id                      BIGSERIAL PRIMARY KEY,
    circuit_personnalise_id BIGINT NOT NULL UNIQUE
        REFERENCES circuits_personnalises(id) ON DELETE CASCADE,
    provider                VARCHAR(30) NOT NULL DEFAULT 'PAYPAL',
    statut                  VARCHAR(30) NOT NULL DEFAULT 'A_PAYER',
    montant                 NUMERIC(10,2) NOT NULL,
    devise                  VARCHAR(3) NOT NULL DEFAULT 'EUR',
    paypal_order_id         VARCHAR(120),
    paypal_capture_id       VARCHAR(120),
    paypal_payer_id         VARCHAR(120),
    paypal_request_id       VARCHAR(120),
    date_paiement           TIMESTAMP,
    order_payload           TEXT,
    capture_payload         TEXT,
    created_at              TIMESTAMP,
    updated_at              TIMESTAMP
);

-- ------------------------------------------------------------
-- Index de performance
-- ------------------------------------------------------------
CREATE INDEX idx_villes_zone_id                    ON villes(zone_id);
CREATE INDEX idx_activites_ville_id                ON activites(ville_id);
CREATE INDEX idx_activites_categorie_id            ON activites(categorie_id);
CREATE INDEX idx_circuits_actif                    ON circuits(actif);
CREATE INDEX idx_circuits_ville_id                 ON circuits(ville_id);
CREATE INDEX idx_actualite_publiee                 ON actualite(publiee);
CREATE INDEX idx_reservations_circuit_id           ON reservations(circuit_id);
CREATE INDEX idx_reservations_utilisateur_id       ON reservations(utilisateur_id);
CREATE INDEX idx_reservations_heb_utilisateur_id   ON reservations_hebergement(utilisateur_id);
CREATE INDEX idx_reservations_heb_hebergement_id   ON reservations_hebergement(id_hebergement);
CREATE INDEX idx_cp_utilisateur_id                 ON circuits_personnalises(utilisateur_id);
CREATE INDEX idx_cp_statut                         ON circuits_personnalises(statut);
CREATE INDEX idx_paiements_heb_order               ON paiements_reservation_hebergement(paypal_order_id);
CREATE INDEX idx_paiements_circuit_order           ON paiements_reservation_circuit(paypal_order_id);
CREATE INDEX idx_paiements_cp_order                ON paiements_circuit_personnalise(paypal_order_id);
