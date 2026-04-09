-- V21: Ajouter les colonnes manquantes dans circuits_personnalises
-- (commentaire_admin, date_traitement, motif_refus, circuit_cree_id)
-- qui etaient dans le baseline V1 reecrit mais jamais ajoutees via migration incrementale.

ALTER TABLE public.circuits_personnalises
    ADD COLUMN IF NOT EXISTS commentaire_admin  VARCHAR(5000),
    ADD COLUMN IF NOT EXISTS date_traitement    DATE,
    ADD COLUMN IF NOT EXISTS motif_refus        VARCHAR(5000),
    ADD COLUMN IF NOT EXISTS circuit_cree_id    BIGINT;

-- Contrainte d'unicite sur circuit_cree_id (une demande -> un circuit cree)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'uk3vjxmba0efk7ybmfl2sndfrly'
    ) THEN
        ALTER TABLE public.circuits_personnalises
            ADD CONSTRAINT uk3vjxmba0efk7ybmfl2sndfrly UNIQUE (circuit_cree_id);
    END IF;
END $$;

-- Cle etrangere vers circuits
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fks81xqkfnjx0m7qyg3hs12mq4t'
    ) THEN
        ALTER TABLE public.circuits_personnalises
            ADD CONSTRAINT fks81xqkfnjx0m7qyg3hs12mq4t
            FOREIGN KEY (circuit_cree_id)
            REFERENCES public.circuits(id_circuit)
            ON DELETE SET NULL;
    END IF;
END $$;
