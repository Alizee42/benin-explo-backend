ALTER TABLE reservations_hebergement
    ADD COLUMN reference_reservation VARCHAR(20);

UPDATE reservations_hebergement
SET reference_reservation = 'HEB-' || LPAD(id_reservation::text, 6, '0')
WHERE reference_reservation IS NULL;

ALTER TABLE reservations_hebergement
    ALTER COLUMN reference_reservation SET NOT NULL;

ALTER TABLE reservations_hebergement
    ADD CONSTRAINT uk_reservations_hebergement_reference UNIQUE (reference_reservation);
