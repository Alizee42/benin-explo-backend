ALTER TABLE public.reservations
    ADD COLUMN IF NOT EXISTS reference_reservation VARCHAR(20);

UPDATE public.reservations
SET reference_reservation = 'CIR-' || LPAD(id_reservation::text, 6, '0')
WHERE reference_reservation IS NULL;

ALTER TABLE public.reservations
    ALTER COLUMN reference_reservation SET NOT NULL;

ALTER TABLE public.reservations
    DROP CONSTRAINT IF EXISTS uk_reservations_reference;

ALTER TABLE public.reservations
    ADD CONSTRAINT uk_reservations_reference UNIQUE (reference_reservation);

CREATE TABLE IF NOT EXISTS public.paiements_reservation_circuit (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL UNIQUE,
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
    CONSTRAINT fk_paiement_circuit_reservation
        FOREIGN KEY (reservation_id)
        REFERENCES public.reservations(id_reservation)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_paiements_circuit_reservation
    ON public.paiements_reservation_circuit(reservation_id);

CREATE INDEX IF NOT EXISTS idx_paiements_circuit_order
    ON public.paiements_reservation_circuit(paypal_order_id);

ALTER TABLE public.circuits_personnalises
    ADD COLUMN IF NOT EXISTS utilisateur_id BIGINT;

UPDATE public.circuits_personnalises cp
SET utilisateur_id = u.id
FROM public.utilisateurs u
WHERE cp.utilisateur_id IS NULL
  AND cp.email_client IS NOT NULL
  AND lower(trim(cp.email_client)) = lower(trim(u.email));

ALTER TABLE public.circuits_personnalises
    DROP CONSTRAINT IF EXISTS fk_circuits_personnalises_utilisateur;

ALTER TABLE public.circuits_personnalises
    ADD CONSTRAINT fk_circuits_personnalises_utilisateur
    FOREIGN KEY (utilisateur_id)
    REFERENCES public.utilisateurs(id)
    ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_circuits_personnalises_utilisateur_id
    ON public.circuits_personnalises(utilisateur_id);

ALTER TABLE public.circuits_personnalises
    ADD COLUMN IF NOT EXISTS reference_reservation VARCHAR(20);

UPDATE public.circuits_personnalises
SET reference_reservation = 'CPS-' || LPAD(id::text, 6, '0')
WHERE reference_reservation IS NULL;

ALTER TABLE public.circuits_personnalises
    ALTER COLUMN reference_reservation SET NOT NULL;

ALTER TABLE public.circuits_personnalises
    DROP CONSTRAINT IF EXISTS uk_circuits_personnalises_reference;

ALTER TABLE public.circuits_personnalises
    ADD CONSTRAINT uk_circuits_personnalises_reference UNIQUE (reference_reservation);

CREATE TABLE IF NOT EXISTS public.paiements_circuit_personnalise (
    id BIGSERIAL PRIMARY KEY,
    circuit_personnalise_id BIGINT NOT NULL UNIQUE,
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
    CONSTRAINT fk_paiement_circuit_personnalise
        FOREIGN KEY (circuit_personnalise_id)
        REFERENCES public.circuits_personnalises(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_paiements_circuit_personnalise
    ON public.paiements_circuit_personnalise(circuit_personnalise_id);

CREATE INDEX IF NOT EXISTS idx_paiements_circuit_personnalise_order
    ON public.paiements_circuit_personnalise(paypal_order_id);
