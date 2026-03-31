ALTER TABLE public.activites
    ADD COLUMN IF NOT EXISTS categorie_id bigint;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_activites_categorie'
    ) THEN
        ALTER TABLE public.activites
            ADD CONSTRAINT fk_activites_categorie
            FOREIGN KEY (categorie_id) REFERENCES public.categories_activites(id_categorie);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_activites_categorie_id
    ON public.activites (categorie_id);
