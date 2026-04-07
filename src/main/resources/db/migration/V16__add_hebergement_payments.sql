CREATE TABLE paiements_reservation_hebergement (
    id BIGSERIAL PRIMARY KEY,
    reservation_hebergement_id BIGINT NOT NULL UNIQUE,
    provider VARCHAR(30) NOT NULL DEFAULT 'PAYPAL',
    statut VARCHAR(30) NOT NULL DEFAULT 'A_PAYER',
    montant NUMERIC(10, 2) NOT NULL,
    devise VARCHAR(3) NOT NULL DEFAULT 'EUR',
    paypal_order_id VARCHAR(120),
    paypal_capture_id VARCHAR(120),
    paypal_payer_id VARCHAR(120),
    paypal_request_id VARCHAR(120),
    date_paiement TIMESTAMP,
    order_payload TEXT,
    capture_payload TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_paiement_hebergement_reservation
        FOREIGN KEY (reservation_hebergement_id)
        REFERENCES reservations_hebergement(id_reservation)
        ON DELETE CASCADE
);

CREATE INDEX idx_paiements_hebergement_reservation
    ON paiements_reservation_hebergement(reservation_hebergement_id);

CREATE INDEX idx_paiements_hebergement_order
    ON paiements_reservation_hebergement(paypal_order_id);
