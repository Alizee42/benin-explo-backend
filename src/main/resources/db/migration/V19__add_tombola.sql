CREATE TABLE tombolas (
    id          BIGSERIAL PRIMARY KEY,
    titre       VARCHAR(255) NOT NULL,
    description TEXT,
    date_debut  TIMESTAMP NOT NULL,
    date_fin    TIMESTAMP NOT NULL,
    statut      VARCHAR(20) NOT NULL DEFAULT 'EN_COURS',
    prix_ticket NUMERIC(10, 2) NOT NULL DEFAULT 0,
    nombre_tickets_max INTEGER,
    image_url   VARCHAR(500),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_tombola_statut CHECK (statut IN ('EN_COURS', 'TERMINEE', 'ANNULEE')),
    CONSTRAINT ck_tombola_dates  CHECK (date_fin > date_debut)
);

CREATE TABLE participations_tombola (
    id                  BIGSERIAL PRIMARY KEY,
    tombola_id          BIGINT NOT NULL,
    utilisateur_id      BIGINT,
    email               VARCHAR(255) NOT NULL,
    nom                 VARCHAR(100) NOT NULL,
    prenom              VARCHAR(100) NOT NULL,
    numero_ticket       VARCHAR(20) NOT NULL UNIQUE,
    date_participation  TIMESTAMP NOT NULL DEFAULT NOW(),
    est_gagnant         BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_participation_tombola
        FOREIGN KEY (tombola_id) REFERENCES tombolas(id) ON DELETE CASCADE,
    CONSTRAINT fk_participation_utilisateur
        FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE SET NULL,
    CONSTRAINT uk_participation_email_tombola
        UNIQUE (tombola_id, email)
);

CREATE INDEX idx_participations_tombola_id ON participations_tombola(tombola_id);
CREATE INDEX idx_participations_utilisateur_id ON participations_tombola(utilisateur_id);
CREATE INDEX idx_participations_gagnant ON participations_tombola(tombola_id, est_gagnant);
