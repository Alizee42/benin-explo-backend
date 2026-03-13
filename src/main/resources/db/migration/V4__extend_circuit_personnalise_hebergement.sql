ALTER TABLE public.circuits_personnalises
    ADD COLUMN IF NOT EXISTS hebergement_id bigint,
    ADD COLUMN IF NOT EXISTS date_arrivee_hebergement date,
    ADD COLUMN IF NOT EXISTS date_depart_hebergement date,
    ADD COLUMN IF NOT EXISTS devise_prix_estime character varying(10);

UPDATE public.circuits_personnalises
SET devise_prix_estime = COALESCE(devise_prix_estime, 'XOF')
WHERE devise_prix_estime IS NULL;

ALTER TABLE public.circuits_personnalises
    ALTER COLUMN devise_prix_estime SET DEFAULT 'XOF';

ALTER TABLE public.circuits_personnalises
    ALTER COLUMN devise_prix_estime SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_circuits_personnalises_hebergement'
    ) THEN
        ALTER TABLE public.circuits_personnalises
            ADD CONSTRAINT fk_circuits_personnalises_hebergement
            FOREIGN KEY (hebergement_id)
            REFERENCES public.hebergements(id_hebergement);
    END IF;
END $$;
