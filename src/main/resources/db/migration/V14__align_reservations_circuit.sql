-- V14: Align circuit reservations with accommodation reservations
-- Add statut and nombre_personnes to reservations table

ALTER TABLE public.reservations
    ADD COLUMN IF NOT EXISTS statut character varying(50) NOT NULL DEFAULT 'EN_ATTENTE',
    ADD COLUMN IF NOT EXISTS nombre_personnes integer NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS commentaires character varying(1000);

-- Cascade delete: si circuit supprimé, ses réservations sont supprimées
ALTER TABLE public.reservations
    DROP CONSTRAINT IF EXISTS fkotevttb8r1rgi6x67r70fd9kd;

ALTER TABLE public.reservations
    ADD CONSTRAINT fkotevttb8r1rgi6x67r70fd9kd
    FOREIGN KEY (circuit_id) REFERENCES public.circuits(id_circuit)
    ON DELETE CASCADE;

-- Cascade delete: si hébergement supprimé, ses réservations sont supprimées
ALTER TABLE public.reservations_hebergement
    DROP CONSTRAINT IF EXISTS fk1fvlpw5vge2np1bn9qugcyvf7;

ALTER TABLE public.reservations_hebergement
    ADD CONSTRAINT fk1fvlpw5vge2np1bn9qugcyvf7
    FOREIGN KEY (id_hebergement) REFERENCES public.hebergements(id_hebergement)
    ON DELETE CASCADE;
