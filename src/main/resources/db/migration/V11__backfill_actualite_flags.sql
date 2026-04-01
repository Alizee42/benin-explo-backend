-- Rattrapage pour les bases historiques baselines sans ces colonnes.
ALTER TABLE public.actualite
    ADD COLUMN IF NOT EXISTS a_la_une boolean,
    ADD COLUMN IF NOT EXISTS publiee boolean;

UPDATE public.actualite
SET a_la_une = false
WHERE a_la_une IS NULL;

UPDATE public.actualite
SET publiee = true
WHERE publiee IS NULL;

ALTER TABLE public.actualite
    ALTER COLUMN a_la_une SET DEFAULT false,
    ALTER COLUMN a_la_une SET NOT NULL,
    ALTER COLUMN publiee SET DEFAULT true,
    ALTER COLUMN publiee SET NOT NULL;